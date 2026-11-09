"""Сценарий анимации «Re:Блок — Возвращение смертью» и сборка датапака.

python3 tools/story.py   -> datapack/ReBlock_Animation (+ dist/ReBlock_Animation.zip)

Все координаты — относительно точки, где игрок запустил /function rz:start
(x вправо-восток, y вверх, z на юг). 20 тиков = 1 секунда.
"""
import json
import math
import os
import random
import shutil
import sys
import zipfile

sys.path.insert(0, os.path.dirname(__file__))
import rigs  # noqa: E402
from engine import NS, P, Scene, dispatch_tree, fnum, lerp3  # noqa: E402

random.seed(7)
HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.normpath(os.path.join(HERE, ".."))
PACK_NAME = "ReBlock_Animation"

# цвета частиц
def fl(*cs):
    # 1.21.1: в NBT-списке все числа одного типа, поэтому везде float с суффиксом f
    return ",".join(fnum(c) + "f" for c in cs)


def dust(r, g, b, s=1.0):
    return f"dust{{color:[{fl(r, g, b)}],scale:{fnum(s)}f}}"


def dust_tr(a, b, s=1.0):
    return f"dust_color_transition{{from_color:[{fl(*a)}],to_color:[{fl(*b)}],scale:{fnum(s)}f}}"


MIASMA = dust_tr((0.25, 0.0, 0.35), (0.02, 0.0, 0.03), 2.2)
SHADOW = dust(0.12, 0.0, 0.2, 1.6)
VIOLET = dust(0.55, 0.15, 0.95, 1.0)
BLOOD = dust(0.75, 0.02, 0.05, 1.3)
ICE = dust_tr((0.75, 0.9, 1.0), (0.4, 0.6, 1.0), 1.0)
ICE_BLOCK = 'block{block_state:{Name:"minecraft:packed_ice"}}'
STONE_BLOCK = 'block{block_state:{Name:"minecraft:stone_bricks"}}'

# поз-библиотека
STAND = P()
LOOK_L = P(head=(0, 40, 0))
LOOK_R = P(head=(0, -40, 0))
THINK = P(arm_r=(165, 0, -15), head=(-8, 0, 8))
WAVE_A = P(arm_r=(160, 0, 40), head=(-5, 0, 0))
WAVE_B = P(arm_r=(160, 0, 15), head=(-5, 0, 0))
SHY = P(arm_l=(55, -20, -25), arm_r=(55, 20, -25), head=(12, 0, 0))
GUARD = P(arm_r=(60, 0, 20), arm_l=(60, 0, 20), leg_r=(-10, 0, 8), leg_l=(10, 0, 8), body=(8, 0, 0))
CAST = P(arm_r=(92, 0, 8), arm_l=(92, 0, 8), body=(6, 0, 0), leg_r=(-15, 0, 4), leg_l=(15, 0, 4))
CAST_UP = P(arm_r=(170, 0, 15), arm_l=(170, 0, 15), body=(-6, 0, 0), head=(-15, 0, 0))
MAD_A = P(body=(-22, 0, 6), head=(-35, 0, 40), arm_r=(150, 0, 70), arm_l=(150, 0, 70))
MAD_B = P(body=(55, 0, 0), head=(-55, 0, -20), arm_r=(20, 0, 80), arm_l=(20, 0, 80), leg_r=(-15, 0, 0), leg_l=(15, 0, 0))
MAD_C = P(body=(-10, 25, -8), head=(-20, 0, -45), arm_r=(120, 0, 30), arm_l=(60, 0, 90))
CROUCH = P(body=(35, 0, 0), leg_r=(70, 0, 5), leg_l=(70, 0, 5), arm_r=(150, 0, 20), arm_l=(-30, 0, 20), head=(-30, 0, 0))
AIR = P(body=(-10, 0, 0), leg_r=(40, 0, 5), leg_l=(-20, 0, 5), arm_r=(175, 0, 10), arm_l=(40, 0, 50), head=(-10, 0, 0))
SMASH = P(body=(40, 0, 0), leg_r=(-30, 0, 5), leg_l=(50, 0, 5), arm_r=(35, 0, 5), arm_l=(-30, 0, 40), head=(-25, 0, 0))
SPIN_BASE = P(body=(5, 0, 0), leg_r=(-15, 0, 8), leg_l=(15, 0, 8), arm_l=(30, 0, 40))
HIT = P(body=(32, 0, 0), arm_r=(55, 0, -10), arm_l=(55, 0, -10), head=(25, 0, 0), leg_r=(-10, 0, 0), leg_l=(15, 0, 0))
LIMP = P(body=(12, 0, 0), head=(35, 0, 0), arm_r=(5, 0, 6), arm_l=(5, 0, 6))
CLUTCH = P(body=(28, 0, 0), head=(30, 0, 10), arm_r=(80, 30, -20), arm_l=(80, -30, -20), leg_r=(15, 0, 4), leg_l=(-5, 0, 4))
SHOCK = P(body=(10, 0, 0), head=(12, 0, 0), arm_r=(65, 25, -15), arm_l=(30, -10, 0))
RESOLVE = P(arm_r=(100, 0, -10), head=(-5, 0, 0), body=(-3, 0, 0), leg_r=(-8, 0, 6), leg_l=(8, 0, 6))
SCREAM = P(arm_r=(95, 0, 10), arm_l=(60, 0, 25), body=(15, 0, 0), head=(-20, 0, 0))

HERO_SPOT = (0, 0, -3.5)
MAGE_SPOT = (2.2, 0, -1.0)
CULT_SPOT = (0, 0, 7.0)


