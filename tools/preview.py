"""Грубый софт-рендер block_display-ригов (без Minecraft) — для проверки тел и поз.

python3 tools/preview.py            -> preview/*.png (персонажи в позах)
python3 tools/preview.py --story    -> preview/story.gif (вся анимация, упрощённо)
"""
import math
import os
import sys

from PIL import Image, ImageDraw

sys.path.insert(0, os.path.dirname(__file__))
from engine import apply, chain, S  # noqa: E402

COLORS = {
    "white_terracotta": (210, 178, 161), "black_concrete": (12, 13, 18), "gray_concrete": (55, 58, 62),
    "white_concrete": (207, 213, 214), "orange_concrete": (224, 97, 1), "brown_terracotta": (77, 51, 36),
    "purple_concrete": (100, 32, 156), "magenta_concrete": (169, 48, 159), "white_wool": (234, 236, 237),
    "pink_concrete": (214, 101, 143), "pink_terracotta": (162, 78, 79), "light_blue_concrete": (36, 137, 199),
    "blue_concrete": (45, 47, 143), "dark_oak_planks": (67, 43, 20), "iron_block": (220, 220, 220),
    "polished_blackstone": (53, 49, 57), "green_concrete": (73, 91, 36), "red_concrete": (142, 33, 33),
    "packed_ice": (141, 180, 250), "blue_ice": (116, 167, 253),
}
FACES = [((0, 1, 3, 2), (-1, 0, 0)), ((4, 6, 7, 5), (1, 0, 0)), ((0, 4, 5, 1), (0, -1, 0)),
         ((2, 3, 7, 6), (0, 1, 0)), ((0, 2, 6, 4), (0, 0, -1)), ((1, 5, 7, 3), (0, 0, 1))]
CORNERS = [(x, y, z) for x in (0, 1) for y in (0, 1) for z in (0, 1)]


class Cam:
    def __init__(self, pos, look, w=480, h=320, fov=70):
        self.pos, self.w, self.h = pos, w, h
        f = [look[i] - pos[i] for i in range(3)]
        n = math.sqrt(sum(c * c for c in f))
        self.f = [c / n for c in f]
        r = [-self.f[2], 0, self.f[0]]
        rn = math.sqrt(sum(c * c for c in r)) or 1
        self.r = [c / rn for c in r]
        self.u = [self.r[1] * self.f[2] - self.r[2] * self.f[1], self.r[2] * self.f[0] - self.r[0] * self.f[2],
                  self.r[0] * self.f[1] - self.r[1] * self.f[0]]
        self.k = (h / 2) / math.tan(math.radians(fov / 2))

    def proj(self, p):
        d = [p[i] - self.pos[i] for i in range(3)]
        z = sum(d[i] * self.f[i] for i in range(3))
        if z < 0.05:
            return None
        x = sum(d[i] * self.r[i] for i in range(3))
        y = sum(d[i] * self.u[i] for i in range(3))
        return (self.w / 2 + x * self.k / z, self.h / 2 - y * self.k / z, z)


def boxes_of(char):
    """[(матрица мировая, блок, glow)] для видимых частей персонажа."""
    out = []
    mats = char.matrices()
    for b in char.rig.boxes:
        m = mats[b.name]
        if abs(m[0][0]) + abs(m[1][1]) + abs(m[2][2]) < 0.01 and abs(m[0][1]) + abs(m[1][0]) < 0.01:
            continue
        out.append((chain([[1, 0, 0, char.pos[0]], [0, 1, 0, char.pos[1]], [0, 0, 1, char.pos[2]], [0, 0, 0, 1]], m),
                    b.block, b.glow))
    return out


