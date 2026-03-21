package com.tttsaurus.rayoff.mixin.toolbox.client;

import com.tttsaurus.rayoff.toolbox.api.event.ClientEvents;
import com.tttsaurus.rayoff.toolbox.api.network.PacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {

    @Shadow
    private Minecraft client;

    @Shadow
    private WorldClient world;

    /**
     * @see ClientEvents.Lifecycle#PRE_LOGIN
     */
    @Inject(
            method = "handleJoinGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"))
    private void onJoinGamePre(SPacketJoinGame packet, CallbackInfo ci) {
        ClientEvents.Lifecycle.PRE_LOGIN.invoke(client);
    }

    /**
     * @see ClientEvents.Lifecycle#POST_LOGIN
     */
    @Inject(method = "handleJoinGame", at = @At("RETURN"))
    private void onJoinGamePost(SPacketJoinGame packet, CallbackInfo ci) {
        ClientEvents.Lifecycle.POST_LOGIN.invoke(client, world, client.player);
    }

    /**
     * Custom payload interception
     */
    @Inject(
            method = "handleCustomPayload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"),
            cancellable = true)
    private void onCustomPayload(SPacketCustomPayload packet, CallbackInfo ci) {
        String channel = packet.getChannelName();
        var split = channel.split(":");
        if (split.length != 2) {
            return;
        }

        ResourceLocation identifier = new ResourceLocation(channel);

        var consumer = PacketRegistry.getClientbound(identifier);
        if (consumer != null) {
            PacketBuffer buf = packet.getBufferData();
            consumer.accept(new PacketRegistry.ClientboundContext(buf));
            ci.cancel();
        }
    }
}
