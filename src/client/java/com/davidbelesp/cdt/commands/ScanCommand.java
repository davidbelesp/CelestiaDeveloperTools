package com.davidbelesp.cdt.commands;

import com.davidbelesp.cdt.annotation.Command;
import com.davidbelesp.cdt.annotation.EventType;
import com.davidbelesp.cdt.annotation.Register;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;

@Register(EventType.COMMAND)
public class ScanCommand implements Command {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("skins")
                        .executes(ctx -> execute(64.0f))
                        .then(ClientCommandManager.argument("radius", FloatArgumentType.floatArg(1f, 256f))
                                .executes(ctx -> execute(FloatArgumentType.getFloat(ctx, "radius"))))
        );
    }

    private int execute(float radius) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return 0;

        double r2 = radius * radius;

        List<AbstractClientPlayerEntity> players = mc.world.getPlayers().stream()
                .filter(p -> p != mc.player)
                .map(p -> p)
                .filter(p -> p.squaredDistanceTo(mc.player) <= r2)
                .toList();

        mc.player.sendMessage(Text.literal(String.format("Scanning %.0fm… found %d player(s)", radius, players.size()))
                .formatted(Formatting.DARK_GRAY), false);

        int count = 0;
        for (AbstractClientPlayerEntity p : players) {
            PlayerListEntry entry = mc.getNetworkHandler() != null ? mc.getNetworkHandler().getPlayerListEntry(p.getUuid()) : null;
            if (entry == null) continue;

            GameProfile gp = entry.getProfile();
            Collection<Property> texProps = gp.getProperties().get("textures");
            if (texProps.isEmpty()) continue;

            Property tex = texProps.iterator().next();
            String texture = tex.value();
            String signature = tex.signature();

            int dist = (int) Math.sqrt(p.squaredDistanceTo(mc.player));

            Text nameBtn = Text.literal("[" + p.getName().getString() + "]")
                    .styled(s -> s.withColor(Formatting.YELLOW)
                            .withClickEvent(new ClickEvent.CopyToClipboard(p.getName().getString()))
                            .withHoverEvent(new HoverEvent.ShowText(Text.literal(p.getName().getString()))));
            
            Text textureBtn = Text.literal("[Texture]").styled(s -> s
                    .withColor(Formatting.AQUA)
                    .withClickEvent(new ClickEvent.CopyToClipboard(
                            texture)
                    )
                    .withHoverEvent(new HoverEvent.ShowText(
                            Text.literal("Click to copy Texture\n" + preview(texture)))
                    )
            );

            Text line = nameBtn.copy().append(Text.literal(" ~" + dist + "m ").formatted(Formatting.LIGHT_PURPLE)).append(textureBtn);

            if (signature != null && !signature.isEmpty()) {
                Text sigBtn = Text.literal(" [Signature]").styled(s -> s
                        .withColor(Formatting.GREEN)
                        .withClickEvent(new ClickEvent.CopyToClipboard(signature))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Text.literal("Click to copy Signature\n" + preview(signature)))));
                line = line.copy().append(sigBtn);
            }

            mc.player.sendMessage(line, false);
            count++;
        }

        if (count == 0) {
            mc.player.sendMessage(Text.literal(
                            "No players with textures found within " + (int) radius + " blocks.")
                    .formatted(Formatting.GRAY), false);
        } else {
            mc.player.sendMessage(Text.literal(
                            "— scanned " + count + " player(s). Click to copy.")
                    .formatted(Formatting.DARK_GRAY), false);
        }
        return 1;
    }

    private String preview(String s) {
        if (s == null) return "(null)";
        int n = Math.min(64, s.length());
        return s.substring(0, n) + (s.length() > n ? "…" : "");
    }
}
