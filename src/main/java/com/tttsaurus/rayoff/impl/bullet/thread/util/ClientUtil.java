package com.tttsaurus.rayoff.impl.bullet.thread.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ClientUtil {
    public static boolean isClient() {
        return FMLCommonHandler.instance().getSide() == Side.CLIENT;
    }

    public static boolean isPaused() {
        if (isClient()) {
            return Minecraft.getMinecraft().isGamePaused();
        }
        return false;
    }

    public static boolean isConnectedToServer() {
        if (isClient()) {
            return Minecraft.getMinecraft().getConnection() != null;
        }
        return false;
    }
}
