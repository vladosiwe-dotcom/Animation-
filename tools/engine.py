"""Движок анимации: риги из block_display, позы, таймлайн -> датапак.

Каждая часть тела — отдельный block_display. Все части персонажа стоят
в одной точке (корень персонажа), а форма/поза задаётся полной матрицей
4x4 в `transformation`. Матрицы считаются здесь, в Python, поэтому в игре
нет никакой математики — только `data merge` с интерполяцией и `tp`
с `teleport_duration`.
"""
import math

NS = "rz"
HIDDEN = 1e-4  # масштаб «невидимой» части


# ---------------------------------------------------------------- матрицы
def mmul(a, b):
    return [[sum(a[i][k] * b[k][j] for k in range(4)) for j in range(4)] for i in range(4)]


def chain(*ms):
    r = ms[0]
    for m in ms[1:]:
        r = mmul(r, m)
    return r


def I():
    return [[1.0 if i == j else 0.0 for j in range(4)] for i in range(4)]


def T(x, y, z):
    m = I()
    m[0][3], m[1][3], m[2][3] = x, y, z
    return m


def S(x, y=None, z=None):
    if y is None:
        y = z = x
    m = I()
    m[0][0], m[1][1], m[2][2] = x, y, z
    return m


def Rx(d):
    c, s = math.cos(math.radians(d)), math.sin(math.radians(d))
    return [[1, 0, 0, 0], [0, c, -s, 0], [0, s, c, 0], [0, 0, 0, 1]]


def Ry(d):
    c, s = math.cos(math.radians(d)), math.sin(math.radians(d))
    return [[c, 0, s, 0], [0, 1, 0, 0], [-s, 0, c, 0], [0, 0, 0, 1]]


def Rz(d):
    c, s = math.cos(math.radians(d)), math.sin(math.radians(d))
    return [[c, -s, 0, 0], [s, c, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]]


def apply(m, v):
    return tuple(m[i][0] * v[0] + m[i][1] * v[1] + m[i][2] * v[2] + m[i][3] for i in range(3))


def apply_dir(m, v):
    return tuple(m[i][0] * v[0] + m[i][1] * v[1] + m[i][2] * v[2] for i in range(3))


def fnum(v):
    s = f"{v:.4f}".rstrip("0").rstrip(".")
    return "0" if s in ("-0", "") else s


def mat_nbt(m):
    return "[" + ",".join(fnum(m[i][j]) + "f" for i in range(4) for j in range(4)) + "]"


def yaw_to(src, dst):
    """Угол Ry, поворачивающий модель (смотрит в +Z) от src к dst."""
    return math.degrees(math.atan2(dst[0] - src[0], dst[2] - src[2]))


def lerp3(a, b, k):
    return tuple(a[i] + (b[i] - a[i]) * k for i in range(3))


def rel(p):
    return " ".join("~" + ("" if abs(c) < 1e-9 else fnum(c)) for c in p)


# ---------------------------------------------------------------- риги
class Joint:
    """kind: up (тело/голова, x>0 = наклон вперёд), down (руки/ноги, x>0 = мах вперёд,
    z>0 = в сторону от тела), fwd (теневые руки: смотрит в +Z, s = растяжение по длине)."""

    def __init__(self, kind, pivot, parent=None, side=1):
        self.kind, self.pivot, self.parent, self.side = kind, pivot, parent, side

    def local(self, a):
        x, y, z = a[0], a[1], a[2]
        p = self.pivot
        if self.kind == "up":
            r = chain(Ry(y), Rx(x), Rz(z))
        elif self.kind == "down":
            r = chain(Ry(y), Rx(-x), Rz(self.side * z))
        elif self.kind == "fwd":
            s = a[3] if len(a) > 3 else 1.0
            r = chain(Ry(y), Rx(x), S(1, 1, s))
        else:
            r = I()
        return chain(T(*p), r, T(-p[0], -p[1], -p[2]))


class Box:
    def __init__(self, name, joint, lo, hi, block, group=None, glow=None, bright=15):
        self.name, self.joint, self.lo, self.hi = name, joint, lo, hi
        self.block, self.group, self.glow, self.bright = block, group, glow, bright


class Rig:
    def __init__(self, name, px):
        self.name, self.px = name, px
        self.joints = {}
        self.boxes = []

    def joint(self, name, kind, pivot_px, parent=None, side=1):
        self.joints[name] = Joint(kind, tuple(c * self.px for c in pivot_px), parent, side)

    def box(self, joint, x0, x1, y0, y1, z0, z1, block, group=None, glow=None, name=None):
        p = self.px
        n = name or f"p{len(self.boxes)}"
        self.boxes.append(Box(n, joint, (x0 * p, y0 * p, z0 * p), (x1 * p, y1 * p, z1 * p), block, group, glow))

    def joint_matrix(self, jname, pose, cache):
        if jname in cache:
            return cache[jname]
        j = self.joints[jname]
        loc = j.local(pose.get(jname, (0, 0, 0)))
        m = chain(self.joint_matrix(j.parent, pose, cache), loc) if j.parent else loc
        cache[jname] = m
        return m


