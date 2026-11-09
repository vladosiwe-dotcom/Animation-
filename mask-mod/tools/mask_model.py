"""Модель маски (по референсу): овальная стеклянная пластина из крупных шестиугольников,
светящиеся рёбра и обод, тёмные боковые крепления со светящимися полосками.

Один источник геометрии для мода и для Blender:
    python3 tools/mask_model.py  ->  src/main/resources/assets/miragemask/models/mask_mesh.json

Координаты — «пиксели» головы игрока, как у ModelPart: x вправо, y ВНИЗ (макушка -8, подбородок 0),
лицо на z = -4, «вперёд» = -z. В игре всё делится на 16.
"""
import json
import math
import os

OUT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "miragemask", "models",
                   "mask_mesh.json")

CX, CY = 0.0, -3.35          # центр овала
A, B = 4.55, 4.05            # полуоси овала (по x и по y)
RC = 2.05                    # радиус соты (flat-top)
EDGE_W = 0.30                # толщина светящегося ребра
RIM_W = 0.42                 # толщина обода


def surface(x, y):
    """Выпуклая «скорлупа» перед лицом; к бокам загибается назад к вискам."""
    nx, ny = (x - CX) / A, (y - CY) / B
    bulge = 1.05 * max(0.0, 1 - 0.85 * nx * nx - 0.55 * ny * ny)
    wrap = 1.1 * nx ** 4
    return -(4.62 + bulge) + wrap


def normal(x, y):
    e = 0.01
    dzdx = (surface(x + e, y) - surface(x - e, y)) / (2 * e)
    dzdy = (surface(x, y + e) - surface(x, y - e)) / (2 * e)
    n = (dzdx, dzdy, -1.0)          # наружу (вперёд, -z)
    l = math.sqrt(sum(c * c for c in n))
    return tuple(c / l for c in n)


def P(x, y, lift=0.0):
    n = normal(x, y)
    z = surface(x, y)
    return [round(x + n[0] * lift, 4), round(y + n[1] * lift, 4), round(z + n[2] * lift, 4)]


def ellipse_poly(n=48, scale=1.0):
    return [(CX + A * scale * math.cos(2 * math.pi * i / n), CY + B * scale * math.sin(2 * math.pi * i / n)) for i in range(n)]


def hex_poly(cx, cy, r):
    return [(cx + r * math.cos(math.radians(60 * i)), cy + r * math.sin(math.radians(60 * i))) for i in range(6)]


def clip(subject, clipper):
    """Sutherland–Hodgman (оба многоугольника выпуклые). Помечаем, какие рёбра — от соты."""
    def inside(p, a, b):
        return (b[0] - a[0]) * (p[1] - a[1]) - (b[1] - a[1]) * (p[0] - a[0]) >= 0

    def inter(p1, p2, a, b):
        x1, y1 = p1; x2, y2 = p2; x3, y3 = a; x4, y4 = b
        den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
        t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den
        return (x1 + t * (x2 - x1), y1 + t * (y2 - y1))

    out = subject
    for i in range(len(clipper)):
        a, b = clipper[i], clipper[(i + 1) % len(clipper)]
        inp, out = out, []
        if not inp:
            break
        s = inp[-1]
        for e in inp:
            if inside(e, a, b):
                if not inside(s, a, b):
                    out.append(inter(s, e, a, b))
                out.append(e)
            elif inside(s, a, b):
                out.append(inter(s, e, a, b))
            s = e
    return out


def area(poly):
    return 0.5 * abs(sum(poly[i][0] * poly[(i + 1) % len(poly)][1] - poly[(i + 1) % len(poly)][0] * poly[i][1]
                         for i in range(len(poly))))


def on_ellipse(p, tol=0.02):
    return abs(((p[0] - CX) / A) ** 2 + ((p[1] - CY) / B) ** 2 - 1) < tol


def strip(p, q, w, lift, n=4):
    """Полоса-ребро от p до q шириной w, лежащая на поверхности; разбита на n кусков."""
    quads = []
    for k in range(n):
        a = (p[0] + (q[0] - p[0]) * k / n, p[1] + (q[1] - p[1]) * k / n)
        b = (p[0] + (q[0] - p[0]) * (k + 1) / n, p[1] + (q[1] - p[1]) * (k + 1) / n)
        dx, dy = b[0] - a[0], b[1] - a[1]
        l = math.hypot(dx, dy) or 1
        ox, oy = -dy / l * w / 2, dx / l * w / 2
        quads.append([P(a[0] - ox, a[1] - oy, lift), P(b[0] - ox, b[1] - oy, lift),
                      P(b[0] + ox, b[1] + oy, lift), P(a[0] + ox, a[1] + oy, lift)])
    return quads


