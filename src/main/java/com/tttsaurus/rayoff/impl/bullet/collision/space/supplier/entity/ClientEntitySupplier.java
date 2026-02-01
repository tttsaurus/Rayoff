package com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

public class ClientEntitySupplier implements EntitySupplier {
    @Override
    public GameType getGameType(EntityPlayer player) {
        var client = Minecraft.getMinecraft();
        var id = player.getUUID();

        // Is client player
        if (client.player != null && client.player.getUUID().equals(id) && client.gameMode != null) {
            return client.gameMode.getPlayerMode();
        }

        // Is remote player
        var connection = Minecraft.getMinecraft().getConnection();
        if (connection != null && connection.getOnlinePlayerIds().contains(id)) {
            var playerInfo = connection.getPlayerInfo(id);
            return playerInfo == null ? GameType.SURVIVAL : playerInfo.getGameMode();
        }

        return GameType.SURVIVAL;
    }
}
