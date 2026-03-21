package com.tttsaurus.rayoff.toolbox.api.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public final class ServerNetworking {
    private ServerNetworking() {
    }

    public static void send(EntityPlayerMP player, ResourceLocation identifier, Consumer<PacketBuffer> consumer) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        consumer.accept(buf);
        player.connection.sendPacket(new SPacketCustomPayload(identifier.toString(), buf));
    }
}
