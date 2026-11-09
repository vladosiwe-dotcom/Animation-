"""Тела персонажей. Координаты в «пикселях» модели (как у скина Minecraft),
ноги 0..12, туловище 12..24, голова 24..33. Модель смотрит в +Z,
правая рука героя — на -X.
"""
from engine import humanoid, Rig

SKIN = "white_terracotta"
PURPLE_GLOW = 0x8A2BE2
DARK_GLOW = 0x5B0A91
RED_GLOW = 0xFF1A3C
WHITE_GLOW = 0xF0FAFF


def base_body(r, top, pants, shoes, sleeve, skin=SKIN, arm_w=3.0):
    # ноги
    for side, x0, x1 in (("r", -4, 0), ("l", 0, 4)):
        j = f"leg_{side}"
        r.box(j, x0 + 0.1, x1 - 0.1, 2, 12, -2, 2, pants, name=f"leg_{side}")
        r.box(j, x0, x1, 0, 2, -2.2, 2.6, shoes, name=f"shoe_{side}")
    # туловище
    r.box("body", -4, 4, 12, 24, -2, 2, top, name="torso")
    # руки
    h = arm_w / 2
    for side, cx in (("r", -5.5), ("l", 5.5)):
        j = f"arm_{side}"
        r.box(j, cx - h, cx + h, 14, 24, -h, h, sleeve, name=f"arm_{side}")
        r.box(j, cx - h + 0.15, cx + h - 0.15, 12, 14, -h + 0.15, h - 0.15, skin, name=f"hand_{side}")
    # голова
    r.box("head", -4.5, 4.5, 24, 33, -4.5, 4.5, skin, name="head")


def eyes(r, block, h=2.0, y0=26.5, white=True):
    for x0, x1 in ((-3.2, -1.4), (1.4, 3.2)):
        if white:
            r.box("head", x0 - 0.2, x1 + 0.2, y0 - 0.2, y0 + h + 0.2, 4.5, 4.56, "white_concrete")
        r.box("head", x0, x1, y0, y0 + h, 4.52, 4.62, block)


# ------------------------------------------------------------ Герой (спортивный костюм)
def hero():
    r = humanoid("hero")
    base_body(r, "black_concrete", "gray_concrete", "white_concrete", "black_concrete")
    # молния и оранжевые полосы костюма
    r.box("body", -0.35, 0.35, 12, 24, 2, 2.1, "white_concrete")
    r.box("body", -4.05, 4.05, 22.6, 24.05, -2.05, 2.05, "orange_concrete")
    r.box("body", -4.05, 4.05, 12, 13, -2.05, 2.05, "gray_concrete")
    for side, cx in (("r", -5.5), ("l", 5.5)):
        sx = -1 if side == "r" else 1
        r.box(f"arm_{side}", cx + sx * 1.5 - 0.05, cx + sx * 1.5 + 0.05, 14, 24, -0.4, 0.4, "orange_concrete")
        r.box(f"arm_{side}", cx - 1.55, cx + 1.55, 14, 15, -1.55, 1.55, "white_concrete")
    for side, x0, x1 in (("r", -4, 0), ("l", 0, 4)):
        r.box(f"leg_{side}", x0 + 0.05, x1 - 0.05, 2, 3, -2.05, 2.05, "orange_concrete")
    # причёска: чёрные торчащие волосы
    H = "black_concrete"
    r.box("head", -4.8, 4.8, 30, 33.6, -4.8, 4.8, H)
    r.box("head", -4.8, 4.8, 25, 33, -4.9, -3.6, H)
    r.box("head", -4.9, -4.2, 26.5, 31, -4.6, 3.5, H)
    r.box("head", 4.2, 4.9, 26.5, 31, -4.6, 3.5, H)
    for x0, y0 in ((-4.3, 28.4), (-1.6, 28.9), (1.2, 28.2)):
        r.box("head", x0, x0 + 2.8, y0, 30.2, 4.5, 4.95, H)
    for x0, z0 in ((-3, -2), (0.5, 0), (-1, 2)):
        r.box("head", x0, x0 + 2.2, 33.4, 34.8, z0, z0 + 2.2, H)
    eyes(r, "black_concrete", h=1.6, y0=26.6)
    r.box("head", -1.2, 1.2, 25.2, 25.5, 4.5, 4.58, "brown_terracotta")
    return r