def render(items, cam, bg=(28, 26, 40), floor=True, extra=None):
    img = Image.new("RGB", (cam.w, cam.h), bg)
    dr = ImageDraw.Draw(img)
    polys = []
    if floor:
        for x in range(-12, 12):
            for z in range(-14, 12):
                pts = [cam.proj((x + a, 0, z + b)) for a, b in ((0, 0), (1, 0), (1, 1), (0, 1))]
                if None in pts:
                    continue
                c = (70, 70, 78) if (x + z) % 2 else (82, 82, 90)
                polys.append((sum(p[2] for p in pts) / 4 + 1000, [(p[0], p[1]) for p in pts], c, None))
    light = (0.4, 0.8, 0.45)
    for m, block, glow in items:
        cs = [apply(m, c) for c in CORNERS]
        for idx, nrm in FACES:
            n = [sum(m[i][j] * nrm[j] for j in range(3)) for i in range(3)]
            ln = math.sqrt(sum(c * c for c in n)) or 1
            n = [c / ln for c in n]
            center = [sum(cs[i][k] for i in idx) / 4 for k in range(3)]
            v = [cam.pos[k] - center[k] for k in range(3)]
            if sum(n[k] * v[k] for k in range(3)) <= 0:
                continue
            pts = [cam.proj(cs[i]) for i in idx]
            if None in pts:
                continue
            shade = 0.55 + 0.45 * max(0, sum(n[k] * light[k] for k in range(3)))
            base = COLORS.get(block, (255, 0, 255))
            col = tuple(min(255, int(c * shade)) for c in base)
            thin = min(math.dist(cs[0], cs[k]) for k in (1, 2, 4)) < 0.02
            polys.append((sum(p[2] for p in pts) / 4 - (0.08 if thin else 0), [(p[0], p[1]) for p in pts], col, glow))
    polys.sort(key=lambda p: -p[0])
    for _, pts, col, glow in polys:
        outline = None
        if glow is not None:
            outline = ((glow >> 16) & 255, (glow >> 8) & 255, glow & 255)
        dr.polygon(pts, fill=col, outline=outline)
    if extra:
        extra(dr)
    return img


