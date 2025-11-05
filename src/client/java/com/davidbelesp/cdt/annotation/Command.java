package com.davidbelesp.cdt.annotation;


import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface Command extends Feature {
    void register(CommandDispatcher<FabricClientCommandSource> dispatcher);
}
