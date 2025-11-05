package com.davidbelesp.cdt.annotation;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface RenderHudFeature extends Feature {
    void onRenderHud(DrawContext ctx, RenderTickCounter tickDelta);
}