def build_scene():
    sc = Scene()
    hero = sc.char(rigs.hero(), (0, 0, -11), yaw=0)
    mage = sc.char(rigs.ice_mage(), (11, 0, -0.5), yaw=-90, visible=False)
    cult = sc.char(rigs.cultist(), (CULT_SPOT[0], -2.3, CULT_SPOT[2]), yaw=180, visible=False)
    maid = sc.char(rigs.oni_maid(), (-7, 0, -12), yaw=0, visible=False)
    witch = sc.char(rigs.witch(), (0, -0.2, -3.5), yaw=0, visible=False)
    heart = sc.char(rigs.heart(), (0, 1.0, -2.3), yaw=0, visible=False)
    sc.spawn_all()

    hands = [f"h{i}" for i in range(1, 7)]

    def cult_hands(t, targets, dur, extra_pose=None):
        if extra_pose is not None:
            sc.pose(t, cult, 0, pose=extra_pose)  # тело сначала, чтобы прицел учитывал позу
        merge = {h: cult.aim(h, tg, rigs.HAND_LEN) for h, tg in zip(hands, targets)}
        sc.pose(t, cult, dur, merge=merge)

    def tips_fx(t, char, joints, particle, n=3):
        for j in joints:
            p = char.point(j, (0, 0, rigs.HAND_LEN * (char.pose.get(j, (0, 0, 0, 1))[3])))
            sc.particle(t, particle, p, (0.15, 0.15, 0.15), 0.01, n)

    # ================================================================ 1. Пролог
    sc.add(1, f"time set 13000")
    sc.card(4, "Re:Блок", "dark_purple", "— жизнь с нуля в кубическом мире —", fade_in=20, stay=70, fade_out=20)
    sc.sound(4, "block.bell.use", 1, 0.5)
    sc.cam_path(2, (6, 4.5, -16), (3.5, 2.4, -9.5), (0, 1, -6), (0, 1.2, -4), 110)
    t = sc.walk(20, hero, HERO_SPOT, speed=0.095)               # ~t=100
    for k in range(20, 100, 10):
        sc.particle(k, "white_ash", (random.uniform(-4, 4), 3, random.uniform(-10, 2)), (3, 1, 3), 0, 6)
    sc.pose(t + 2, hero, 8, pose=LOOK_L)
    sc.pose(t + 16, hero, 10, pose=LOOK_R)
    sc.say(t + 6, "Герой", "gold", "Где это я?.. Это что, другой мир?!", 50)
    sc.pose(t + 32, hero, 8, pose=THINK)
    sc.cam(t + 28, (2.2, 1.9, -1.6), (0, 1.55, -3.5), 0)
    sc.cam(t + 29, (1.6, 1.8, -1.2), (0, 1.55, -3.5), 30)

    # ================================================================ 2. Встреча с ледяной волшебницей
    t2 = 140
    sc.pose(t2, mage, 0, visible=True)
    sc.cam(t2, (-4, 2.2, -1.5), (4, 1.2, -0.5), 0)
    sc.cam(t2 + 1, (-3, 2.0, -2.5), (2.5, 1.3, -0.8), 50)
    end = sc.walk(t2 + 2, mage, MAGE_SPOT, speed=0.11)
    for k in range(t2, end, 3):
        sc.particle(k, "snowflake", (random.uniform(-1, 11), 2.8, random.uniform(-3, 2)), (1.5, 0.5, 1.5), 0.01, 4)
        sc.particle(k, "end_rod", lerp3((11, 1.8, -0.5), (2.6, 1.8, -1.0), (k - t2) / (end - t2)), (0.2, 0.2, 0.2), 0.005, 1)
    sc.pose(t2 + 20, hero, 6, pose=STAND, face=(mage.pos[0], 0, mage.pos[2]))
    sc.pose(t2 + 30, hero, 6, pose=STAND, face=MAGE_SPOT)
    sc.pose(end + 1, mage, 6, pose=SHY, face=HERO_SPOT)
    w = end + 6
    sc.cam(w, (-1.3, 2.0, -6.0), (2.2, 1.5, -1.0), 0)
    for k in range(4):
        sc.pose(w + k * 6, hero, 6, pose=WAVE_A if k % 2 == 0 else WAVE_B)
    sc.pose(w + 26, hero, 6, pose=STAND)
    sc.say(w + 2, "Герой", "gold", "Привет! Я просто парень... из другого мира!", 55)
    sc.cam(w + 60, (0.8, 1.75, -2.9), (2.2, 1.55, -1.0), 0)     # лицо волшебницы из-за плеча героя
    sc.cam(w + 61, (1.0, 1.72, -2.6), (2.2, 1.55, -1.0), 50)
    LINE = "...Тебе нельзя здесь находиться. Здесь опасно."
    sc.say(w + 62, "Волшебница", "light_purple", LINE, 60)
    sc.pose(w + 62, mage, 10, pose=P(arm_l=(45, 0, -10), head=(-5, 0, 0)))

    # ================================================================ 3. Миазмы. Безумный жрец
    t3 = 350
    sc.cam(t3, (-2.8, 2.3, 0.5), (0, 1.3, 7), 0)
    sc.cam(t3 + 1, (-2.2, 2.0, 1.5), (0, 1.3, 7), 80)
    sc.effect(t3, "darkness", 6)
    sc.sound(t3, "ambient.cave", 1, 0.6)
    sc.sound(t3 + 4, "entity.elder_guardian.curse", 0.6, 0.5)
    sc.pose(t3, hero, 8, pose=GUARD, face=CULT_SPOT)
    sc.pose(t3 + 4, mage, 8, pose=P(head=(-10, 0, 0), arm_l=(30, 0, 10)), face=CULT_SPOT)
    for k in range(t3, t3 + 90, 2):
        r = 0.4 + (k - t3) * 0.03
        sc.ring(k, "squid_ink", (0, 0.1, 7), min(r, 2.2), n=10, d=(0.05, 0.05, 0.05), speed=0.01)
        sc.particle(k, MIASMA, (0, 0.6, 7), (1.2, 0.6, 1.2), 0, 6)
        if k % 6 == 0:
            sc.particle(k, "large_smoke", (0, 0.3, 7), (1, 0.2, 1), 0.02, 4)
            sc.particle(k, STONE_BLOCK, (0, 0.1, 7), (1, 0.1, 1), 0, 6)
    t_rise = t3 + 20
    sc.pose(t_rise, cult, 0, visible=True, pose=P(arm_r=(170, 0, 10), arm_l=(170, 0, 10), head=(-30, 0, 0)))
    sc.move(t_rise + 1, cult, CULT_SPOT, 55, turn=False)
    sc.sound(t_rise, "entity.wither.spawn", 0.5, 0.6)
    tm = t_rise + 60                                            # ~430
    sc.pose(tm, cult, 10, pose=MAD_A)
    sc.sound(tm, "entity.witch.celebrate", 1, 0.55)
    sc.say(tm + 2, "Безумец", "dark_green", "Ах... мой мозг... ДРО-О-ОЖИТ!", 55)
    sc.pose(tm + 20, cult, 6, pose=MAD_C)
    sc.pose(tm + 30, cult, 6, pose=MAD_A)
    sc.pose(tm + 40, cult, 6, pose=MAD_C)
    sc.cam(tm + 55, (0.9, 2.0, 4.4), (0, 1.75, 7), 0)          # крупный план
    sc.cam(tm + 56, (0.6, 1.95, 4.9), (0, 1.75, 7), 40)
    sc.pose(tm + 60, cult, 4, pose=MAD_B)
    sc.sound(tm + 60, "entity.evoker.prepare_attack", 1, 0.7)
    sc.say(tm + 62, "Безумец", "dark_green", "А вы... вы ЛЕНИТЕСЬ?!", 50)
    t = tm + 110                                                # ~540
    sc.cam(t, (-1.5, 1.9, -6.5), (0.5, 1.4, 1), 0)
    sc.say(t + 2, "Герой", "gold", "Этот тип... явно опасен!", 40)
    sc.move(t + 30, mage, (1.2, 0, 0.6), 12)
    sc.pose(t + 42, mage, 6, pose=GUARD, face=CULT_SPOT)
    sc.say(t + 44, "Волшебница", "light_purple", "Встань за меня!", 30)
    sc.pose(t + 40, cult, 8, pose=MAD_A)

    # ================================================================ 4. Невидимые руки vs лёд
    t4 = 610
    sc.cam(t4, (-6.5, 4.6, -1.5), (0, 1.6, 5), 0)
    sc.cam(t4 + 1, (-5.5, 4.0, -0.5), (0, 1.6, 5), 60)
    sc.pose(t4, cult, 0, pose=P(body=(-15, 0, 0), head=(-30, 0, 0), arm_r=(120, 0, 70), arm_l=(120, 0, 70)))
    fold = {h: (0, 180, 0, 0.05) for h in hands}
    sc.pose(t4, cult, 0, merge=fold, show=["hands"])
    fan = [(-4.5, 5, 6), (4.5, 5, 6), (-5.5, 2.5, 5), (5.5, 2.5, 5), (-3.5, 0.6, 4), (3.5, 0.6, 4)]
    fan2 = [(-3.8, 5.6, 5), (3.6, 4.4, 5.5), (-5, 3.2, 4), (5.6, 1.8, 6), (-4, 0.9, 5), (3, 0.3, 3.4)]
    cult_hands(t4 + 2, fan, 8)
    sc.sound(t4 + 2, "entity.warden.sonic_boom", 0.4, 0.5)
    sc.sound(t4 + 2, "entity.illusioner.mirror_move", 1, 0.6)
    for k in range(t4 + 2, t4 + 14):
        sc.particle(k, SHADOW, (0, 1.4, 7), (1.5, 1, 1.5), 0, 10)
    sc.say(t4 + 10, "Герой", "gold", "Руки?! Чёрные руки из его спины!", 45)
    for i, k in enumerate(range(t4 + 20, t4 + 90, 10)):
        cult_hands(k, fan2 if i % 2 == 0 else fan, 10)
        tips_fx(k, cult, hands, SHADOW, 3)
        if i % 3 == 0:
            sc.sound(k, "entity.vex.charge", 0.7, 0.6)

    # каст ледяных копий
    t = t4 + 90                                                 # 700
    sc.cam(t, (-6, 2.1, 1.5), (0.5, 1.4, 3.5), 0)
    sc.cam(t + 1, (-5.6, 2.0, 2.2), (0.5, 1.4, 3.5), 70)
    sc.pose(t, mage, 6, pose=CAST, face=CULT_SPOT)
    sc.say(t + 2, "Волшебница", "light_purple", "Эль Хума!", 30)
    sc.sound(t, "block.amethyst_block.chime", 1, 1.4)
    sc.sound(t + 4, "entity.illusioner.cast_spell", 1, 1.2)
    caster = mage.point("arm_r", (-5.5, 11, 0))
    circle = (caster[0] - 0.1, caster[1], caster[2] + 0.6)
    for k in range(t, t + 50, 2):
        sc.ring(k, "snowflake", circle, 0.9, n=14, axis="x" if False else "z")
        sc.ring(k, "end_rod", circle, 0.55, n=8, axis="z")
    targets = [(0, 1.6, 7), (-0.8, 2.2, 7), (0.8, 1.2, 7), (-0.5, 0.8, 7), (0.6, 2.0, 7), (0, 1.3, 7)]
    tips = []
    for i, tg in enumerate(targets):
        ts = t + 10 + i * 6
        src = (circle[0] + random.uniform(-0.6, 0.6), circle[1] + random.uniform(-0.5, 0.5), circle[2])
        hit = lerp3(src, tg, 0.72)                              # руки перехватывают на подлёте
        sc.sound(ts, "entity.player.attack.sweep", 0.6, 1.8)
        imp = sc.shard(ts, src, hit, 8, "packed_ice", glow=0x9FD8FF, trail=ICE)
        hand = hands[i]
        sc.pose(imp - 3, cult, 3, merge={hand: cult.aim(hand, hit, rigs.HAND_LEN)})
        sc.particle(imp, ICE_BLOCK, hit, (0.3, 0.3, 0.3), 0.1, 30)
        sc.particle(imp, "snowflake", hit, (0.2, 0.2, 0.2), 0.15, 15)
        sc.particle(imp, SHADOW, hit, (0.3, 0.3, 0.3), 0, 10)
        sc.sound(imp, "block.glass.break", 1, 0.8 + i * 0.08)
        tips.append(imp)
    t = max(tips) + 6                                           # ~760
    sc.cam(t, (0.8, 2.0, 4.3), (0, 1.75, 7), 0)
    cult_hands(t, fan, 8, extra_pose=MAD_A)
    sc.sound(t, "entity.witch.celebrate", 1, 0.5)
    sc.say(t + 2, "Безумец", "dark_green", "Бесполезно! БЕС-ПО-ЛЕЗ-НО!", 40)

    # руки бросаются на волшебницу
    t = 790
    sc.cam(t, (-4.5, 2.6, -2.5), (1, 1.2, 2.5), 0)
    sc.pose(t, cult, 0, pose=MAD_B)
    aim_mage = [(1.2 + random.uniform(-0.4, 0.4), random.uniform(0.6, 2), 0.6 + 2.2) for _ in hands]
    cult_hands(t + 1, aim_mage, 10)
    sc.sound(t + 1, "entity.phantom.swoop", 1, 0.5)
    sc.pose(t + 2, mage, 4, pose=P(arm_r=(80, 0, -20), arm_l=(80, 0, -20), body=(-12, 0, 0), head=(10, 0, 0)))

    # ================================================================ 5. Горничная-они
    sc.pose(t + 1, maid, 0, visible=True, pose=CROUCH, show=["flail"], face=(0, 0, 2))
    sc.sound(t + 1, "entity.ravager.roar", 0.6, 1.6)
    sc.pose(t + 2, maid, 4, pose=AIR, show=["horn"])
    sc.move(t + 3, maid, (-3, 5, -3), 9, turn=False)
    sc.particle(t + 3, "cloud", (-7, 0.2, -12), (0.4, 0.1, 0.4), 0.05, 12)
    for k in range(t + 3, t + 22):
        sc.particle(k, "end_rod", lerp3((-7, 2, -12), (0.8, 1.5, 2.8), (k - t - 3) / 19), (0.1, 0.1, 0.1), 0.02, 2)
    sc.move(t + 12, maid, (0.8, 0, 2.8), 8, turn=False)
    sc.pose(t + 15, maid, 5, pose=SMASH)
    imp = t + 20                                                # 810
    sc.cam(imp - 2, (4.5, 1.3, 0.5), (0.8, 0.9, 3.2), 0)
    sc.cam(imp - 1, (4.9, 1.6, 0.0), (0.8, 0.9, 3.2), 30)
    sc.particle(imp, "explosion_emitter", (0.6, 0.2, 3.6), count=1)
    sc.particle(imp, STONE_BLOCK, (0.6, 0.2, 3.6), (1.2, 0.2, 1.2), 0.2, 80)
    sc.sound(imp, "entity.generic.explode", 1, 0.8)
    sc.sound(imp, "entity.iron_golem.attack", 1, 0.6)
    for k in range(10):
        sc.ring(imp + k, "cloud", (0.6, 0.15, 3.6), 0.6 + k * 0.5, n=16 + k * 3, speed=0.01)
        sc.ring(imp + k, STONE_BLOCK, (0.6, 0.15, 3.6), 0.6 + k * 0.5, n=10)
    recoil = [(-3, 6, 10), (3, 6, 10), (-5, 4, 9), (5, 4, 9), (-3, 2, 10), (3, 2, 10)]
    cult_hands(imp, recoil, 6, extra_pose=MAD_C)
    sc.pose(imp + 14, cult, 8, merge=fold)
    sc.pose(imp + 22, cult, 0, hide=["hands"])
    sc.pose(imp + 4, mage, 8, pose=STAND, face=(0.8, 0, 2.8))
    sc.pose(imp + 8, maid, 8, pose=SPIN_BASE, face=CULT_SPOT)
    sc.say(imp + 10, "Горничная", "aqua", "Я не позволю вам их тронуть.", 45)
    for k in range(imp, imp + 90, 3):
        sc.particle(k, "end_rod", maid.point("head", (0, 38, 3)), (0.05, 0.05, 0.05), 0.01, 1)

    # вращение кистеня над головой
    t = 870
    sc.cam(t, (-3.2, 2.2, 1.5), (0.8, 1.8, 3.6), 0)
    sc.cam(t + 1, (-3.6, 2.6, 2.4), (0.8, 1.8, 3.6), 36)
    for k in range(10):
        sc.pose(t + k * 3, maid, 3, merge={"arm_r": (145, 90 * k, 0)})
        if k % 2 == 0:
            sc.sound(t + k * 3, "entity.player.attack.sweep", 0.8, 0.7)
        ball = maid.point("arm_r", (-5.5, -25, 0))
        sc.particle(t + k * 3, "crit", ball, (0.1, 0.1, 0.1), 0.1, 4)
    t += 30
    sc.move(t, maid, (0.2, 0, 4.4), 6, turn=False)
    sc.pose(t, maid, 5, pose=SMASH)
    hit = t + 6                                                 # ~906
    sc.particle(hit, "explosion", (0, 1, 6.6), (0.3, 0.3, 0.3), 0, 4)
    sc.particle(hit, "sweep_attack", (0, 1.2, 6.4), count=1)
    sc.sound(hit, "entity.generic.explode", 0.8, 1.1)
    sc.sound(hit, "entity.player.attack.knockback", 1, 0.6)
    sc.pose(hit, cult, 5, pose=P(body=(-35, 0, 0), head=(-30, 0, 0), arm_r=(100, 0, 60), arm_l=(100, 0, 60)))
    sc.move(hit, cult, (0, 0, 9.5), 7, turn=False)
    sc.pose(hit + 10, cult, 10, pose=MAD_A)
    sc.cam(hit + 12, (1.2, 1.9, 7.4), (0, 1.75, 9.5), 0)
    sc.sound(hit + 14, "entity.witch.celebrate", 1, 0.45)
    sc.say(hit + 14, "Безумец", "dark_green", "А-ха-ха-ха! Это ЛЮБОВЬ! Всё это — ЛЮБОВЬ!", 50)
    sc.move(hit + 20, maid, (1.9, 0, 4.6), 8, turn=False)
    sc.pose(hit + 28, maid, 6, pose=SPIN_BASE, face=(0, 0, 9.5))

    # ================================================================ 6. Удар в спину
    t6 = 975
    sc.cam(t6, (-2.6, 1.7, -4.8), (0, 1.3, -3.2), 0)
    sc.pose(t6, hero, 6, pose=STAND, face=(0, 0, 9.5))
    sc.pose(t6, cult, 0, merge={"h1": (0, 180, 0, 0.05)}, show=["hands"])
    sc.pose(t6 + 1, cult, 0, merge={h: (0, 180, 0, 0.0001) for h in hands[1:]})
    chest = (0.0, 1.25, -3.4)
    start = cult.aim("h1", chest, rigs.HAND_LEN)
    sc.pose(t6 + 2, cult, 0, merge={"h1": start[:3] + (0.05,)})
    sc.pose(t6 + 3, cult, 6, merge={"h1": start})
    sc.sound(t6 + 3, "entity.phantom.swoop", 1, 0.4)
    stab = t6 + 9                                              # 984
    sc.particle(stab, BLOOD, chest, (0.2, 0.25, 0.2), 0, 40)
    sc.particle(stab, "damage_indicator", chest, (0.2, 0.2, 0.2), 0.1, 6)
    sc.particle(stab, SHADOW, chest, (0.3, 0.3, 0.3), 0, 20)
    sc.sound(stab, "entity.player.hurt", 1, 0.7)
    sc.sound(stab, "item.trident.hit", 1, 0.6)
    sc.pose(stab, hero, 3, pose=HIT)
    sc.say(stab + 4, "Герой", "gold", "Кх... а...", 30)
    sc.pose(stab + 14, cult, 6, merge={"h1": start[:3] + (0.05,)})
    sc.pose(stab + 21, cult, 0, hide=["hands"])
    sc.cam(stab + 16, (0.6, 4.6, -1.8), (0, 0.2, -4.2), 0)
    sc.cam(stab + 17, (0.6, 5.6, -1.6), (0, 0.2, -4.2), 50)
    sc.pose(stab + 16, hero, 14, pose=P(body=(0, 0, 0), head=(-10, 0, 0), arm_r=(10, 0, 50), arm_l=(10, 0, 50)),
            tilt=-88, lift=0.12)
    sc.pose(stab + 18, mage, 6, pose=SCREAM, face=HERO_SPOT)
    sc.say(stab + 20, "Волшебница", "light_purple", "Не-е-ет!!!", 40)
    sc.sound(stab + 30, "entity.player.death", 1, 0.8)
    for k in range(stab + 2, stab + 70, 3):
        sc.particle(k, BLOOD, (0, 0.1, -3.5 + 0.2), (0.5, 0.02, 0.5), 0, 4)

    # ================================================================ 7. Возвращение смертью
    t7 = 1060
    sc.effect(t7, "blindness", 3)
    sc.effect(t7, "darkness", 14)
    sc.sound(t7, "block.beacon.deactivate", 1, 0.5)
    sc.sound(t7, "entity.warden.heartbeat", 1, 0.6)
    b = t7 + 6
    for c in (mage, maid, cult):
        sc.pose(b, c, 0, visible=False)
    sc.pose(b, hero, 0, pose=LIMP, tilt=0, lift=0, yaw=0)
    sc.cam(b, (2.6, 1.9, -0.2), (0, 1.35, -2.9), 0)
    sc.cam(b + 1, (2.0, 1.8, -0.6), (0, 1.35, -2.9), 50)
    sc.add(b, "time set 18000")
    for k in range(b, b + 230, 3):                               # пустота и тени
        for _ in range(3):
            a = random.uniform(0, 2 * math.pi)
            r = random.uniform(3.5, 7)
            sc.particle(k, SHADOW, (math.cos(a) * r, random.uniform(0, 4), -3.5 + math.sin(a) * r), (0.3, 0.3, 0.3), 0, 3)
        sc.ring(k, "squid_ink", (0, 0.05, -3.5), 2.4 + 0.3 * math.sin(k / 5), n=8, d=(0.05, 0.02, 0.05), speed=0.005)
        sc.particle(k, "reverse_portal", (0, 1, -3.5), (2, 1, 2), 0.02, 4)
    # сердце
    hb = t7 + 40                                               # 1100
    sc.pose(hb, heart, 20, visible=True, scale=1.0)
    sc.particle(hb, VIOLET, (0, 1.25, -2.3), (0.3, 0.3, 0.3), 0, 20)
    beats = list(range(hb + 20, hb + 120, 16)) + list(range(hb + 120, hb + 160, 8))
    for i, k in enumerate(beats):
        sc.pose(k, heart, 2, scale=1.3 if k < hb + 120 else 1.0, merge={"body": (0, (i * 25) % 360, 0)})
        sc.pose(k + 3, heart, 5, scale=1.0 if k < hb + 120 else 0.7)
        sc.sound(k, "entity.warden.heartbeat", 1.2, 0.9 if k < hb + 120 else 1.3)
        sc.particle(k, BLOOD, (0, 1.25, -2.3), (0.15, 0.15, 0.15), 0, 6)
    # руки ведьмы выползают из тьмы
    wh = ["hl", "hr", "hb"]
    th = t7 + 60                                              # 1120
    sc.pose(th, witch, 0, visible=True, show=["hands"], merge={h: (0, 0, 0, 0.02) for h in wh})
    hc = (0, 1.3, -2.3)
    near = [(0.9, 1.4, -2.3), (-0.9, 1.4, -2.3), (0, 0.7, -2.0)]
    far = [(3.5, 3, -1), (-3.5, 3, -1), (0, -0.5, 0)]
    sc.pose(th + 1, witch, 10, merge={h: witch.aim(h, f, rigs.HAND_LEN)[:3] + (0.2,) for h, f in zip(wh, far)})
    sc.pose(th + 12, witch, 50, merge={h: witch.aim(h, n_, rigs.HAND_LEN) for h, n_ in zip(wh, near)})
    sc.sound(th, "entity.elder_guardian.curse", 0.7, 0.4)
    sc.sound(th + 30, "ambient.soul_sand_valley.mood", 1, 0.6)
    for k in range(th + 12, th + 62, 4):
        tips_fx(k, witch, wh, VIOLET, 4)
    whisper = "Я люблю тебя"
    sc.cam(th + 20, (0.4, 1.6, -0.5), (0, 1.3, -2.3), 0)
    sc.cam(th + 21, (0.3, 1.5, -1.0), (0, 1.3, -2.3), 50)
    sc.card(th + 22, "", "dark_purple", whisper + "...", "dark_purple", 10, 30, 10)
    sc.card(th + 62, "", "dark_purple", (whisper + ". ") * 2, "dark_purple", 5, 30, 5)
    sc.card(th + 100, "", "dark_purple", (whisper + ". ") * 3, "light_purple", 3, 30, 3)
    sc.pose(th + 62, hero, 12, pose=CLUTCH)
    # сжатие сердца
    sq = th + 120                                             # 1240
    clasp = [(0.25, 1.35, -2.3), (-0.25, 1.35, -2.3), (0, 0.95, -2.3)]
    sc.pose(sq, witch, 8, merge={h: witch.aim(h, c_, rigs.HAND_LEN) for h, c_ in zip(wh, clasp)})
    sc.pose(sq + 8, heart, 6, scale=0.35)
    sc.card(sq + 6, "Я ЛЮБЛЮ ТЕБЯ", "dark_red", "", "dark_red", 0, 25, 5, extra=',"obfuscated":false')
    sc.sound(sq + 8, "entity.warden.heartbeat", 1.5, 0.5)
    sc.sound(sq + 10, "block.respawn_anchor.deplete", 1, 0.6)
    sc.particle(sq + 12, BLOOD, hc, (0.3, 0.3, 0.3), 0.05, 60)
    sc.pose(sq + 12, hero, 4, pose=P(body=(-25, 0, 0), head=(-40, 0, 0), arm_r=(30, 0, 50), arm_l=(30, 0, 50)))
    # вспышка и перемотка времени
    fl = sq + 30                                              # 1270
    sc.sound(fl, "entity.lightning_bolt.thunder", 0.7, 1.6)
    sc.sound(fl, "block.portal.travel", 0.4, 2.0)
    sc.particle(fl, "flash", (0, 1.4, -2.3), count=3) if False else None
    sc.particle(fl, "end_rod", (0, 1.4, -2.6), (0.1, 0.1, 0.1), 0.6, 200)
    sc.particle(fl, "firework", (0, 1.4, -2.6), (0.1, 0.1, 0.1), 0.4, 120)
    for k in range(fl, fl + 40, 1):
        a = k * 0.5
        for j in range(3):
            aa = a + j * 2.094
            rr = 3 - (k - fl) * 0.06
            sc.particle(k, "reverse_portal", (math.cos(aa) * rr, 0.2 + (k - fl) * 0.08, -3.5 + math.sin(aa) * rr), count=3)
            sc.particle(k, "enchant", (math.cos(-aa) * rr, 3 - (k - fl) * 0.06, -3.5 + math.sin(-aa) * rr), count=2)
        if (k - fl) % 4 == 0:
            sc.sound(k, "block.note_block.hat", 1, 2.0 - (k - fl) * 0.03)
    sc.card(fl + 4, "ВОЗВРАЩЕНИЕ СМЕРТЬЮ", "dark_purple", "Return by Death", "light_purple", 5, 45, 15)
    sc.effect(fl + 34, "blindness", 2)

    # ================================================================ 8. Снова та же сцена
    t8 = fl + 40                                             # 1310
    sc.add(t8, "effect clear @a[tag=rz.viewer] minecraft:darkness", "time set 13000")
    sc.pose(t8, witch, 0, visible=False, hide=["hands"])
    sc.pose(t8, heart, 0, visible=False)
    sc.move(t8, mage, MAGE_SPOT, 0, turn=False)
    sc.pose(t8, mage, 0, visible=True, pose=P(arm_l=(45, 0, -10), head=(-5, 0, 0)), face=HERO_SPOT)
    sc.move(t8, maid, (-7, 0, -12), 0, turn=False)
    sc.pose(t8, maid, 0, visible=False, hide=["horn", "flail"])
    sc.move(t8, cult, (CULT_SPOT[0], -2.3, CULT_SPOT[2]), 0, turn=False)
    sc.pose(t8, hero, 0, pose=SHOCK, face=MAGE_SPOT, tilt=0, lift=0)
    sc.cam(t8, (4.2, 1.8, -4.9), (1.1, 1.3, -2.2), 0)           # общий план сбоку
    sc.cam(t8 + 1, (3.8, 1.75, -4.4), (1.1, 1.3, -2.2), 28)
    sc.cam(t8 + 29, (0.8, 1.75, -2.9), (2.2, 1.55, -1.0), 0)
    sc.cam(t8 + 30, (1.0, 1.72, -2.6), (2.2, 1.55, -1.0), 50)
    for k in range(t8, t8 + 230, 4):
        sc.particle(k, "snowflake", (random.uniform(-3, 4), 2.8, random.uniform(-5, 1)), (1.5, 0.4, 1.5), 0.01, 3)
    sc.sound(t8 + 4, "entity.player.breath", 1, 0.8)
    sc.say(t8 + 30, "Волшебница", "light_purple", LINE, 60)
    sc.pose(t8 + 70, hero, 6, merge={"head": (10, 30, 0)})
    sc.pose(t8 + 80, hero, 6, merge={"head": (10, -30, 0)})
    sc.pose(t8 + 90, hero, 6, merge={"head": (15, 0, 0)})
    sc.cam(t8 + 95, (-1.0, 1.75, -1.8), (0, 1.6, -3.5), 0)
    sc.cam(t8 + 96, (-0.6, 1.7, -2.2), (0, 1.6, -3.5), 60)
    sc.say(t8 + 98, "Герой", "gold", "Я... вернулся? Снова этот момент...", 55)
    sc.pose(t8 + 160, hero, 8, pose=RESOLVE)
    sc.sound(t8 + 160, "block.respawn_anchor.set_spawn", 0.8, 1.2)
    sc.say(t8 + 162, "Герой", "gold", "В этот раз... я спасу всех. Даже если придётся умереть снова.", 70)
    end = t8 + 250
    sc.cam(end - 20, (6, 4.5, -9), (1, 1.2, -2), 0)
    sc.cam(end - 19, (8, 6, -12), (1, 1.2, -2), 50)
    sc.card(end - 10, "Re:Блок", "dark_purple", "Продолжение следует...", "gray", 20, 60, 20)
    sc.add(end + 60, "function rz:finish")
    return sc


