"""Анимация надевания маски от первого лица — сцена Blender.

Запуск (нужен модуль bpy или Blender 4.2+/5.x):
    python tools/blender/put_on_animation.py            # сцена + экспорт + превью
    blender -b -P tools/blender/put_on_animation.py     # то же из самого Blender

Что делает:
  1. строит сцену: камера = глаза игрока (FOV 70°, как рука в Minecraft), рука — в тех же
     координатах, что и рука Minecraft от первого лица, маска — из mask_mesh.json;
  2. ставит ключевые кадры: рука поднимает маску к лицу, разворачивает, прижимает, маска «шлёпается»;
  3. экспортирует кадры для мода: assets/miragemask/animations/put_on_first_person.json
     (на каждый тик: матрица руки и матрица маски в пространстве камеры);
  4. рендерит превью (Cycles) и сохраняет .blend, чтобы анимацию можно было править руками.

Система координат сцены = пространство камеры Minecraft: x вправо, y вверх, камера смотрит в -z.
"""
import json
import math
import os
import sys

import bpy
from mathutils import Matrix, Quaternion, Vector

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.normpath(os.path.join(HERE, "..", ".."))
MESH = os.path.join(ROOT, "src", "main", "resources", "assets", "miragemask", "models", "mask_mesh.json")
ANIM_OUT = os.path.join(ROOT, "src", "main", "resources", "assets", "miragemask", "animations", "put_on_first_person.json")
PREVIEW_DIR = os.path.join(ROOT, "docs", "blender")
BLEND_OUT = os.path.join(HERE, "mask_put_on.blend")
RENDER = "--no-render" not in sys.argv

USE_TICKS = 20          # = MirageMaskItem.PUT_ON_TICKS, 1 кадр = 1 тик
ARM_LEN = 0.75          # длина руки Minecraft (12 px)


def T(x, y, z):
    return Matrix.Translation((x, y, z))


def R(deg, axis):
    return Matrix.Rotation(math.radians(deg), 4, axis)


# Положение руки Minecraft от первого лица в покое (ItemInHandRenderer.renderPlayerArm, правая рука)
A0 = T(0.64, -0.6, -0.72) @ R(45, "Y") @ T(-1, 3.6, 3.5) @ R(120, "Z") @ R(200, "X") @ R(-135, "Y") @ T(5.6, 0, 0)
SHOULDER_LOCAL = Vector((-0.375, 0.0, 0.0))     # в координатах руки (как renderRightHand)
HAND_LOCAL = Vector((-0.375, ARM_LEN, 0.0))


# ---------------------------------------------------------------- сцена
def reset():
    bpy.ops.wm.read_factory_settings(use_empty=True)
    sc = bpy.context.scene
    sc.render.fps = 20
    sc.frame_start, sc.frame_end = 0, USE_TICKS + 10
    return sc


def material(name, color, emission=None, strength=0.0, alpha=1.0, transmission=0.0):
    m = bpy.data.materials.new(name)
    m.use_nodes = True
    bsdf = m.node_tree.nodes.get("Principled BSDF")
    bsdf.inputs["Base Color"].default_value = (*color, 1)
    bsdf.inputs["Roughness"].default_value = 0.15
    if emission:
        bsdf.inputs["Emission Color"].default_value = (*emission, 1)
        bsdf.inputs["Emission Strength"].default_value = strength
    if alpha < 1:
        bsdf.inputs["Alpha"].default_value = alpha
    if transmission:
        bsdf.inputs["Transmission Weight"].default_value = transmission
        bsdf.inputs["IOR"].default_value = 1.2
    return m


def mesh_object(name, verts, faces, mats, face_mats):
    me = bpy.data.meshes.new(name)
    me.from_pydata(verts, [], faces)
    for m in mats:
        me.materials.append(m)
    for poly, mi in zip(me.polygons, face_mats):
        poly.material_index = mi
    me.update()
    ob = bpy.data.objects.new(name, me)
    bpy.context.scene.collection.objects.link(ob)
    return ob


def build_mask():
    data = json.load(open(MESH))
    verts, faces, fm = [], [], []

    def add(poly, mi):
        base = len(verts)
        for p in poly:
            verts.append((p[0] / 16, p[1] / 16, p[2] / 16))   # пиксели -> блоки, оси как в ModelPart
        faces.append(list(range(base, base + len(poly))))
        fm.append(mi)

    for cell in data["cells"]:
        for g in cell["glass"]:
            add(g["tri"], 0)
        for q in cell["edges"]:
            add(q, 1)
    for q in data["rim"]:
        add(q, 1)
    for clip in data["clips"]:
        for f in clip:
            add(f["quad"], 2)
    for q in data["bars"]:
        add(q, 1)
    mats = [material("Glass", (0.62, 0.66, 0.85), alpha=0.55, transmission=0.6),
            material("Glow", (0.9, 0.93, 1.0), emission=(0.85, 0.9, 1.0), strength=9.0),
            material("Clip", (0.06, 0.06, 0.08))]
    return mesh_object("Mask", verts, faces, mats, fm)


