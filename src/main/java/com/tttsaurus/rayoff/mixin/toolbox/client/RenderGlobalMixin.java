package com.tttsaurus.rayoff.mixin.toolbox.client;

import com.tttsaurus.rayoff.toolbox.api.event.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {

    @Shadow
    @Final
    private Minecraft mc;

    /**
     * @see ClientEvents.Render#BEFORE_DEBUG
     */
    @Inject(
            method = "renderEntities",
            at = @At("TAIL"))
    private void onRenderEntities(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        WorldClient world = mc.world;
        if (world == null) {
            return;
        }

        ClientEvents.Render.BEFORE_DEBUG.invoke(partialTicks, world);
    }
}
