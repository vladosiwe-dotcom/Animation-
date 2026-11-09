package com.vladosiwe.miragemask.registry;

import com.vladosiwe.miragemask.MirageMask;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, MirageMask.MOD_ID);

    /** Светящийся шестиугольник — вспышки при надевании, смене облика и срыве маски. */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HEX =
            PARTICLES.register("hex", () -> new SimpleParticleType(true));

    private ModParticles() {}
}
