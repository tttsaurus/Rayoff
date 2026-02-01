package com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.entity;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;

public class ServerEntitySupplier implements EntitySupplier {
    @Override
    public GameType getGameType(EntityPlayer player) {
        if (player instanceof EntityPlayerMP serverPlayer) {
            return serverPlayer.getServerWorld().getMinecraftServer().getGameType();
        }
        return GameType.SURVIVAL;
    }
}
