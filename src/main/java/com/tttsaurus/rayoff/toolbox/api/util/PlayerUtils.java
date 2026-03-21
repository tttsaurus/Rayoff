package com.tttsaurus.rayoff.toolbox.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class PlayerUtils {
    private PlayerUtils() {
    }

    public static Set<EntityPlayerMP> tracking(Entity entity) {
        if (!(entity.world instanceof WorldServer worldServer)) {
            return Collections.emptySet();
        }

        Set<EntityPlayerMP> players = new HashSet<>();
        for (EntityPlayer player : worldServer.getEntityTracker().getTrackingPlayers(entity)) {
            if (player instanceof EntityPlayerMP playerMP) {
                players.add(playerMP);
            }
        }
        return players;
    }

    public static Set<EntityPlayerMP> around(WorldServer level, Vec3d pos, double radius) {
        Set<EntityPlayerMP> players = new HashSet<>();

        for (EntityPlayer player : level.playerEntities) {
            if (player instanceof EntityPlayerMP playerMP && playerMP.getDistanceSq(pos.x, pos.y, pos.z) <= radius * radius) {
                players.add(playerMP);
            }
        }

        return players;
    }

    public static Set<EntityPlayerMP> level(WorldServer level) {
        Set<EntityPlayerMP> players = new HashSet<>();
        for (EntityPlayer player : level.playerEntities) {
            if (player instanceof EntityPlayerMP playerMP) {
                players.add(playerMP);
            }
        }
        return players;
    }

    public static Set<EntityPlayerMP> all(MinecraftServer server) {
        return new HashSet<>(server.getPlayerList().getPlayers());
    }
}
