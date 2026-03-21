package com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

public class ClientEntitySupplier implements EntitySupplier {
    @Override
    public GameType getGameType(EntityPlayer player) {
        var client = Minecraft.getMinecraft();
        var id = player.getUniqueID();

        if (client.player != null && client.player.getUniqueID().equals(id) && client.playerController != null) {
            return client.playerController.getCurrentGameType();
        }

        var connection = Minecraft.getMinecraft().getConnection();
        if (connection != null) {
            var playerInfo = connection.getPlayerInfo(id);
            return playerInfo == null ? GameType.SURVIVAL : playerInfo.getGameType();
        }

        return GameType.SURVIVAL;
    }
}
