package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

/** Три «материала» маски: энергия (свечение), стекло (полупрозрачное), тёмный пластик креплений. */
public final class MaskRenderTypes extends RenderType {
    /** Аддитивное свечение без текстуры и освещения. */
    public static final RenderType ENERGY = create("miragemask_energy", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 8192, false, true,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setOutputState(WEATHER_TARGET)
                    .createCompositeState(false));

    /** Полупрозрачное стекло (обычное смешивание), сквозь него видно лицо. */
    public static final RenderType GLASS = create("miragemask_glass", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 4096, false, true,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setOutputState(TRANSLUCENT_TARGET)
                    .createCompositeState(false));

    /** Непрозрачные тёмные крепления. */
    public static final RenderType SOLID = create("miragemask_solid", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 1024, false, false,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));

    private MaskRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int size, boolean crumbling,
                            boolean sort, Runnable setup, Runnable clear) {
        super(name, format, mode, size, crumbling, sort, setup, clear);
    }
}
