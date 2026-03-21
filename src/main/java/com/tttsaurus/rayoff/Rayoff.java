package com.tttsaurus.rayoff;

import com.tttsaurus.rayoff.Reference;
import com.tttsaurus.rayoff.impl.RayoffCore;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class Rayoff {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Path gameDir = event.getModConfigurationDirectory().toPath().getParent();
        RayoffCore.initialize(gameDir);

        if (event.getSide() == Side.CLIENT) {
            RayoffCore.initializeClient();
        }
    }
}
