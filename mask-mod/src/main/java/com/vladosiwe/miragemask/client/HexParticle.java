package com.vladosiwe.miragemask.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/** Светящийся шестиугольник голубой энергии (или белая искра); медленно крутится и гаснет. */
public class HexParticle extends TextureSheetParticle {
    private final float spin;

    protected HexParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
        this.xd = vx + (random.nextDouble() - 0.5) * 0.04;
        this.yd = vy + 0.01 + random.nextDouble() * 0.02;
        this.zd = vz + (random.nextDouble() - 0.5) * 0.04;
        this.lifetime = 18 + random.nextInt(16);
        this.quadSize = 0.06F + random.nextFloat() * 0.08F;
        this.gravity = 0;
        this.friction = 0.92F;
        this.hasPhysics = false;
        int kind = random.nextInt(10);
        if (kind < 5) setColor(0.12F, 0.78F, 1.0F);        // голубая энергия
        else if (kind < 8) setColor(0.66F, 0.95F, 1.0F);   // светлая кромка
        else setColor(1.0F, 1.0F, 1.0F);                   // искра
        this.roll = this.oRoll = random.nextFloat() * Mth.TWO_PI;
        this.spin = (random.nextFloat() - 0.5F) * 0.2F;
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += spin;
        this.alpha = 1.0F - (float) age / lifetime;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double vx, double vy, double vz) {
            HexParticle p = new HexParticle(level, x, y, z, vx, vy, vz);
            p.pickSprite(sprites);
            return p;
        }
    }
}
