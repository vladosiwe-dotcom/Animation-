"""Рисует текстуры маски: розовое полупрозрачное «стекло» с фиолетовыми сотами (как на референсе).

python3 tools/gen_textures.py
"""
import math
import os

from PIL import Image, ImageDraw, ImageFilter

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "miragemask", "textures")
SS = 16  # суперсэмплинг

PINK = (190, 240, 255)
VIOLET = (30, 200, 255)
DEEP = (10, 90, 190)


def hex_pts(cx, cy, r):
    return [(cx + r * math.cos(math.radians(60 * i - 30)), cy + r * math.sin(math.radians(60 * i - 30))) for i in range(6)]


def mask_texture(size=32):
    S = size * SS
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    # силуэт маски: лоб, скулы, подбородок (как стеклянная «плёнка» на лице)
    shape = Image.new("L", (S, S), 0)
    d = ImageDraw.Draw(shape)
    u = S / 32
    d.rounded_rectangle([3 * u, 3 * u, 29 * u, 22 * u], radius=7 * u, fill=255)
    d.ellipse([5 * u, 12 * u, 27 * u, 30 * u], fill=255)
    # прорези: глаза и рот
    holes = Image.new("L", (S, S), 0)
    h = ImageDraw.Draw(holes)
    h.ellipse([7 * u, 11 * u, 14.5 * u, 16.5 * u], fill=255)
    h.ellipse([17.5 * u, 11 * u, 25 * u, 16.5 * u], fill=255)
    h.rounded_rectangle([10 * u, 22 * u, 22 * u, 26 * u], radius=2 * u, fill=255)
    from PIL import ImageChops
    body = ImageChops.subtract(shape, holes)

    # стекло: розовое, полупрозрачное, к краям плотнее
    glass = Image.new("RGBA", (S, S), PINK + (0,))
    edge = body.filter(ImageFilter.GaussianBlur(2.2 * u))
    alpha = Image.eval(edge, lambda v: int(255 - v * 0.55))  # центр прозрачнее, край плотнее
    alpha = ImageChops.multiply(alpha, body)
    glass.putalpha(alpha)
    img.alpha_composite(glass)

    # соты: верх ярче (лоб), к низу бледнее, как на кадре
    hexes = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    hd = ImageDraw.Draw(hexes)
    r = 3.7 * u
    for row in range(-1, 16):
        for col in range(-1, 16):
            cx = col * math.sqrt(3) * r + (row % 2) * math.sqrt(3) * r / 2
            cy = row * 1.5 * r
            k = max(0.25, 1 - cy / S * 1.1)
            fill = DEEP + (int(70 * k),)
            line = VIOLET + (int(255 * k),)
            hd.polygon(hex_pts(cx, cy, r * 0.92), fill=fill, outline=None)
            hd.line(hex_pts(cx, cy, r * 0.92) + [hex_pts(cx, cy, r * 0.92)[0]], fill=line, width=max(1, int(0.9 * u)))
    hexes.putalpha(ImageChops.multiply(hexes.getchannel("A"), body))
    img.alpha_composite(hexes)

    # глянцевая кромка и блики
    rim = ImageChops.subtract(body, body.filter(ImageFilter.MinFilter(int(1.3 * u) | 1)))
    rim_l = Image.new("RGBA", (S, S), (255, 240, 248, 0))
    rim_l.putalpha(rim)
    img.alpha_composite(rim_l)
    hl = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    ImageDraw.Draw(hl).ellipse([6 * u, 4.5 * u, 11 * u, 7 * u], fill=(255, 255, 255, 200))
    hl.putalpha(ImageChops.multiply(hl.getchannel("A"), body))
    img.alpha_composite(hl)

    out = img.resize((size, size), Image.LANCZOS)
    # искры как на референсе
    px = out.load()
    for x, y in ((2, 9), (29, 7), (27, 25), (4, 24)):
        px[x, y] = (255, 255, 255, 255)
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            px[x + dx, y + dy] = (230, 220, 255, 170)
    return out


def hex_particle(size=16):
    S = size * SS
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    c = S / 2
    d.polygon(hex_pts(c, c, S * 0.46), fill=(255, 255, 255, 70))
    pts = hex_pts(c, c, S * 0.44)
    d.line(pts + [pts[0]], fill=(255, 255, 255, 255), width=int(S * 0.09))
    return img.resize((size, size), Image.LANCZOS)


if __name__ == "__main__":
    os.makedirs(os.path.join(ROOT, "item"), exist_ok=True)
    os.makedirs(os.path.join(ROOT, "particle"), exist_ok=True)
    mask_texture().save(os.path.join(ROOT, "item", "mirage_mask.png"))
    hex_particle().save(os.path.join(ROOT, "particle", "hex.png"))
    big = mask_texture(32).resize((256, 256), Image.NEAREST)
    bg = Image.new("RGBA", big.size, (40, 40, 48, 255))
    bg.alpha_composite(big)
    bg.save(os.path.join(os.path.dirname(__file__), "..", "preview_mask_item.png"))
    print("ok")
