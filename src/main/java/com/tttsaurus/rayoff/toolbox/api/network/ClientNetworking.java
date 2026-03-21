package com.tttsaurus.rayoff.toolbox.api.network;

import com.google.common.base.Preconditions;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public final class ClientNetworking {
    private ClientNetworking() {
    }

    public static void send(ResourceLocation identifier, Consumer<PacketBuffer> consumer) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        consumer.accept(buf);

        var connection = Minecraft.getMinecraft().getConnection();
        Preconditions.checkNotNull(connection);

        connection.sendPacket(new CPacketCustomPayload(identifier.toString(), buf));
    }
}
