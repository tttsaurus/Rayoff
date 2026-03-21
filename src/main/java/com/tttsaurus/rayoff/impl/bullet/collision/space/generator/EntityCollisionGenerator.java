package com.tttsaurus.rayoff.impl.bullet.collision.space.generator;

import com.jme3.math.Vector3f;
import com.tttsaurus.rayoff.impl.bullet.collision.body.EntityRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.toolbox.api.compat.Convert;

/**
 * Mods should implement it on their own, with will allow for better performance
 */
public class EntityCollisionGenerator {
    public static void step(MinecraftSpace space) {
        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            if (rigidBody.getElement().skipVanillaEntityCollisions()) {
                continue;
            }

            final var box = rigidBody.getCurrentBoundingBox();
            final var location = rigidBody.getPhysicsLocation(new Vector3f()).subtract(new Vector3f(0, -box.getYExtent(), 0));
            final var mass = rigidBody.getMass();

            final var vanillaBox = rigidBody.getCurrentMinecraftBoundingBox();

            for (var entity : space.getWorkerThread().getEntitySupplier().getInsideOf(rigidBody, vanillaBox)) {
                final var entityBox = entity.getEntityBoundingBox();
                final var entityPos = Convert.toBullet(entity.getPositionVector().add(0.0, entityBox.maxY - entityBox.minY, 0.0));
                final var normal = location.subtract(entityPos).multLocal(new Vector3f(1, 0, 1)).normalize();

                final var intersection = entityBox.intersect(vanillaBox);
                final double intersectionSize = (intersection.maxX - intersection.minX) * (intersection.maxY - intersection.minY) * (intersection.maxZ - intersection.minZ);
                final double vanillaSize = (vanillaBox.maxX - vanillaBox.minX) * (vanillaBox.maxY - vanillaBox.minY) * (vanillaBox.maxZ - vanillaBox.minZ);
                final var force = normal.clone()
                        .multLocal(vanillaSize == 0.0 ? 0.0f : (float) (intersectionSize / vanillaSize))
                        .multLocal(mass)
                        .multLocal(new Vector3f(1, 0, 1));
                rigidBody.applyCentralImpulse(force);
            }
        }
    }
}