# ---------------------------------------------------------------- сборка
STATIC = {
    "load": """scoreboard objectives add rz.t dummy
scoreboard players add #play rz.t 0
tellraw @a ["",{"text":"[Re:Блок] ","color":"dark_purple","bold":true},{"text":"Анимация загружена. ","color":"gray"},{"text":"/function rz:start","color":"light_purple","clickEvent":{"action":"suggest_command","value":"/function rz:start"}},{"text":" — кино с камерой, ","color":"gray"},{"text":"/function rz:play","color":"light_purple","clickEvent":{"action":"suggest_command","value":"/function rz:play"}},{"text":" — свободный обзор.","color":"gray"}]
""",
    "tick": """execute if score #play rz.t matches 1 as @e[type=marker,tag=rz.stage,limit=1] at @s run function rz:run
""",
    "run": """scoreboard players add #t rz.t 1
function rz:d/root
execute if score #cam rz.t matches 1 if score #t rz.t matches 3.. as @a[tag=rz.viewer,gamemode=spectator] run spectate @e[type=item_display,tag=rz.cam,limit=1] @s
""",
    "start": """# Кино-режим: игроки становятся зрителями и смотрят через камеру.
function rz:play
scoreboard players set #cam rz.t 1
tag @a[gamemode=survival] add rz.gm_s
tag @a[gamemode=creative] add rz.gm_c
tag @a[gamemode=adventure] add rz.gm_a
gamemode spectator @a[tag=rz.viewer]
""",
    "play": """# Свободный режим: сцена разворачивается вокруг точки запуска, смотри как хочешь.
function rz:cleanup
tag @a add rz.viewer
scoreboard players set #cam rz.t 0
scoreboard players set #t rz.t 0
execute align xz positioned ~0.5 ~ ~0.5 run summon marker ~ ~ ~ {Tags:["rz.e","rz.stage"]}
execute align xz positioned ~0.5 ~ ~0.5 run summon item_display ~ ~1.6 ~-10 {Tags:["rz.e","rz.cam"],teleport_duration:0}
scoreboard players set #play rz.t 1
tellraw @a[tag=rz.viewer] {"text":"[Re:Блок] Мотор!","color":"dark_purple"}
""",
    "finish": """# Конец: вернуть игроков и убрать сущности.
execute as @a[tag=rz.viewer,gamemode=spectator] run spectate
execute at @e[type=marker,tag=rz.stage,limit=1] run tp @a[tag=rz.viewer] ~ ~ ~-8 facing ~ ~1 ~
gamemode survival @a[tag=rz.gm_s]
gamemode creative @a[tag=rz.gm_c]
gamemode adventure @a[tag=rz.gm_a]
effect clear @a[tag=rz.viewer] minecraft:darkness
effect clear @a[tag=rz.viewer] minecraft:blindness
time set 13000
function rz:cleanup
tellraw @a {"text":"[Re:Блок] Конец. Перезапуск: /function rz:start","color":"dark_purple"}
""",
    "stop": """# Прервать анимацию в любой момент.
function rz:finish
""",
    "cleanup": """scoreboard players set #play rz.t 0
kill @e[tag=rz.e]
tag @a remove rz.viewer
tag @a remove rz.gm_s
tag @a remove rz.gm_c
tag @a remove rz.gm_a
""",
}

