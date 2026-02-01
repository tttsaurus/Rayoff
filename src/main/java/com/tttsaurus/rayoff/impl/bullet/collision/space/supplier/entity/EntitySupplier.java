package com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.entity;

import com.tttsaurus.rayoff.api.EntityPhysicsElement;
import com.tttsaurus.rayoff.impl.bullet.collision.body.ElementRigidBody;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameType;

import java.util.List;

public interface EntitySupplier {

    default List<Entity> getInsideOf(ElementRigidBody rigidBody, AxisAlignedBB box) {
        if (!rigidBody.isInWorld()) {
            return List.of();
        }

        return rigidBody.getSpace().getWorld().getEntitiesOfClass(Entity.class, box,
                entity ->
                        // Entity can be a Boat, Minecart, or any LivingEntity so long as it is not a player in spectator mode.
                        (
                            entity instanceof EntityBoat ||
                            entity instanceof EntityMinecart ||
                            (
                                entity instanceof EntityLiving &&
                                !(entity instanceof EntityPlayer player && this.getGameType(player) == GameType.SPECTATOR)
                            )
                        )
                        && !EntityPhysicsElement.is(entity));
    }

    GameType getGameType(EntityPlayer player);
}