def build_arm():
    # рука Minecraft (широкая модель): pivot (-5, 2, 0) + куб (-3,-2,-2)..(1,10,2) => x -8..-4, y 0..12, z -2..2 (px)
    x0, x1, y0, y1, z0, z1 = -8 / 16, -4 / 16, 0.0, 12 / 16, -2 / 16, 2 / 16
    ys = 7 / 16  # рукав до сюда, дальше кисть
    verts, faces, fm = [], [], []

    def box(a, b, c, d, e, f, mi):
        base = len(verts)
        for x in (a, b):
            for y in (c, d):
                for z in (e, f):
                    verts.append((x, y, z))
        for q in ((0, 1, 3, 2), (4, 6, 7, 5), (0, 4, 5, 1), (2, 3, 7, 6), (0, 2, 6, 4), (1, 5, 7, 3)):
            faces.append([base + i for i in q])
            fm.append(mi)

    box(x0, x1, y0, ys, z0, z1, 0)
    box(x0 + 0.002, x1 - 0.002, ys, y1, z0 + 0.002, z1 - 0.002, 1)
    mats = [material("Sleeve", (0.05, 0.05, 0.07)), material("Skin", (0.78, 0.58, 0.45))]
    return mesh_object("Arm", verts, faces, mats, fm)


def setup_camera_and_world(sc):
    cam_data = bpy.data.cameras.new("Eyes")
    cam_data.sensor_fit = "VERTICAL"
    cam_data.angle = math.radians(70)            # FOV руки в Minecraft
    cam_data.clip_start = 0.01
    cam = bpy.data.objects.new("Eyes", cam_data)
    sc.collection.objects.link(cam)
    cam.matrix_world = Matrix.Identity(4)        # в начале координат, смотрит в -z, вверх +y
    sc.camera = cam
    world = bpy.data.worlds.new("Night")
    world.use_nodes = True
    world.node_tree.nodes["Background"].inputs["Color"].default_value = (0.05, 0.07, 0.12, 1)
    world.node_tree.nodes["Background"].inputs["Strength"].default_value = 0.6
    sc.world = world
    key = bpy.data.lights.new("Key", "AREA")
    key.energy = 60
    key.size = 2
    lo = bpy.data.objects.new("Key", key)
    lo.location = (1.5, 1.5, 1.0)
    lo.rotation_euler = (math.radians(-40), math.radians(40), 0)
    sc.collection.objects.link(lo)
    # задник: «трава и небо» далеко за маской
    bpy.ops.mesh.primitive_plane_add(size=40, location=(0, 0, -12))
    bg = bpy.context.active_object
    bg.name = "Backdrop"
    bg.data.materials.append(material("Backdrop", (0.12, 0.2, 0.32)))
    bpy.ops.mesh.primitive_plane_add(size=40, location=(0, -3, -10), rotation=(math.radians(90), 0, 0))
    gr = bpy.context.active_object
    gr.name = "Ground"
    gr.data.materials.append(material("Grass", (0.12, 0.25, 0.08)))
    return cam


# ---------------------------------------------------------------- позы
def arm_pose(hand_target, direction, roll=0.0):
    """Матрица руки: кисть в hand_target, рука вдоль direction (от плеча к кисти), с доворотом roll."""
    rest_axis = (A0.to_3x3() @ Vector((0, 1, 0))).normalized()
    q = rest_axis.rotation_difference(Vector(direction).normalized())
    rot = q.to_matrix().to_4x4() @ A0.to_3x3().to_4x4()
    rot = Quaternion(Vector(direction).normalized(), math.radians(roll)).to_matrix().to_4x4() @ rot
    m = rot.copy()
    hand_world_offset = m.to_3x3() @ HAND_LOCAL
    m.translation = Vector(hand_target) - hand_world_offset
    return m


MASK_CENTER_PX = Vector((0.0, -3.35, -5.2))
MASK_SCALE = 0.78

# Конечная поза: маска прямо перед глазами лицевой стороной к камере, кисть держит её снизу-справа
HAND_END = Vector((0.17, -0.19, -0.27))
DIR_END = Vector((-0.30, 0.62, -0.30))
MASK_END_POS = Vector((0.0, -0.035, -0.30))


def mask_end_world():
    return (Matrix.Translation(MASK_END_POS) @ R(180, "X") @ Matrix.Scale(MASK_SCALE, 4)
            @ Matrix.Translation(-MASK_CENTER_PX / 16))


