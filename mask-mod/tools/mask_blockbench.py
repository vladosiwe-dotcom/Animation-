"""Маска в стиле Blockbench: воксельная модель из кубов + пиксель-арт текстуры.

python3 tools/mask_blockbench.py  создаёт:
  src/main/resources/assets/miragemask/geo/mirage_mask.json        — модель (формат Java Block/Item, открывается в Blockbench)
  src/main/resources/assets/miragemask/textures/entity/mirage_mask.png       — текстура 64x64
  src/main/resources/assets/miragemask/textures/entity/mirage_mask_glow.png  — свечение (те же UV)
  src/main/resources/assets/miragemask/textures/item/mirage_mask.png         — иконка 32x32
  tools/blockbench/mirage_mask.bbmodel                              — проект Blockbench (текстура внутри)

Координаты модели как в Blockbench: 1 = пиксель, y вверх. Голова игрока = куб (4,0,4)-(12,8,12),
лицо — грань z = 4 (north), подбородок y = 0, макушка y = 8.
Перевод в координаты головы ModelPart: (x - 8, -y, z - 8).
"""
import base64
import io
import json
import math
import os
import uuid

from PIL import Image

ROOT = os.path.normpath(os.path.join(os.path.dirname(__file__), ".."))
RES = os.path.join(ROOT, "src", "main", "resources", "assets", "miragemask")

# ---------------------------------------------------------------- форма маски
CX = 8.0
Y_CHIN, Y_TOP = 0.45, 7.35


def half_width(y):
    """Полуширина силуэта: узкий подбородок, широкие скулы, скруглённый лоб."""
    t = (y - Y_CHIN) / (Y_TOP - Y_CHIN)
    if t < 0 or t > 1:
        return -1
    if t < 0.55:
        return 1.7 + 2.05 * math.sin(t / 0.55 * math.pi / 2)
    k = (t - 0.55) / 0.45
    return 3.75 * math.sqrt(max(0.0, 1 - k ** 3))


def inside(x, y):
    return abs(x - CX) <= half_width(y)


def edge_dist(x, y):
    """Приблизительное расстояние до края силуэта (в пикселях модели), >0 внутри."""
    best = 9.0
    for a in range(0, 360, 6):
        dx, dy = math.cos(math.radians(a)), math.sin(math.radians(a))
        for s in (0.1, 0.2, 0.3, 0.45, 0.6, 0.8, 1.0, 1.3):
            if not inside(x + dx * s, y + dy * s):
                best = min(best, s)
                break
    return best if inside(x, y) else -1


EYES = [(6.05, 4.55), (9.95, 4.55)]


def eye_val(x, y):
    """<1 внутри линзы (миндалевидный эллипс, внешний уголок чуть выше)."""
    best = 9
    for ex, ey in EYES:
        tilt = 0.12 * (1 if ex > CX else -1)
        dx, dy = x - ex, (y - ey) - tilt * (x - ex)
        best = min(best, (dx / 1.45) ** 2 + (dy / 0.78) ** 2)
    return best


HEX_R = 2.15
HEX_ORIGIN = (8.0, 3.9)


def hex_edge(x, y):
    """Расстояние до ближайшего ребра сот (flat-top)."""
    a = HEX_R * math.sqrt(3) / 2
    best = 9
    col_w, row_h = 1.5 * HEX_R, 2 * a
    c0 = round((x - HEX_ORIGIN[0]) / col_w)
    for c in range(c0 - 1, c0 + 2):
        cx = HEX_ORIGIN[0] + c * col_w
        off = a if c % 2 else 0
        r0 = round((y - HEX_ORIGIN[1] - off) / row_h)
        for r in range(r0 - 1, r0 + 2):
            cy = HEX_ORIGIN[1] + off + r * row_h
            px, py = abs(x - cx), abs(y - cy)
            h = max(py, (py + math.sqrt(3) * px) / 2)
            best = min(best, a - h) if h <= a else best
    return best