BUILD_STAGE = """# Площадь в средневековом городе (около 25x27 блоков) вокруг тебя.
# ВНИМАНИЕ: заменяет блоки в этой области! Запускай на ровном пустом месте.
execute align xz run fill ~-12 ~ ~-14 ~12 ~12 ~12 air
execute align xz run fill ~-12 ~-1 ~-14 ~12 ~-1 ~12 stone_bricks
execute align xz run fill ~-11 ~-1 ~-13 ~11 ~-1 ~11 polished_andesite
execute align xz run fill ~-1 ~-1 ~-13 ~1 ~-1 ~11 smooth_stone
execute align xz run fill ~-11 ~-1 ~-1 ~11 ~-1 ~1 smooth_stone
execute align xz run fill ~-11 ~-1 ~-11 ~-11 ~-1 ~9 cracked_stone_bricks
execute align xz run fill ~11 ~-1 ~-11 ~11 ~-1 ~9 cracked_stone_bricks
# фонари
execute align xz run fill ~-6 ~ ~-6 ~-6 ~2 ~-6 dark_oak_fence
execute align xz run setblock ~-6 ~3 ~-6 lantern
execute align xz run fill ~6 ~ ~-6 ~6 ~2 ~-6 dark_oak_fence
execute align xz run setblock ~6 ~3 ~-6 lantern
execute align xz run fill ~-6 ~ ~6 ~-6 ~2 ~6 dark_oak_fence
execute align xz run setblock ~-6 ~3 ~6 lantern
execute align xz run fill ~6 ~ ~6 ~6 ~2 ~6 dark_oak_fence
execute align xz run setblock ~6 ~3 ~6 lantern
# торговые лавки
execute align xz run fill ~-10 ~ ~-9 ~-10 ~2 ~-9 spruce_fence
execute align xz run fill ~-10 ~ ~-5 ~-10 ~2 ~-5 spruce_fence
execute align xz run fill ~-11 ~3 ~-9 ~-9 ~3 ~-5 red_wool
execute align xz run fill ~-10 ~ ~-8 ~-10 ~ ~-6 barrel
execute align xz run fill ~-10 ~ ~4 ~-10 ~2 ~4 spruce_fence
execute align xz run fill ~-10 ~ ~8 ~-10 ~2 ~8 spruce_fence
execute align xz run fill ~-11 ~3 ~4 ~-9 ~3 ~8 yellow_wool
execute align xz run fill ~-10 ~ ~5 ~-10 ~ ~7 hay_block
execute align xz run fill ~10 ~ ~-12 ~10 ~2 ~-12 spruce_fence
execute align xz run fill ~10 ~ ~-8 ~10 ~2 ~-8 spruce_fence
execute align xz run fill ~9 ~3 ~-12 ~11 ~3 ~-8 blue_wool
execute align xz run fill ~10 ~ ~-11 ~10 ~ ~-9 composter
# стены домов по краям
execute align xz run fill ~-12 ~ ~12 ~12 ~5 ~12 white_terracotta
execute align xz run fill ~-12 ~ ~12 ~12 ~0 ~12 cobblestone
execute align xz run fill ~-12 ~6 ~12 ~12 ~6 ~12 dark_oak_planks
execute align xz run fill ~-12 ~ ~-14 ~-12 ~5 ~12 white_terracotta
execute align xz run fill ~12 ~ ~-14 ~12 ~5 ~12 white_terracotta
execute align xz run fill ~-12 ~0 ~-14 ~-12 ~0 ~12 cobblestone
execute align xz run fill ~12 ~0 ~-14 ~12 ~0 ~12 cobblestone
tellraw @s {"text":"[Re:Блок] Площадь построена. Встань в центр и запусти /function rz:start","color":"dark_purple"}
"""


