package com.tttsaurus.rayoff.toolbox.api.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class PacketRegistry {
    private PacketRegistry() {
    }

    private static final Map<ResourceLocation, Consumer<ServerboundContext>> SERVERBOUND = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Consumer<ClientboundContext>> CLIENTBOUND = new ConcurrentHashMap<>();

    public static void registerServerbound(ResourceLocation identifier, Consumer<ServerboundContext> packetHandler) {
        SERVERBOUND.put(identifier, packetHandler);
    }

    public static void registerClientbound(ResourceLocation identifier, Consumer<ClientboundContext> packetHandler) {
        CLIENTBOUND.put(identifier, packetHandler);
    }

    public static Consumer<ServerboundContext> getServerbound(ResourceLocation identifier) {
        return SERVERBOUND.get(identifier);
    }

    public static Consumer<ClientboundContext> getClientbound(ResourceLocation identifier) {
        return CLIENTBOUND.get(identifier);
    }

    public record ServerboundContext(PacketBuffer byteBuf, EntityPlayerMP player) {
    }

    public record ClientboundContext(PacketBuffer byteBuf) {
    }
}