def P(**joints):
    """Поза: P(arm_r=(x,y,z), head=(...)). Нет сустава = ноль."""
    return dict(joints)


def humanoid(name, px=0.055):
    r = Rig(name, px)
    r.joint("body", "up", (0, 12, 0))
    r.joint("head", "up", (0, 24, 0), "body")
    r.joint("arm_r", "down", (-5.5, 22.5, 0), "body", side=-1)
    r.joint("arm_l", "down", (5.5, 22.5, 0), "body", side=1)
    r.joint("leg_r", "down", (-2, 12, 0), None, side=-1)
    r.joint("leg_l", "down", (2, 12, 0), None, side=1)
    return r


# ---------------------------------------------------------------- состояние персонажа
class Char:
    def __init__(self, scene, rig, pos, yaw=0.0, visible=True, groups=()):
        self.scene, self.rig, self.name = scene, rig, rig.name
        self.pos = tuple(pos)
        self.yaw, self.tilt, self.lift, self.scale = yaw, 0.0, 0.0, 1.0
        self.pose = {}
        self.visible = visible
        self.groups = set(groups)
        self.last = {}  # имя части -> последняя отправленная матрица
        self.tp_dur = 0

    def root(self):
        return chain(T(0, self.lift, 0), Ry(self.yaw), Rx(self.tilt), S(self.scale))

    def matrices(self):
        cache = {}
        root = self.root()
        out = {}
        for b in self.rig.boxes:
            vis = self.visible and (b.group is None or b.group in self.groups)
            if not vis:
                out[b.name] = S(HIDDEN)
                continue
            jm = self.rig.joint_matrix(b.joint, self.pose, cache)
            size = tuple(b.hi[i] - b.lo[i] for i in range(3))
            out[b.name] = chain(root, jm, T(*b.lo), S(*size))
        return out

    def point(self, joint, local_px):
        """Мировая (относительно сцены) точка на суставе — для частиц."""
        jm = self.rig.joint_matrix(joint, self.pose, {})
        p = self.rig.px
        v = apply(chain(self.root(), jm), tuple(c * p for c in local_px))
        return tuple(self.pos[i] + v[i] for i in range(3))

    def aim(self, joint, target, length_px):
        """Углы и растяжение для fwd-сустава, чтобы кончик (length_px по +Z) достал target."""
        j = self.rig.joints[joint]
        parent = self.rig.joint_matrix(j.parent, self.pose, {}) if j.parent else I()
        w = chain(self.root(), parent, T(*j.pivot))
        origin = apply(w, (0, 0, 0))
        d = tuple(target[i] - self.pos[i] - origin[i] for i in range(3))
        # обратный поворот (матрица ортогональна * scale)
        sc2 = sum(w[i][0] ** 2 for i in range(3))
        dl = tuple(sum(w[k][i] * d[k] for k in range(3)) / sc2 for i in range(3))
        dist = math.sqrt(sum(c * c for c in dl))
        yaw = math.degrees(math.atan2(dl[0], dl[2]))
        pitch = math.degrees(math.atan2(dl[1], math.hypot(dl[0], dl[2])))
        return (-pitch, yaw, 0, dist / (length_px * self.rig.px))

    def tag(self, part=None):
        return f"{NS}.{self.name}" + (f".{part}" if part else "")

    def sel(self, part=None):
        return f"@e[type=block_display,tag={self.tag(part)}" + (",limit=1]" if part else "]")


