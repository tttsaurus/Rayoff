package com.tttsaurus.rayoff.impl.bullet.collision.space.generator;

import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.impl.bullet.collision.body.ElementRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.body.TerrainRigidBody;
import com.tttsaurus.rayoff.toolbox.api.math.AABBUtils;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 * @see MinecraftSpace
 */
public class TerrainGenerator {
    public static void step(MinecraftSpace space) {
        final var chunkCache = space.getChunkCache();
        final var keep = new HashSet<TerrainRigidBody>();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = AABBUtils.inflate(rigidBody.getCurrentMinecraftBoundingBox(), 0.5f);

            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
                chunkCache.getBlockData(blockPos).ifPresent(blockData -> {
                    space.getTerrainObjectAt(blockPos).ifPresentOrElse(terrain -> {
                        if (blockData.blockState() != terrain.getBlockState()) {
                            space.removeCollisionObject(terrain);

                            final var terrain2 = TerrainRigidBody.from(blockData);
                            space.addCollisionObject(terrain2);
                            keep.add(terrain2);
                        } else {
                            keep.add(terrain);
                        }
                    }, () -> {
                        final var terrain = TerrainRigidBody.from(blockData);
                        space.addCollisionObject(terrain);
                        keep.add(terrain);
                    });
                });
            });
        }

        space.getTerrainMap().forEach((blockPos, terrain) -> {
            if (!keep.contains(terrain)) {
                space.removeTerrainObjectAt(blockPos);
            }
        });
    }
}