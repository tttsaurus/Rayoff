package com.tttsaurus.rayoff.impl.bullet.collision.space.storage;

import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;

/**
 * Used for storing a {@link MinecraftSpace} within any
 * {@link net.minecraft.world.World} object.
 */
public interface SpaceStorage {
    void setSpace(MinecraftSpace space);
    MinecraftSpace getSpace();
}