# ---------------------------------------------------------------- сцена / таймлайн
class Scene:
    def __init__(self):
        self.frames = {}
        self.chars = []
        self.length = 0
        self.uid = 0
        self.log = []  # (t, kind, ...) — для превью-рендера

    def add(self, t, *cmds):
        t = max(1, int(t))
        self.frames.setdefault(t, []).extend(cmds)
        self.length = max(self.length, t)

    def char(self, rig, pos, yaw=0.0, visible=True, groups=()):
        c = Char(self, rig, pos, yaw, visible, groups)
        self.chars.append(c)
        return c

    # --- спавн всех персонажей на тике 1
    def spawn_all(self):
        for c in self.chars:
            self.log.append((1, 'pos', c.name, c.pos, 0))
            mats = c.matrices()
            for b in c.rig.boxes:
                m = mats[b.name]
                c.last[b.name] = m
                self.log.append((1, 'mat', c.name, b.name, m, 0, b.block, b.glow))
                extra = ""
                if b.glow is not None:
                    extra = f",Glowing:1b,glow_color_override:{b.glow}"
                self.add(
                    1,
                    f'summon block_display {rel(c.pos)} {{Tags:["{NS}.e","{c.tag()}","{c.tag(b.name)}"],'
                    f'block_state:{{Name:"minecraft:{b.block}"}},transformation:{mat_nbt(m)},'
                    f"brightness:{{sky:15,block:{b.bright}}},view_range:2f{extra}}}",
                )

    # --- позы / состояние
    def pose(self, t, c, dur=0, pose=None, merge=None, yaw=None, face=None, tilt=None, lift=None,
             scale=None, show=(), hide=(), visible=None):
        if pose is not None:
            c.pose = dict(pose)
        if merge:
            c.pose.update(merge)
        if face is not None:
            c.yaw = yaw_to(c.pos, face)
        if yaw is not None:
            c.yaw = yaw
        if tilt is not None:
            c.tilt = tilt
        if lift is not None:
            c.lift = lift
        if scale is not None:
            c.scale = scale
        c.groups |= set(show)
        c.groups -= set(hide)
        if visible is not None:
            c.visible = visible
        mats = c.matrices()
        for name, m in mats.items():
            old = c.last.get(name)
            if old is not None and all(abs(m[i][j] - old[i][j]) < 1e-4 for i in range(3) for j in range(4)):
                continue
            c.last[name] = m
            self.log.append((max(1, int(t)), 'mat', c.name, name, m, int(dur), None, None))
            self.add(t, f"data merge entity {c.sel(name)} {{transformation:{mat_nbt(m)},"
                        f"start_interpolation:0,interpolation_duration:{int(dur)}}}")

    def _tp(self, t, c, to, dur):
        self.log.append((max(1, int(t)), 'pos', c.name, tuple(to), dur))
        if c.tp_dur != dur:
            self.add(t - 1, f"execute as {c.sel()} run data merge entity @s {{teleport_duration:{dur}}}")
            c.tp_dur = dur
        self.add(t, f"tp {c.sel()} {rel(to)}")

    def move(self, t, c, to, dur, turn=True):
        """Перемещение с плавной интерполяцией (длинные пути режутся на куски ≤ 50 тиков)."""
        to = tuple(to)
        if turn and (abs(to[0] - c.pos[0]) + abs(to[2] - c.pos[2])) > 0.05:
            self.pose(t, c, 4, face=(to[0], c.pos[1], to[2]))
        start = c.pos
        n = max(1, math.ceil(dur / 50)) if dur > 0 else 1
        for k in range(n):
            seg_t = t + round(dur * k / n)
            seg_d = round(dur * (k + 1) / n) - round(dur * k / n)
            dst = lerp3(start, to, (k + 1) / n)
            self._tp(seg_t, c, dst, seg_d)
        c.pos = to

    def walk(self, t, c, to, speed=0.1, base=None, step=6):
        to = tuple(to)
        dist = math.dist(c.pos, to)
        dur = max(1, round(dist / speed))
        self.move(t, c, to, dur)
        a = P(leg_r=(28, 0, 0), leg_l=(-28, 0, 0), arm_r=(-22, 0, 4), arm_l=(22, 0, 4), body=(4, 0, 0))
        b = P(leg_r=(-28, 0, 0), leg_l=(28, 0, 0), arm_r=(22, 0, 4), arm_l=(-22, 0, 4), body=(4, 0, 0))
        k = 0
        for tt in range(t, t + dur, step):
            self.pose(tt, c, step, pose=dict(base or {}), merge=(a if k % 2 == 0 else b))
            k += 1
        self.pose(t + dur, c, 5, pose=base or {})
        return t + dur

    # --- камера
    def cam(self, t, pos, look, dur=0):
        self._cam_dur = getattr(self, "_cam_dur", -1)
        if self._cam_dur != dur:
            self.add(t - 1, f"data merge entity @e[type=item_display,tag={NS}.cam,limit=1] {{teleport_duration:{dur}}}")
            self._cam_dur = dur
        self.add(t, f"tp @e[type=item_display,tag={NS}.cam,limit=1] {rel(pos)} facing {rel(look)}")
        self.log.append((max(1, int(t)), 'cam', tuple(pos), tuple(look), dur))

    def cam_path(self, t, a, b, look_a, look_b, dur):
        n = max(1, math.ceil(dur / 50))
        self.cam(t, a, look_a, 0)
        for k in range(n):
            tt = t + 1 + round(dur * k / n)
            d = round(dur * (k + 1) / n) - round(dur * k / n)
            self.cam(tt, lerp3(a, b, (k + 1) / n), lerp3(look_a, look_b, (k + 1) / n), d)

    # --- текст и звук
    def say(self, t, who, color, text, stay=60):
        V = f"@a[tag={NS}.viewer]"
        self.add(t, f"title {V} times 4 {stay} 8",
                 f'title {V} subtitle ["",{{"text":"{who}: ","color":"{color}","bold":true}},{{"text":"{text}","color":"white"}}]',
                 f'title {V} title {{"text":""}}')

    def card(self, t, title, color, sub="", sub_color="gray", fade_in=20, stay=60, fade_out=20, extra=""):
        V = f"@a[tag={NS}.viewer]"
        self.add(t, f"title {V} times {fade_in} {stay} {fade_out}",
                 f'title {V} subtitle {{"text":"{sub}","color":"{sub_color}","italic":true}}',
                 f'title {V} title {{"text":"{title}","color":"{color}","bold":true{extra}}}')

    def sound(self, t, name, vol=1.0, pitch=1.0):
        self.add(t, f"execute as @a[tag={NS}.viewer] at @s run playsound minecraft:{name} master @s ~ ~ ~ {fnum(vol)} {fnum(pitch)}")

    def effect(self, t, eff, seconds, amp=0):
        self.add(t, f"effect give @a[tag={NS}.viewer] minecraft:{eff} {seconds} {amp} true")

    # --- частицы
    def particle(self, t, p, pos, d=(0, 0, 0), speed=0, count=1):
        self.log.append((max(1, int(t)), 'pt', p, tuple(pos), tuple(d), speed, count))
        self.add(t, f"particle {p} {rel(pos)} {fnum(d[0])} {fnum(d[1])} {fnum(d[2])} {fnum(speed)} {count} force @a")

    def ring(self, t, p, center, r, n=24, axis="y", speed=0, count=1, d=(0, 0, 0)):
        for i in range(n):
            a = 2 * math.pi * i / n
            ca, sa = math.cos(a) * r, math.sin(a) * r
            off = {"y": (ca, 0, sa), "z": (ca, sa, 0), "x": (0, ca, sa)}[axis]
            self.particle(t, p, tuple(center[k] + off[k] for k in range(3)), d, speed, count)

    def line(self, t, p, a, b, n=10, d=(0, 0, 0), speed=0, count=1):
        for i in range(n + 1):
            self.particle(t, p, lerp3(a, b, i / n), d, speed, count)

    # --- снаряды (ледяные копья и т.п.)
    def shard(self, t, start, end, dur, block, length=1.4, width=0.22, glow=None, trail=None, roll=45):
        self.uid += 1
        tag = f"{NS}.fx{self.uid}"
        d = tuple(end[i] - start[i] for i in range(3))
        yaw = math.degrees(math.atan2(d[0], d[2]))
        pitch = math.degrees(math.atan2(d[1], math.hypot(d[0], d[2])))
        m = chain(Ry(yaw), Rx(-pitch), Rz(roll), T(-width / 2, -width / 2, -length / 2), S(width, width, length))
        g = f",Glowing:1b,glow_color_override:{glow}" if glow is not None else ""
        self.add(t, f'summon block_display {rel(start)} {{Tags:["{NS}.e","{tag}"],block_state:{{Name:"minecraft:{block}"}},'
                    f"transformation:{mat_nbt(m)},brightness:{{sky:15,block:15}},teleport_duration:{dur}{g}}}")
        self.add(t + 2, f"tp @e[type=block_display,tag={tag},limit=1] {rel(end)}")
        if trail:
            for k in range(dur + 1):
                self.particle(t + 2 + k, trail, lerp3(start, end, k / dur), (0.05, 0.05, 0.05), 0.01, 2)
        self.add(t + 2 + dur, f"kill @e[type=block_display,tag={tag}]")
        self.log.append((t, 'shard', self.uid, start, tuple(end), dur, m, block, glow))
        return t + 2 + dur


# ---------------------------------------------------------------- вывод датапака
def dispatch_tree(ticks, emit, name="d/root"):
    """Бинарное дерево проверок: на каждом тике ~log2(N) сравнений вместо N."""
    lines = []
    if len(ticks) <= 6:
        for t in ticks:
            lines.append(f"execute if score #t {NS}.t matches {t} run function {NS}:f/{t}")
    else:
        mid = len(ticks) // 2
        left, right = ticks[:mid], ticks[mid:]
        ln, rn = f"{name}l", f"{name}r"
        lines.append(f"execute if score #t {NS}.t matches ..{left[-1]} run function {NS}:{ln}")
        lines.append(f"execute if score #t {NS}.t matches {right[0]}.. run function {NS}:{rn}")
        dispatch_tree(left, emit, ln)
        dispatch_tree(right, emit, rn)
    emit(name, "\n".join(lines) + "\n")