# ------------------------------------------------------------ Ледяная волшебница (серебро + фиолет)
def ice_mage():
    r = humanoid("mage")
    base_body(r, "white_concrete", "white_concrete", "purple_concrete", "white_concrete", arm_w=2.6)
    r.box("body", -3, 3, 13, 20, 2, 2.12, "purple_concrete")
    r.box("body", -4.05, 4.05, 22.8, 24.05, -2.05, 2.05, "purple_concrete")
    r.box("body", -1, 1, 20, 22.5, 2.05, 2.2, "magenta_concrete")
    # юбка
    r.box("body", -5.6, 5.6, 7, 13, -3.4, 3.4, "white_concrete", name="skirt")
    r.box("body", -5.7, 5.7, 7, 8, -3.5, 3.5, "purple_concrete")
    # длинные серебряные волосы
    H = "white_wool"
    r.box("head", -4.8, 4.8, 30, 33.8, -4.8, 4.8, H)
    r.box("head", -5, 5, 9, 33, -5.4, -3.8, H, name="hair_back")
    r.box("head", -5.3, -4.4, 17, 31, -2, 4.2, H)
    r.box("head", 4.4, 5.3, 17, 31, -2, 4.2, H)
    r.box("head", -4.6, 4.6, 29.6, 32.4, 4.5, 4.95, H)
    for x0 in (-4, -1.5, 1.2):
        r.box("head", x0, x0 + 2, 28.6, 29.8, 4.5, 4.9, H)
    # цветок в волосах
    r.box("head", 3.6, 5.6, 30.5, 32.5, 1.5, 3.5, "white_concrete")
    r.box("head", 4.1, 5.1, 31, 32, 3.4, 3.7, "pink_concrete")
    eyes(r, "purple_concrete", h=2.4, y0=26.2)
    r.box("head", -0.8, 0.8, 25.1, 25.4, 4.5, 4.58, "pink_terracotta")
    return r


# ------------------------------------------------------------ Горничная-они (синие волосы, кистень)
def oni_maid():
    r = humanoid("maid")
    base_body(r, "black_concrete", "black_concrete", "black_concrete", "black_concrete", arm_w=2.6)
    r.box("body", -5, 5, 6.5, 13, -3.2, 3.2, "black_concrete", name="skirt")
    r.box("body", -5.1, 5.1, 6, 6.8, -3.3, 3.3, "white_concrete")
    r.box("body", -3, 3, 7.5, 13, 3.2, 3.45, "white_concrete", name="apron")
    r.box("body", -2.5, 2.5, 13, 19, 2, 2.15, "white_concrete")
    r.box("body", -4.05, 4.05, 22.5, 24.05, -2.05, 2.05, "white_concrete")
    for side, cx in (("r", -5.5), ("l", 5.5)):
        r.box(f"arm_{side}", cx - 1.45, cx + 1.45, 14, 15, -1.45, 1.45, "white_concrete")
    H = "light_blue_concrete"
    r.box("head", -4.8, 4.8, 30, 33.6, -4.8, 4.8, H)
    r.box("head", -5, 5, 25, 33, -5.2, -3.8, H)
    r.box("head", -5.2, -4.4, 24.5, 31, -4, 3.8, H)
    r.box("head", 4.4, 5.2, 24.5, 31, -4, 3.8, H)
    r.box("head", 0.3, 4.9, 26.6, 31, 4.5, 4.95, H)       # чёлка закрывает левый глаз
    r.box("head", -4.6, 0.3, 29.4, 31, 4.5, 4.9, H)
    r.box("head", -4.3, 4.3, 33.4, 34.6, -1.5, 1.5, "white_concrete")  # наколка горничной
    r.box("head", -3.2, -1.4, 26.4, 28.6, 4.5, 4.56, "white_concrete")
    r.box("head", -3, -1.6, 26.6, 28.4, 4.52, 4.62, "blue_concrete")
    r.box("head", -0.8, 0.8, 25.1, 25.4, 4.5, 4.58, "pink_terracotta")
    # рог они — светится, появляется в бою
    r.box("head", -0.7, 0.7, 33.5, 36.5, 2.4, 3.8, "white_concrete", group="horn", glow=WHITE_GLOW)
    r.box("head", -0.4, 0.4, 36.5, 38.6, 2.7, 3.5, "white_concrete", group="horn", glow=WHITE_GLOW)
    # кистень (моргенштерн) в правой руке: рукоять, цепь, шар с шипами
    cx = -5.5
    r.box("arm_r", cx - 0.7, cx + 0.7, 6.5, 12.5, -0.7, 0.7, "dark_oak_planks", group="flail")
    for k in range(6):
        y1 = 6.5 - k * 3.6
        r.box("arm_r", cx - 0.4, cx + 0.4, y1 - 2.6, y1, -0.4, 0.4, "iron_block", group="flail")
    by = -21.5
    r.box("arm_r", cx - 3.5, cx + 3.5, by - 7, by, -3.5, 3.5, "iron_block", group="flail")
    sp = "polished_blackstone"
    r.box("arm_r", cx - 5.5, cx - 3.5, by - 4.3, by - 2.7, -0.8, 0.8, sp, group="flail")
    r.box("arm_r", cx + 3.5, cx + 5.5, by - 4.3, by - 2.7, -0.8, 0.8, sp, group="flail")
    r.box("arm_r", cx - 0.8, cx + 0.8, by - 4.3, by - 2.7, 3.5, 5.5, sp, group="flail")
    r.box("arm_r", cx - 0.8, cx + 0.8, by - 4.3, by - 2.7, -5.5, -3.5, sp, group="flail")
    r.box("arm_r", cx - 0.8, cx + 0.8, by - 9, by - 7, -0.8, 0.8, sp, group="flail")
    return r


