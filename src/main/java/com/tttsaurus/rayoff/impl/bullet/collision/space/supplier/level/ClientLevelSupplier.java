package com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is a {@link LevelSupplier} which returns a single
 * {@link WorldClient} object in a {@link List} object.
 */
public record ClientLevelSupplier(Minecraft minecraft) implements LevelSupplier {
    @Override
    public List<World> getAll() {
        final var out = new ArrayList<World>();

        if (minecraft.world != null) {
            out.add(minecraft.world);
        }

        return out;
    }

    @Override
    public World get(int dimensionId) {
        if (minecraft.world != null && minecraft.world.provider.getDimension() == dimensionId) {
            return minecraft.world;
        }

        return null;
    }

    @Override
    public Optional<World> getOptional(int dimensionId) {
        return Optional.ofNullable(get(dimensionId));
    }
}
