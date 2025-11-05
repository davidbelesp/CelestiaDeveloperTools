package com.davidbelesp.cdt.utils;

import net.minecraft.client.MinecraftClient;

import java.awt.*;

public class ChatUtils {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static void sendMessage(String message) {
        if (MC.player == null) return;

    }

}
