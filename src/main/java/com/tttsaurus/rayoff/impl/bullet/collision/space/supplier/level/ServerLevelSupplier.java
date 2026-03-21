package com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This {@link LevelSupplier} provides a list of all
 * {@link WorldServer} objects running on the {@link MinecraftServer}.
 */
public record ServerLevelSupplier(MinecraftServer server) implements LevelSupplier {
    @Override
    public List<World> getAll() {
        return new ArrayList<>(Arrays.asList(server.worlds));
    }

    @Override
    public World get(int dimensionId) {
        return server.getWorld(dimensionId);
    }

    @Override
    public Optional<World> getOptional(int dimensionId) {
        return Optional.ofNullable(get(dimensionId));
    }
}
