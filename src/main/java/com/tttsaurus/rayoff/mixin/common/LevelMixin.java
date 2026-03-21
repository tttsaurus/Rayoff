package com.tttsaurus.rayoff.mixin.common;

import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.impl.bullet.collision.space.storage.SpaceStorage;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * This is how each {@link MinecraftSpace} is stored within its associated {@link World}.
 * @see SpaceStorage
 */
@Mixin(World.class)
public class LevelMixin implements SpaceStorage {
    @Unique private MinecraftSpace space;

    @Override
    public void setSpace(MinecraftSpace space) {
        this.space = space;
    }

    @Override
    public MinecraftSpace getSpace() {
        return this.space;
    }
}
