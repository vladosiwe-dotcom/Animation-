package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

/** «Энергия»: аддитивное свечение без текстуры, без освещения и без отсечения граней. */
public final class MaskRenderTypes extends RenderType {
    public static final RenderType ENERGY = create("miragemask_energy", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 4096, false, true,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setOutputState(WEATHER_TARGET)
                    .createCompositeState(false));

    private MaskRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int size, boolean crumbling,
                            boolean sort, Runnable setup, Runnable clear) {
        super(name, format, mode, size, crumbling, sort, setup, clear);
    }
}