# ------------------------------------------------------------ Безумный жрец с теневыми руками
HAND_LEN = 49  # px от основания до кончиков пальцев


def shadow_hand(r, j, group, block="black_concrete", glow=PURPLE_GLOW, w=1.0):
    r.box(j, -1.2 * w, 1.2 * w, -1.2 * w, 1.2 * w, 0, 36, block, group=group, glow=glow)
    r.box(j, -3 * w, 3 * w, -1 * w, 1 * w, 36, 42, block, group=group, glow=glow)
    for x0 in (-3, -1.4, 0.2, 1.8):
        r.box(j, x0 * w, (x0 + 1.2) * w, -0.6 * w, 0.6 * w, 42, 49, block, group=group, glow=glow)
    r.box(j, 3 * w, 4.2 * w, -0.6 * w, 0.6 * w, 37, 42, block, group=group, glow=glow)


def cultist():
    r = humanoid("cult")
    base_body(r, "black_concrete", "black_concrete", "black_concrete", "black_concrete", arm_w=3.4)
    r.box("body", -5, 5, 0.5, 13, -3.5, 3.5, "black_concrete", name="robe")
    r.box("body", -1, 1, 0.5, 24, 3.45, 3.6, "purple_concrete")
    r.box("body", -4.1, 4.1, 22.5, 24.1, -2.1, 2.1, "purple_concrete")
    H = "green_concrete"
    r.box("head", -4.8, 4.8, 29, 33.6, -4.8, 4.8, H)
    r.box("head", -4.9, 4.9, 25, 33, -4.9, -3.4, H)
    r.box("head", -4.9, -4.3, 25.5, 31, -4.5, 4.6, H)
    r.box("head", 4.3, 4.9, 25.5, 31, -4.5, 4.6, H)
    r.box("head", -4.5, 4.5, 30.6, 31.4, 4.5, 4.9, H)
    # выпученные глаза и оскал
    for x0 in (-3.6, 1.0):
        r.box("head", x0, x0 + 2.6, 26.5, 29.5, 4.5, 4.56, "white_concrete")
        r.box("head", x0 + 0.9, x0 + 1.7, 27.4, 28.4, 4.52, 4.64, "red_concrete")
    r.box("head", -2.6, 2.6, 24.8, 25.8, 4.5, 4.58, "red_concrete")
    r.box("head", -2.2, 2.2, 25.2, 25.6, 4.52, 4.62, "white_concrete")
    # 6 «невидимых рук» из спины
    for i, (x, y) in enumerate(((-3, 22), (3, 22), (-4, 18.5), (4, 18.5), (-2.5, 15), (2.5, 15)), 1):
        r.joint(f"h{i}", "fwd", (x, y, -2.2), "body")
        shadow_hand(r, f"h{i}", "hands")
    return r


# ------------------------------------------------------------ Ведьма (только руки из тьмы)
def witch():
    r = Rig("witch", 0.11)
    r.joint("body", "up", (0, 0, 0))
    r.joint("hl", "fwd", (34, 4, -14), "body")
    r.joint("hr", "fwd", (-34, 4, -14), "body")
    r.joint("hb", "fwd", (0, -6, -30), "body")
    shadow_hand(r, "hl", "hands", glow=DARK_GLOW)
    shadow_hand(r, "hr", "hands", glow=DARK_GLOW)
    shadow_hand(r, "hb", "hands", glow=DARK_GLOW, w=0.8)
    return r


# ------------------------------------------------------------ Пиксельное сердце
def heart():
    r = Rig("heart", 0.07)
    r.joint("body", "up", (0, 3, 0))
    rows = [".XX.XX.", "XXXXXXX", "XXXXXXX", ".XXXXX.", "..XXX..", "...X..."]
    for i, row in enumerate(rows):
        y = 5 - i
        x = 0
        while x < len(row):
            if row[x] == "X":
                x2 = x
                while x2 < len(row) and row[x2] == "X":
                    x2 += 1
                r.box("body", x - 3.5, x2 - 3.5, y, y + 1, -1, 1, "red_concrete", glow=RED_GLOW)
                x = x2
            else:
                x += 1
    return r