def hex_facet(x, y):
    """Номер грани (0..5) внутри соты — для «гранёного» стекла."""
    a = HEX_R * math.sqrt(3) / 2
    col_w = 1.5 * HEX_R
    c = round((x - HEX_ORIGIN[0]) / col_w)
    cx = HEX_ORIGIN[0] + c * col_w
    off = a if c % 2 else 0
    r = round((y - HEX_ORIGIN[1] - off) / (2 * a))
    cy = HEX_ORIGIN[1] + off + r * 2 * a
    return int(((math.degrees(math.atan2(y - cy, x - cx)) + 360) % 360) // 60), (c * 7 + r * 3) % 5


def shade_pixel(x, y, scale):
    """Цвет пикселя текстуры в точке (x, y) модели. scale — пикселей текстуры на пиксель модели."""
    px = 1.0 / scale
    if not inside(x, y):
        return (0, 0, 0, 0), (0, 0, 0, 0)
    ed = edge_dist(x, y)
    edge_tint = 18 if ed < 1.05 * px else 0                         # край чуть светлее, без обводки
    he = hex_edge(x, y)
    if he < 0.42 * px:
        return (240, 244, 255, 255), (255, 255, 255, 255)          # ребро соты
    if he < 1.15 * px:
        return (168, 182, 245, 190), (110, 130, 255, 90)          # ореол ребра
    facet, cell = hex_facet(x, y)
    base = 150 + [14, 4, -10, -16, -4, 10][facet] + cell * 3
    streak = 34 if abs((x - 4.5) - (7.6 - y) * 0.9) < 0.75 else 0  # диагональный блик, как на референсе
    v = base + streak + edge_tint
    return (int(v * 0.86), int(v * 0.9), min(255, int(v * 1.22)), 112 + streak * 2), (0, 0, 0, 0)


# ---------------------------------------------------------------- текстура 64x64
TEX = 64
U = TEX / 16  # пикселей текстуры на единицу UV модели


def paint_texture():
    img = Image.new("RGBA", (TEX, TEX), (0, 0, 0, 0))
    glow = Image.new("RGBA", (TEX, TEX), (0, 0, 0, 0))
    # лицевая область: x 2..14 -> u 0..12 (u = 14 - x), y 8..0 -> v 0..8 (v = 8 - y); 4 px текстуры на пиксель модели
    for py in range(32):
        for pxl in range(48):
            x = 14 - (pxl + 0.5) / 4
            y = 8 - (py + 0.5) / 4
            c, g = shade_pixel(x, y, 4)
            img.putpixel((pxl, py), c)
            glow.putpixel((pxl, py), g)
    fill = lambda im, x0, y0, x1, y1, c: [im.putpixel((i, j), c) for i in range(x0, x1) for j in range(y0, y1)]
    fill(img, 48, 0, 52, 4, (252, 253, 255, 255)); fill(glow, 48, 0, 52, 4, (255, 255, 255, 255))   # обод (верх/низ полосок)
    fill(img, 52, 0, 56, 4, (170, 182, 230, 120))                                                    # торцы стекла
    fill(img, 52, 0, 56, 1, (220, 228, 255, 170))
    fill(img, 48, 4, 52, 8, (26, 26, 34, 255))                                                       # тёмный пластик
    fill(img, 48, 4, 52, 5, (44, 44, 56, 255))
    # боковая грань крепления: тёмная, со светящейся вертикальной полоской (u 12..16, v 2..5 -> px 48..64, 8..20)
    fill(img, 48, 8, 64, 20, (30, 30, 40, 255))
    fill(img, 48, 8, 64, 9, (48, 48, 62, 255))
    for bx in (51, 60):
        fill(img, bx, 10, bx + 2, 18, (250, 252, 255, 255)); fill(glow, bx, 10, bx + 2, 18, (255, 255, 255, 255))
    # задняя грань крепления (u 12..13, v 5..8 -> px 48..52, 20..32)
    fill(img, 48, 20, 52, 32, (30, 30, 40, 255))
    fill(img, 49, 22, 51, 30, (250, 252, 255, 255)); fill(glow, 49, 22, 51, 30, (255, 255, 255, 255))
    return img, glow


def paint_icon():
    """Иконка 32x32: маска анфас, тёмная обводка, крепления по бокам."""
    S = 32
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    scale = 3.0                        # пикселей иконки на пиксель модели
    ox, oy = S / 2 - CX * scale, S / 2 + 3.95 * scale
    for py in range(S):
        for pxl in range(S):
            x = (pxl + 0.5 - ox) / scale
            y = (oy - (py + 0.5)) / scale
            # на иконке смотрим на маску спереди: левая сторона картинки = +x модели
            c, _ = shade_pixel(2 * CX - x, y, scale)
            if c[3]:
                if sum(c[:3]) > 690:                        # рёбра и обод — белые
                    img.putpixel((pxl, py), (250, 252, 255, 255))
                else:                                       # стекло темнее и непрозрачно — контраст на светлом слоте
                    img.putpixel((pxl, py), (int(c[0] * 0.62), int(c[1] * 0.64), int(c[2] * 0.78), 255))
    # тёмная обводка по контуру
    a = img.copy()
    for py in range(S):
        for pxl in range(S):
            if a.getpixel((pxl, py))[3] == 0 and any(
                    0 <= pxl + dx < S and 0 <= py + dy < S and a.getpixel((pxl + dx, py + dy))[3] > 0
                    for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                img.putpixel((pxl, py), (14, 14, 20, 255))
    # крепления
    for x0 in (3, 26):
        for py in range(12, 21):
            for pxl in range(x0, x0 + 3):
                if img.getpixel((pxl, py))[3] < 200:
                    img.putpixel((pxl, py), (30, 30, 40, 255))
        for py in range(14, 19):
            img.putpixel((x0 + 1 if x0 < 10 else x0 + 1, py), (245, 248, 255, 255))
    return img


# ---------------------------------------------------------------- геометрия (кубы)
def depth(x, part):
    """Передняя поверхность (z) полоски: середина вперёд, края назад; лоб и подбородок уходят назад."""
    nx = (x - CX) / 4.5
    z = 4.0 - (0.25 + 0.8 * max(0.0, 1 - nx * nx))          # плотнее к лицу
    return z + {"brow": 0.25, "mid": 0.0, "chin": 0.35}[part]


def elements():
    els = []
    seg = [("chin", Y_CHIN, 2.4), ("mid", 2.4, 6.0), ("brow", 6.0, Y_TOP)]
    for xi in range(4, 12):
        x0, x1 = float(xi), float(xi + 1)
        xs = [x0 + 0.05 + 0.1 * k for k in range(10)]
        ys = [y / 20 for y in range(int(Y_CHIN * 20), int(Y_TOP * 20) + 1)]
        col = [y for y in ys if any(inside(x, y) for x in xs)]
        if not col:
            continue
        ylo, yhi = min(col), max(col) + 0.05
        for i, (part, s0, s1) in enumerate(seg):
            a, b = max(ylo, s0), min(yhi, s1)
            if b - a < 0.05:
                continue
            zf = round(depth((x0 + x1) / 2, part), 3)
            els.append(cube(f"glass_{xi}_{part}", [x0, round(a, 3), zf], [x1, round(b, 3), round(zf + 0.45, 3)],
                            "glass", top_rim=(b >= yhi - 0.01), bottom_rim=(a <= ylo + 0.01)))
    # крепления по бокам: от края маски назад к ушам
    for s, (x0, x1) in (("right", (3.2, 3.95)), ("left", (12.05, 12.8))):
        els.append(cube(f"clip_{s}", [x0, 3.3, 3.9], [x1, 5.6, 8.4], "clip", outer="west" if s == "right" else "east"))
    return els


def front_uv(x0, y0, x1, y1):
    # north: u0 у x1 (левый край, если смотреть на маску спереди), v0 у верха
    return [14 - x1, 8 - y1, 14 - x0, 8 - y0]


def cube(name, frm, to, kind, top_rim=False, bottom_rim=False, outer=None):
    x0, y0, z0 = frm
    x1, y1, z1 = to
    rim = [12.1, 0.1, 12.9, 0.9]
    edge = [13.1, 0.1, 13.9, 0.9]
    dark = [12.1, 1.1, 12.9, 1.9]
    if kind == "glass":
        fuv = front_uv(x0, y0, x1, y1)
        e = 0.12  # торцы берут узкую полоску лицевой текстуры у своего края: цвет и прозрачность совпадают с силуэтом
        faces = {"north": fuv, "south": [fuv[2], fuv[1], fuv[0], fuv[3]],
                 "east": [14 - x1 + e, 8 - y1, 14 - x1, 8 - y0],
                 "west": [14 - x0, 8 - y1, 14 - x0 - e, 8 - y0],
                 "up": [14 - x0, 8 - y1, 14 - x1, 8 - y1 + e],
                 "down": [14 - x0, 8 - y0 - e, 14 - x1, 8 - y0]}
    else:
        side = [12, 2, 16, 5]
        faces = {"north": dark, "south": [12, 5, 13, 8], "up": dark, "down": dark,
                 "east": side if outer == "east" else dark, "west": side if outer == "west" else dark}
    return {"name": name, "from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to],
            "faces": {k: {"uv": [round(u, 3) for u in v], "texture": "#mask"} for k, v in faces.items()}}


# ---------------------------------------------------------------- запись
def png_bytes(img):
    b = io.BytesIO()
    img.save(b, "PNG")
    return b.getvalue()


def main():
    img, glow = paint_texture()
    icon = paint_icon()
    els = elements()
    os.makedirs(os.path.join(RES, "geo"), exist_ok=True)
    os.makedirs(os.path.join(RES, "textures", "entity"), exist_ok=True)
    os.makedirs(os.path.join(RES, "textures", "item"), exist_ok=True)
    img.save(os.path.join(RES, "textures", "entity", "mirage_mask.png"))
    glow.save(os.path.join(RES, "textures", "entity", "mirage_mask_glow.png"))
    icon.save(os.path.join(RES, "textures", "item", "mirage_mask.png"))
    model = {
        "credit": "Mirage Mask — made with tools/mask_blockbench.py (open in Blockbench: Java Block/Item)",
        "texture_size": [TEX, TEX],
        "textures": {"mask": "miragemask:entity/mirage_mask", "particle": "miragemask:item/mirage_mask"},
        "elements": els,
        "display": {},
        "mirage_mask": {"head_cube": [4, 0, 4, 12, 8, 12], "to_head_space": "(x-8, -y, z-8)", "center": [8, 3.9, 2.9]},
    }
    with open(os.path.join(RES, "geo", "mirage_mask.json"), "w") as f:
        json.dump(model, f, indent=1)

    # проект Blockbench (.bbmodel): те же кубы, текстура встроена; UV в пикселях текстуры
    tex_uuid = str(uuid.uuid4())
    bb_elements = []
    for e in els:
        bb_elements.append({
            "name": e["name"], "type": "cube", "uuid": str(uuid.uuid4()), "box_uv": False, "rescale": False,
            "from": e["from"], "to": e["to"], "origin": [8, 4, 8], "color": 0,
            "faces": {k: {"uv": [u * U for u in v["uv"]], "texture": 0} for k, v in e["faces"].items()},
        })
    bb = {
        "meta": {"format_version": "4.10", "model_format": "java_block", "box_uv": False},
        "name": "mirage_mask", "parent": "", "ambientocclusion": False, "front_gui_light": False,
        "visible_box": [1, 1, 0], "resolution": {"width": TEX, "height": TEX},
        "elements": bb_elements,
        "outliner": [{"name": "mask", "origin": [8, 4, 8], "uuid": str(uuid.uuid4()), "export": True,
                      "isOpen": True, "visibility": True, "children": [b["uuid"] for b in bb_elements]}],
        "textures": [{"path": "", "name": "mirage_mask.png", "folder": "entity", "namespace": "miragemask",
                      "id": "0", "particle": True, "render_mode": "default", "visible": True, "mode": "bitmap",
                      "saved": True, "uuid": tex_uuid, "relative_path": "../../src/main/resources/assets/miragemask/textures/entity/mirage_mask.png",
                      "width": TEX, "height": TEX, "uv_width": TEX, "uv_height": TEX,
                      "source": "data:image/png;base64," + base64.b64encode(png_bytes(img)).decode()}],
    }
    os.makedirs(os.path.join(ROOT, "tools", "blockbench"), exist_ok=True)
    with open(os.path.join(ROOT, "tools", "blockbench", "mirage_mask.bbmodel"), "w") as f:
        json.dump(bb, f, indent=1)

    # превью текстур (увеличенно) для проверки
    prev = Image.new("RGBA", (64 * 8 + 32 * 8 + 16, 64 * 8), (40, 40, 52, 255))
    prev.alpha_composite(img.resize((512, 512), Image.NEAREST), (0, 0))
    prev.alpha_composite(icon.resize((256, 256), Image.NEAREST), (528, 0))
    prev.save(os.path.join(ROOT, "docs", "blockbench_textures.png"))
    print(f"elements={len(els)} -> geo/mirage_mask.json, textures, icon, tools/blockbench/mirage_mask.bbmodel")


if __name__ == "__main__":
    main()
