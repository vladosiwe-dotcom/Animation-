"""Синтез звуков маски (numpy) -> OGG Vorbis моно (ffmpeg).

python3 tools/make_sounds.py -> src/main/resources/assets/miragemask/sounds/mask/{put_on,disguise}.ogg
"""
import os
import subprocess
import tempfile
import wave

import numpy as np

SR = 44100
OUT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "miragemask", "sounds", "mask")
rng = np.random.default_rng(7)


def t(sec):
    return np.arange(int(SR * sec)) / SR


def smooth(x, n):
    return np.convolve(x, np.ones(n) / n, mode="same")


def band_noise(sec, lo_win, hi_win):
    n = rng.standard_normal(int(SR * sec))
    return smooth(n, lo_win) - smooth(n, hi_win)     # грубый полосовой фильтр


def place(buf, sig, at):
    i = int(at * SR)
    end = min(len(buf), i + len(sig))
    buf[i:end] += sig[:end - i]


def env(sec, attack, release):
    x = t(sec)
    a = np.clip(x / max(attack, 1e-4), 0, 1)
    r = np.clip((sec - x) / max(release, 1e-4), 0, 1)
    return a * r


def put_on():
    total = 0.95
    buf = np.zeros(int(SR * total))
    # шорох: рука подносит маску к лицу
    w = band_noise(0.6, 9, 140) * env(0.6, 0.35, 0.2) * 0.22   # мягкий шорох (средние частоты)
    place(buf, w, 0.0)
    # стеклянное мерцание поверх
    x = t(0.6)
    shimmer = sum(np.sin(2 * np.pi * f * x) for f in (2400, 3150, 4210)) / 3
    place(buf, shimmer * (0.5 + 0.5 * np.sin(2 * np.pi * 18 * x)) * env(0.6, 0.4, 0.15) * 0.06, 0.0)
    # щелчки: соты прилипают одна за другой
    for k in range(6):
        f = 3200 + k * 260 + rng.uniform(-120, 120)
        x = t(0.03)
        click = np.sin(2 * np.pi * f * x) * np.exp(-x * 160) * (1 - np.exp(-x * 1500))
        place(buf, click * 0.32, 0.56 + k * 0.024)
    # мягкий «чмок» — маска прилегла к лицу
    x = t(0.12)
    freq = 150 * np.exp(-x * 9) + 65
    thump = np.sin(2 * np.pi * np.cumsum(freq) / SR) * np.exp(-x * 30) * (1 - np.exp(-x * 800))
    place(buf, thump * 0.75, 0.72)
    place(buf, band_noise(0.05, 6, 40) * np.exp(-t(0.05) * 70) * (1 - np.exp(-t(0.05) * 1500)) * 0.2, 0.72)
    # лёгкий звон в конце
    x = t(0.25)
    chime = (np.sin(2 * np.pi * 1760 * x) + 0.6 * np.sin(2 * np.pi * 2640 * x)) * np.exp(-x * 14)
    place(buf, chime * 0.12, 0.74)
    return buf


def disguise():
    total = 1.25
    x = t(total)
    buf = np.zeros(len(x))
    e = np.sin(np.pi * np.clip(x / total, 0, 1)) ** 1.5
    # переливание вверх (несколько расстроенных голосов)
    for base, ratio, amp in ((420, 2.6, 0.22), (630, 2.4, 0.16), (845, 2.2, 0.12)):
        freq = base * ratio ** (x / total)
        buf += np.sin(2 * np.pi * np.cumsum(freq) / SR) * amp * e * (0.7 + 0.3 * np.sin(2 * np.pi * 6 * x))
    # мягкая низкая подложка
    buf += np.sin(2 * np.pi * 110 * x) * 0.18 * e
    # искры
    for _ in range(26):
        at = rng.uniform(0.15, 1.05)
        f = rng.uniform(3000, 6200)
        y = t(0.04)
        place(buf, np.sin(2 * np.pi * f * y) * np.exp(-y * 120) * (1 - np.exp(-y * 1500)) * rng.uniform(0.05, 0.12), at)
    return buf


def write_ogg(sig, name, peak_db):
    sig = sig / (np.max(np.abs(sig)) + 1e-9) * (10 ** (peak_db / 20))
    fade = min(len(sig), int(0.01 * SR))
    sig[-fade:] *= np.linspace(1, 0, fade)
    pcm = (np.clip(sig, -1, 1) * 32767).astype(np.int16)
    os.makedirs(OUT, exist_ok=True)
    with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp:
        with wave.open(tmp.name, "wb") as wv:
            wv.setnchannels(1); wv.setsampwidth(2); wv.setframerate(SR)
            wv.writeframes(pcm.tobytes())
        out = os.path.normpath(os.path.join(OUT, name + ".ogg"))
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", tmp.name, "-ac", "1", "-c:a", "libvorbis", "-q:a", "6", out], check=True)
    os.unlink(tmp.name)
    print("wrote", out)


if __name__ == "__main__":
    write_ogg(put_on(), "put_on", -4)
    write_ogg(disguise(), "disguise", -9)    # тише
