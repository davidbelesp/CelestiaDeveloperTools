package com.davidbelesp.cdt.features;

import com.davidbelesp.cdt.annotation.EventType;
import com.davidbelesp.cdt.annotation.Register;
import com.davidbelesp.cdt.annotation.RenderHudFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

@Register(EventType.RENDER_HUD)
public class HudBadge implements RenderHudFeature {
    @Override
    public void onRenderHud(DrawContext ctx, RenderTickCounter tickDelta) {
        var font = MinecraftClient.getInstance().textRenderer;
        ctx.drawText(font, "MyMod ✔", 6, 6, 0xFFFFFF, true);
    }
}