# ключевые кадры: (тик, кисть, направление руки, доворот, сжатие маски по глубине)
KEYS = [
    (0, (1.05, -1.02, -0.92), (0.30, 0.55, -0.78), -20, 1.00),   # рука ниже кадра справа, маска в ладони
    (5, (0.55, -0.48, -0.78), (0.10, 0.75, -0.65), -15, 1.00),   # подъём
    (10, (0.32, -0.26, -0.50), (-0.18, 0.72, -0.52), -25, 1.00), # маска у лица, ещё под углом
    (14, (0.19, -0.20, -0.30), (-0.28, 0.64, -0.34), -8, 1.00),  # разворот лицом к камере
    (17, (0.17, -0.18, -0.21), (-0.30, 0.62, -0.30), 0, 0.72),   # прижал — маска сплющивается о лицо
    (20, (0.17, -0.19, -0.24), (-0.30, 0.62, -0.30), 0, 0.92),   # отскок, маска прилипла
]


def animate(arm, mask):
    arm.rotation_mode = "QUATERNION"
    mask.rotation_mode = "QUATERNION"
    mask.parent = arm
    f_end = arm_pose(HAND_END, DIR_END)
    grip = f_end.inverted() @ mask_end_world()       # как маска лежит в ладони (постоянно)
    for tick, hand, direction, roll, squash in KEYS:
        arm.matrix_world = arm_pose(hand, direction, roll)
        arm.keyframe_insert("location", frame=tick)
        arm.keyframe_insert("rotation_quaternion", frame=tick)
        # сжатие по глубине маски вокруг её центра (лицевая ось маски = локальная z)
        center = Matrix.Translation(MASK_CENTER_PX / 16)
        squash_m = center @ Matrix.Diagonal((1 + (1 - squash) * 0.35, 1 - (1 - squash) * 0.15, squash, 1)) @ center.inverted()
        mask.matrix_basis = grip @ squash_m
        mask.keyframe_insert("location", frame=tick)
        mask.keyframe_insert("rotation_quaternion", frame=tick)
        mask.keyframe_insert("scale", frame=tick)
    for ob in (arm, mask):
        if ob.animation_data and ob.animation_data.action:
            for fc in ob.animation_data.action.fcurves if hasattr(ob.animation_data.action, "fcurves") else []:
                for kp in fc.keyframe_points:
                    kp.interpolation = "BEZIER"
                    kp.easing = "AUTO"


def export(sc, arm, mask):
    frames = []
    for f in range(0, USE_TICKS + 1):
        sc.frame_set(f)
        frames.append({"arm": [round(v, 6) for row in arm.matrix_world for v in row],
                       "mask": [round(v, 6) for row in mask.matrix_world for v in row]})
    os.makedirs(os.path.dirname(ANIM_OUT), exist_ok=True)
    with open(ANIM_OUT, "w") as fh:
        json.dump({"ticks": USE_TICKS, "space": "first_person_camera", "layout": "row_major",
                   "arm_model": "renderRightHand", "frames": frames}, fh, separators=(",", ":"))
    print("exported", ANIM_OUT, len(frames), "frames")


def render(sc, cam, arm, mask):
    os.makedirs(PREVIEW_DIR, exist_ok=True)
    sc.render.engine = "CYCLES"
    sc.cycles.device = "CPU"
    sc.cycles.samples = 24
    sc.cycles.use_denoising = False
    sc.render.resolution_x, sc.render.resolution_y = 960, 540
    sc.render.film_transparent = False
    sc.render.image_settings.file_format = "PNG"
    frames_dir = os.path.join(PREVIEW_DIR, "frames")
    os.makedirs(frames_dir, exist_ok=True)
    for f in range(0, USE_TICKS + 6):
        sc.frame_set(min(f, USE_TICKS))
        sc.render.filepath = os.path.join(frames_dir, f"put_on_{f:03d}.png")
        bpy.ops.render.render(write_still=True)
    # «студийные» кадры маски — спереди и сбоку, как на референсе
    arm.hide_render = True
    for name, rot in (("mask_front", R(180, "X")), ("mask_side", R(180, "X") @ R(70, "Y"))):
        mask.parent = None
        mask.animation_data_clear()
        mask.matrix_world = (Matrix.Translation((0, 0, -1.25)) @ rot @ Matrix.Scale(1.6, 4)
                             @ Matrix.Translation(-MASK_CENTER_PX / 16))
        sc.render.filepath = os.path.join(PREVIEW_DIR, name + ".png")
        bpy.ops.render.render(write_still=True)


if __name__ == "__main__":
    sc = reset()
    cam = setup_camera_and_world(sc)
    arm = build_arm()
    mask = build_mask()
    animate(arm, mask)
    export(sc, arm, mask)
    bpy.ops.wm.save_as_mainfile(filepath=BLEND_OUT)
    if RENDER:
        render(sc, cam, arm, mask)
    print("done")
