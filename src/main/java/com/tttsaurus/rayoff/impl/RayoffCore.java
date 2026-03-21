package com.tttsaurus.rayoff.impl;

import com.tttsaurus.rayoff.Reference;
import com.tttsaurus.rayoff.impl.bullet.natives.NativeLoader;
import com.tttsaurus.rayoff.impl.bullet.thread.PhysicsThread;
import com.tttsaurus.rayoff.impl.event.ClientEventHandler;
import com.tttsaurus.rayoff.impl.event.ServerEventHandler;
import com.tttsaurus.rayoff.impl.event.network.EntityNetworking;
import com.tttsaurus.rayoff.toolbox.api.event.ClientEvents;
import com.tttsaurus.rayoff.toolbox.api.event.ServerEvents;
import com.tttsaurus.rayoff.toolbox.api.network.PacketRegistry;
import com.tttsaurus.rayoff.toolbox.api.network.ServerNetworking;
import com.tttsaurus.rayoff.toolbox.impl.Transporter;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class RayoffCore {

	public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

	private static boolean serverHasRayoff = false;

	public static void initialize(Path gameDir) {
		// prevent annoying libbulletjme spam
		java.util.logging.LogManager.getLogManager().reset();

		NativeLoader.load(gameDir);
		LOGGER.info("LibBulletJME loaded.");

		Transporter.initialize();
		LOGGER.info("Transporter module initialized.");

		EntityNetworking.registerServer();
		LOGGER.info("EntityNetworking server packets registered.");

		ServerEventHandler.register();
		LOGGER.info("Server events registered.");

		// Rayoff Server Detection
		ServerEvents.Lifecycle.JOIN.register(player -> {
			ServerNetworking.send(player, new ResourceLocation(Reference.MOD_ID, "has_rayoff"), buf -> {});
		});
	}

	public static void initializeClient() {
		EntityNetworking.registerClient();
		LOGGER.info("EntityNetworking client packets registered.");

		ClientEventHandler.register();
		LOGGER.info("Client events registered.");

		// Rayoff Server Detection
		PacketRegistry.registerClientbound(new ResourceLocation(Reference.MOD_ID, "has_rayoff"), ctx -> serverHasRayoff = true);
		ClientEvents.Lifecycle.DISCONNECT.register((client, level) -> serverHasRayoff = false);
	}

	public static PhysicsThread getThread(boolean isClient) {
		return isClient ? ClientEventHandler.getThread() : ServerEventHandler.getThread();
	}

	public static boolean serverHasRayoff() {
		return serverHasRayoff;
	}
}
