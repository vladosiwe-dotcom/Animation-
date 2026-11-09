package com.vladosiwe.miragemask.registry;

import com.vladosiwe.miragemask.MirageMask;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Свои звуки маски (синтезированы tools/make_sounds.py). */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MirageMask.MOD_ID);

    /** Надевание — слышат все рядом. */
    public static final DeferredHolder<SoundEvent, SoundEvent> PUT_ON = SOUNDS.register("mask.put_on",
            () -> SoundEvent.createVariableRangeEvent(MirageMask.id("mask.put_on")));
    /** Маскировка — тихо, только сам игрок. */
    public static final DeferredHolder<SoundEvent, SoundEvent> DISGUISE = SOUNDS.register("mask.disguise",
            () -> SoundEvent.createVariableRangeEvent(MirageMask.id("mask.disguise")));

    private ModSounds() {}
}