def pose_sheet(out_dir):
    from engine import Scene, P
    import rigs
    sc = Scene()
    poses = [
        ("hero", rigs.hero(), [P(), P(arm_r=(150, 0, 35), head=(0, 25, 0)), P(leg_r=(28, 0, 0), leg_l=(-28, 0, 0), arm_r=(-22, 0, 4), arm_l=(22, 0, 4))], set()),
        ("mage", rigs.ice_mage(), [P(), P(arm_r=(90, 0, 5), arm_l=(90, 0, 5))], set()),
        ("maid", rigs.oni_maid(), [P(), P(arm_r=(150, 0, 0), body=(10, 0, 0))], {"horn", "flail"}),
        ("cult", rigs.cultist(), [P(), P(body=(-20, 0, 0), head=(-30, 0, 35), arm_r=(140, 0, 60), arm_l=(140, 0, 60))], {"hands"}),
    ]
    os.makedirs(out_dir, exist_ok=True)
    tiles = []
    for name, rig, plist, groups in poses:
        for i, pz in enumerate(plist):
            c = sc.char(rig, (0, 0, 0), yaw=25, groups=groups)
            c.pose = pz
            items = boxes_of(c)
            if name == "cult":
                for j in range(1, 7):
                    tgt = [(-4, 4, 3), (4, 4, 3), (-5, 2, 2), (5, 2, 2), (-3, 0.5, 3), (3, 0.5, 3)][j - 1]
                    c.pose[f"h{j}"] = c.aim(f"h{j}", tgt, rigs.HAND_LEN)
                items = boxes_of(c)
                cam = Cam((5, 3.5, 8), (0, 1.6, 0), 320, 320, 75)
            else:
                cam = Cam((1.4, 1.5, 3.6), (0, 1.0, 0), 320, 320, 50)
            tiles.append(render(items, cam, floor=False))
    W = 4
    sheet = Image.new("RGB", (320 * W, 320 * math.ceil(len(tiles) / W)), (0, 0, 0))
    for i, t in enumerate(tiles):
        sheet.paste(t, ((i % W) * 320, (i // W) * 320))
    sheet.save(os.path.join(out_dir, "bodies.png"))
    print("saved", os.path.join(out_dir, "bodies.png"))




# ------------------------------------------------------------------ видео всей истории
PT_COL = {
    "snowflake": (230, 240, 255), "end_rod": (255, 255, 235), "squid_ink": (10, 8, 14), "large_smoke": (60, 60, 64),
    "cloud": (200, 200, 205), "reverse_portal": (170, 60, 230), "enchant": (200, 170, 255), "crit": (240, 240, 200),
    "firework": (255, 255, 255), "explosion": (230, 230, 230), "explosion_emitter": (255, 240, 220),
    "white_ash": (200, 200, 200), "damage_indicator": (120, 10, 10), "sweep_attack": (240, 240, 240),
}


def pt_color(p):
    if p.startswith("dust"):
        import re
        nums = [float(x) for x in re.findall(r"-?\d+\.?\d*", p.split("color:[")[1].split("]")[0])]
        return tuple(int(255 * min(1, c)) for c in nums[:3])
    if p.startswith("block"):
        return (150, 190, 250) if "ice" in p else (120, 120, 125)
    return PT_COL.get(p, (255, 255, 255))


def lerp_m(a, b, k):
    return [[a[i][j] + (b[i][j] - a[i][j]) * k for j in range(4)] for i in range(4)]


def story_video(out_path, step=2, w=480, h=270):
    import random
    import subprocess
    from story import build_scene
    sc = build_scene()
    rnd = random.Random(1)
    parts, pos, cams, shards, pts = {}, {}, [], [], []
    meta = {}
    for e in sorted(sc.log, key=lambda e: e[0]):
        t, kind = e[0], e[1]
        if kind == "mat":
            _, _, ch, part, m, dur, block, glow = e
            if block is not None:
                meta[(ch, part)] = (block, glow)
            parts.setdefault((ch, part), []).append((t, m, dur))
        elif kind == "pos":
            pos.setdefault(e[2], []).append((t, e[3], e[4]))
        elif kind == "cam":
            cams.append((t, e[2], e[3], e[4]))
        elif kind == "shard":
            shards.append(e)
        elif kind == "pt":
            _, _, p, ppos, d, speed, count = e
            col = pt_color(p)
            for _ in range(min(count, 12)):
                q = tuple(ppos[i] + rnd.gauss(0, d[i] + speed * 3) for i in range(3))
                pts.append((t, q, col))

    def track(keys, t, init, mix):
        cur, prev = init, init
        for kt, v, dur in keys:
            if kt > t:
                break
            prev = cur
            if dur and t < kt + dur:
                cur = mix(prev, v, (t - kt) / dur)
            else:
                cur = v
        return cur

    def track_m(keys, t):
        val = keys[0][1]
        for kt, m, dur in keys[1:]:
            if kt > t:
                break
            k = 1.0 if not dur else min(1.0, (t - kt) / dur)
            start = val
            val = lerp_m(start, m, k) if k < 1 else m
        return val

    ffmpeg = subprocess.Popen(["ffmpeg", "-y", "-loglevel", "error", "-f", "rawvideo", "-pix_fmt", "rgb24",
                               "-s", f"{w}x{h}", "-r", str(20 // step), "-i", "-", "-pix_fmt", "yuv420p",
                               "-c:v", "libx264", "-crf", "23", out_path], stdin=subprocess.PIPE)
    cam_state = (cams[0][1], cams[0][2])
    for t in range(1, sc.length + 1, step):
        # камера
        cp, cl = cams[0][1], cams[0][2]
        for kt, p_, l_, dur in cams:
            if kt > t:
                break
            if dur and t < kt + dur:
                k = (t - kt) / dur
                cp, cl = lerp3_(cp, p_, k), lerp3_(cl, l_, k)
            else:
                cp, cl = p_, l_
        cam = Cam(cp, cl, w, h, 70)
        items = []
        for (ch, part), keys in parts.items():
            m = track_m(keys, t)
            if abs(m[0][0]) + abs(m[1][1]) + abs(m[2][2]) + abs(m[0][1]) + abs(m[1][0]) < 0.003:
                continue
            ppos = track(pos.get(ch, []), t, (0, 0, 0), lerp3_)
            block, glow = meta[(ch, part)]
            items.append((chain([[1, 0, 0, ppos[0]], [0, 1, 0, ppos[1]], [0, 0, 1, ppos[2]], [0, 0, 0, 1]], m), block, glow))
        for e in shards:
            _, _, uid, start, end, dur, m, block, glow = e
            t0 = e[0]
            if t0 <= t <= t0 + 2 + dur:
                k = max(0.0, min(1.0, (t - t0 - 2) / dur))
                p_ = lerp3_(start, end, k)
                items.append((chain([[1, 0, 0, p_[0]], [0, 1, 0, p_[1]], [0, 0, 1, p_[2]], [0, 0, 0, 1]], m), "packed_ice", glow))
        night = (16, 14, 30)
        live = [(q, col, t - pt_t) for pt_t, q, col in pts if 0 <= t - pt_t < 10]

        def draw_pts(dr):
            for q, col, age in live:
                pr = cam.proj((q[0], q[1] + age * 0.02, q[2]))
                if pr is None:
                    continue
                r = max(1, int(5 / pr[2]))
                dr.rectangle([pr[0] - r, pr[1] - r, pr[0] + r, pr[1] + r], fill=col)

        img = render(items, cam, bg=night, extra=draw_pts)
        ffmpeg.stdin.write(img.tobytes())
    ffmpeg.stdin.close()
    ffmpeg.wait()
    print("saved", out_path)


def lerp3_(a, b, k):
    return tuple(a[i] + (b[i] - a[i]) * k for i in range(3))


if __name__ == "__main__":
    here = os.path.dirname(os.path.abspath(__file__))
    if "--story" in sys.argv:
        story_video(os.path.join(here, "..", "preview", "story.mp4"))
    else:
        pose_sheet(os.path.join(here, "..", "preview"))