def write_pack(sc):
    out = os.path.join(ROOT, "datapack", PACK_NAME)
    if os.path.exists(out):
        shutil.rmtree(out)
    fdir = os.path.join(out, "data", NS, "function")
    os.makedirs(os.path.join(fdir, "f"), exist_ok=True)
    os.makedirs(os.path.join(fdir, "d"), exist_ok=True)

    def emit(name, text):
        path = os.path.join(fdir, name + ".mcfunction")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w", encoding="utf-8") as f:
            f.write(text)

    with open(os.path.join(out, "pack.mcmeta"), "w", encoding="utf-8") as f:
        json.dump({"pack": {"description": "§5Re:Блок§7 — анимация «Возвращение смертью»",
                            "pack_format": 48}}, f, ensure_ascii=False, indent=2)
    tags = os.path.join(out, "data", "minecraft", "tags", "function")
    os.makedirs(tags, exist_ok=True)
    for n in ("load", "tick"):
        with open(os.path.join(tags, n + ".json"), "w") as f:
            json.dump({"values": [f"{NS}:{n}"]}, f, indent=2)

    for name, text in STATIC.items():
        emit(name, text)
    emit("build_stage", BUILD_STAGE)
    ticks = sorted(sc.frames)
    for t in ticks:
        emit(f"f/{t}", "\n".join(sc.frames[t]) + "\n")
    dispatch_tree(ticks, emit)

    os.makedirs(os.path.join(ROOT, "dist"), exist_ok=True)
    zpath = os.path.join(ROOT, "dist", PACK_NAME + ".zip")
    with zipfile.ZipFile(zpath, "w", zipfile.ZIP_DEFLATED) as z:
        for base, _, files in os.walk(out):
            for fn in sorted(files):
                full = os.path.join(base, fn)
                z.write(full, os.path.relpath(full, out))
    n_cmd = sum(len(v) for v in sc.frames.values())
    print(f"frames={len(ticks)} commands={n_cmd} length={sc.length} ticks (~{sc.length / 20:.0f} s)")
    print("pack:", out)
    print("zip: ", zpath)


if __name__ == "__main__":
    write_pack(build_scene())
