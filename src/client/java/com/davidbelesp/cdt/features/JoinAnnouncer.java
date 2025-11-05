package com.davidbelesp.cdt.features;

import com.davidbelesp.cdt.annotation.EventType;
import com.davidbelesp.cdt.annotation.JoinFeature;
import com.davidbelesp.cdt.annotation.Register;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;


@Register(EventType.JOIN)
public class JoinAnnouncer implements JoinFeature {

    @Override
    public void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal("[MyMod] Joined world!"), false); // local chat
            // To actually chat to server: client.player.networkHandler.sendChatMessage("hello!");
        }
    }
}