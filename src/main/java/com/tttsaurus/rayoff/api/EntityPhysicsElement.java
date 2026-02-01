package com.tttsaurus.rayoff.api;

import com.google.common.base.Preconditions;
import com.tttsaurus.rayoff.impl.bullet.collision.body.EntityRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.body.shape.MinecraftShape;
import net.minecraft.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Use this interface to create a physics entity.
 * @see PhysicsElement
 */
public interface EntityPhysicsElement extends PhysicsElement<Entity> {

    static boolean is(Entity entity) {
        return entity instanceof EntityPhysicsElement element && element.getRigidBody() != null;
    }

    @NonNull
    static EntityPhysicsElement get(Entity entity) {
        Preconditions.checkState(entity instanceof EntityPhysicsElement,
                "Argument \"entity\" is not an instance of EntityPhysicsElement.");

        return (EntityPhysicsElement) entity;
    }

    @Override
    @Nullable
    EntityRigidBody getRigidBody();

    @Override
    default MinecraftShape.@NonNull Convex createShape() {
        final var box = cast().getEntityBoundingBox();
        double xSize = box.maxX - box.minX;
        double ySize = box.maxY - box.minY;
        double zSize = box.maxZ - box.minZ;
        return MinecraftShape.convex(box.contract(xSize * 0.25, ySize * 0.25, zSize * 0.25));
    }

    default boolean skipVanillaEntityCollisions() {
        return false;
    }
}