def build():
    oval = ellipse_poly()
    cells = []
    colstep, rowstep = 1.5 * RC, math.sqrt(3) * RC
    for col in range(-3, 4):
        for row in range(-3, 4):
            cx = CX + col * colstep
            cy = CY + row * rowstep + (rowstep / 2 if col % 2 else 0)
            poly = clip(hex_poly(cx, cy, RC), oval)
            if len(poly) < 3 or area(poly) < 0.35:
                continue
            gx = sum(p[0] for p in poly) / len(poly)
            gy = sum(p[1] for p in poly) / len(poly)
            # гранёное стекло: веер треугольников от центра, у каждой грани свой оттенок
            glass = []
            for i in range(len(poly)):
                a, b = poly[i], poly[(i + 1) % len(poly)]
                mid = ((a[0] + b[0]) / 2, (a[1] + b[1]) / 2)
                facet = 0.78 + 0.22 * math.sin(i * 2.3 + col * 1.7 + row * 0.9)   # одна грань на сторону соты
                glass.append({"tri": [P(gx, gy), P(a[0], a[1]), P(mid[0], mid[1])], "shade": round(facet, 3)})
                glass.append({"tri": [P(gx, gy), P(mid[0], mid[1]), P(b[0], b[1])], "shade": round(facet, 3)})
            edges, glow = [], []
            for i in range(len(poly)):
                a, b = poly[i], poly[(i + 1) % len(poly)]
                if on_ellipse(a) and on_ellipse(b):
                    continue  # это обод — рисуется отдельно
                edges += strip(a, b, EDGE_W, 0.06)
                glow += strip(a, b, EDGE_W * 3.2, 0.04)   # ореол свечения (для игры)
            d = math.hypot((gx - CX) / A, (gy - CY) / B)
            cells.append({"center": P(gx, gy), "normal": [round(c, 4) for c in normal(gx, gy)],
                          "dist": round(d, 3), "glass": glass, "edges": edges, "glow": glow})
    rim, rim_glow = [], []
    for i in range(len(oval)):
        rim += strip(oval[i], oval[(i + 1) % len(oval)], RIM_W, 0.08, n=1)
        rim_glow += strip(oval[i], oval[(i + 1) % len(oval)], RIM_W * 2.8, 0.05, n=1)

    # боковые крепления (тёмные «ремешки» от края маски к ушам) и светящиеся полоски
    clips, bars = [], []
    for s in (-1, 1):
        x0, x1 = (4.35, 5.05) if s > 0 else (-5.05, -4.35)
        clips.append(box(x0, x1, -4.9, -1.7, -4.75, 0.9))
        clips.append(box(x0 - 0.15 * s if s < 0 else x0, x1 + 0.15 * s if s > 0 else x1, -4.3, -2.3, -5.3, -4.6))
        xo = x1 + 0.02 if s > 0 else x0 - 0.02
        bars.append(face_x(xo, -4.4, -2.2, -0.55, 0.0))     # полоска сбоку (видна сзади и сбоку)
        bars.append(face_x(xo, -4.0, -2.6, -4.2, -3.8))     # полоска у края маски (видна спереди)
        xm = (x0 + x1) / 2
        bars.append([[xm - 0.18, -4.4, 0.92], [xm + 0.18, -4.4, 0.92], [xm + 0.18, -2.2, 0.92], [xm - 0.18, -2.2, 0.92]])  # сзади
    return {"cells": cells, "rim": rim, "rim_glow": rim_glow, "clips": clips, "bars": bars,
            "center": [CX, CY, -5.2]}


def box(x0, x1, y0, y1, z0, z1):
    """6 граней коробки, у каждой свой оттенок (без освещения)."""
    v = lambda x, y, z: [x, y, z]
    return [
        {"quad": [v(x0, y0, z0), v(x1, y0, z0), v(x1, y1, z0), v(x0, y1, z0)], "shade": 0.85},  # перед
        {"quad": [v(x0, y0, z1), v(x0, y1, z1), v(x1, y1, z1), v(x1, y0, z1)], "shade": 0.55},  # зад
        {"quad": [v(x0, y0, z0), v(x0, y1, z0), v(x0, y1, z1), v(x0, y0, z1)], "shade": 0.7},   # -x
        {"quad": [v(x1, y0, z0), v(x1, y0, z1), v(x1, y1, z1), v(x1, y1, z0)], "shade": 0.7},   # +x
        {"quad": [v(x0, y0, z0), v(x0, y0, z1), v(x1, y0, z1), v(x1, y0, z0)], "shade": 1.0},   # верх
        {"quad": [v(x0, y1, z0), v(x1, y1, z0), v(x1, y1, z1), v(x0, y1, z1)], "shade": 0.45},  # низ
    ]


def face_x(x, y0, y1, z0, z1):
    return [[x, y0, z0], [x, y1, z0], [x, y1, z1], [x, y0, z1]]


if __name__ == "__main__":
    data = build()
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w") as f:
        json.dump(data, f, separators=(",", ":"))
    n_tri = sum(len(c["glass"]) for c in data["cells"])
    n_edge = sum(len(c["edges"]) for c in data["cells"])
    print(f"cells={len(data['cells'])} glass_tris={n_tri} edge_quads={n_edge} rim={len(data['rim'])} "
          f"clips={sum(len(c) for c in data['clips'])} bars={len(data['bars'])} -> {os.path.normpath(OUT)}")
