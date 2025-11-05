package com.davidbelesp.cdt.annotation;


import net.minecraft.client.MinecraftClient;

public interface TickFeature extends Feature {
    void onTick(MinecraftClient client);
}
