package com.davidbelesp.cdt;

import com.davidbelesp.cdt.annotation.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class CelestiaDeveloperToolsClient implements ClientModInitializer {

	public static final MinecraftClient MC = MinecraftClient.getInstance();
	private final List<Feature> features = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		registerFeatures();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

	}

	private void registerFeatures() {
		discoverAndRegister("com.davidbelesp.cdt"); // limit to your code’s root package

		if (features.stream().anyMatch(f -> f instanceof TickFeature)) {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				for (Feature f : features) if (f instanceof TickFeature t) t.onTick(client);
			});
		}

		if (features.stream().anyMatch(f -> f instanceof RenderHudFeature)) {
			HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
				for (Feature f : features) if (f instanceof RenderHudFeature r) r.onRenderHud(ctx, tickDelta);
			});
		}

		if (features.stream().anyMatch(f -> f instanceof JoinFeature)) {
			ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
				for (Feature f : features) if (f instanceof JoinFeature j) j.onJoin(handler, sender, client);
			});
		}

		if (features.stream().anyMatch(f -> f instanceof Command)) {
			ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
				for (Feature f : features) {
					if (f instanceof Command c) {
						c.register(dispatcher);
					}
				}
			});
		}
	}

	private void discoverAndRegister(String basePackage) {
		Reflections reflections = new Reflections(basePackage);

		for (Class<?> clazz : reflections.getTypesAnnotatedWith(Register.class)) {
			try {
				Object obj = clazz.getDeclaredConstructor().newInstance();
				if (!(obj instanceof Feature feature)) continue;

				Register ann = clazz.getAnnotation(Register.class);
				boolean ok = switch (ann.value()) {
					case TICK       -> feature instanceof TickFeature;
					case RENDER_HUD -> feature instanceof RenderHudFeature;
					case JOIN       -> feature instanceof JoinFeature;
					case COMMAND   -> feature instanceof Command;
				};
				if (!ok) {
					throw new IllegalStateException(clazz.getSimpleName()
							+ " is @" + ann.value() + " but doesn’t implement the matching interface");
				}
				features.add(feature);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}