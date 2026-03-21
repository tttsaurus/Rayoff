package com.tttsaurus.rayoff.mixin.toolbox.client;

import com.tttsaurus.rayoff.toolbox.api.event.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldClient.class)
public class WorldClientMixin {

    @Shadow
    @Final
    private Minecraft mc;

    /**
     * @see ClientEvents.Lifecycle#LOAD_LEVEL
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        ClientEvents.Lifecycle.LOAD_LEVEL.invoke(mc, (WorldClient)(Object)this);
    }

    /**
     * @see ClientEvents.Tick#START_LEVEL_TICK
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickStart(CallbackInfo ci) {
        ClientEvents.Tick.START_LEVEL_TICK.invoke((WorldClient)(Object)this);
    }

    /**
     * @see ClientEvents.Tick#END_LEVEL_TICK
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(CallbackInfo ci) {
        ClientEvents.Tick.END_LEVEL_TICK.invoke((WorldClient)(Object)this);
    }

    /**
     * @see ClientEvents.Entity#LOAD
     * @see ClientEvents.Player#ADD
     */
    @Inject(method = "addEntityToWorld", at = @At("TAIL"))
    private void onAddEntity(int entityId, Entity entity, CallbackInfo ci) {
        ClientEvents.Entity.LOAD.invoke(entity);
        if (entity instanceof AbstractClientPlayer player) {
            boolean local = player instanceof EntityPlayerSP;
            ClientEvents.Player.ADD.invoke(player, local);
        }
    }

    /**
     * @see ClientEvents.Entity#UNLOAD
     */
    @Inject(method = "removeEntityFromWorld", at = @At("HEAD"))
    private void onRemoveEntity(int entityID, CallbackInfoReturnable<Entity> cir) {
        Entity entity = ((WorldClient)(Object)this).getEntityByID(entityID);
        if (entity != null) {
            ClientEvents.Entity.UNLOAD.invoke(entity);
        }
    }

    /**
     * @see ClientEvents.Entity#UNLOAD
     */
    @Inject(method = "removeAllEntities", at = @At("HEAD"))
    private void onRemoveAllEntities(CallbackInfo ci) {
        WorldClient world = (WorldClient)(Object)this;
        for (Entity entity : world.loadedEntityList) {
            ClientEvents.Entity.UNLOAD.invoke(entity);
        }
    }
}
