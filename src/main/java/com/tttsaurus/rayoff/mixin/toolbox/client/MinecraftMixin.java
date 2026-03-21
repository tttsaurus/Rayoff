package com.tttsaurus.rayoff.mixin.toolbox.client;

import com.tttsaurus.rayoff.toolbox.api.event.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    /**
     * @see ClientEvents.Lifecycle#DISCONNECT
     */
    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onLoadWorld(WorldClient world, String message, CallbackInfo ci) {
        if (world == null) {
            Minecraft minecraft = (Minecraft)(Object)this;
            ClientEvents.Lifecycle.DISCONNECT.invoke(minecraft, minecraft.world);
        }
    }

    /**
     * @see ClientEvents.Tick#START_CLIENT_TICK
     */
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onStartTick(CallbackInfo ci) {
        ClientEvents.Tick.START_CLIENT_TICK.invoke((Minecraft)(Object)this);
    }

    /**
     * @see ClientEvents.Tick#END_CLIENT_TICK
     */
    @Inject(method = "runTick", at = @At("RETURN"))
    private void onEndTick(CallbackInfo ci) {
        ClientEvents.Tick.END_CLIENT_TICK.invoke((Minecraft)(Object)this);
    }
}
