package com.davidbelesp.cdt.annotation;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public interface JoinFeature extends Feature {
    void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client);
}
