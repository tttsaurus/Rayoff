package com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.level;

import com.tttsaurus.rayoff.impl.bullet.thread.PhysicsThread;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * This interface is what allows the {@link PhysicsThread} to retrieve
 * a list of {@link World} objects without knowing where they come from.
 * In this way, it can be used for client worlds or server worlds and,
 * in the case of immersive portals, it can be used to provide multiple
 * client Levels.
 * @see PhysicsThread
 * @see ClientLevelSupplier
 * @see ServerLevelSupplier
 */
public interface LevelSupplier {
     /**
     * Provides the complete list of {@link World}s. If
      * there aren't any, it will return an empty list.
      * @return the list of {@link World}s.
      */
     List<World> getAll();

     /**
      * Provides a specific {@link World} based on the given dimension id.
      * @param dimensionId the dimension id to identify the world with
      * @return a {@link World}
      */
     World get(int dimensionId);

     /**
      * Provides a specific {@link World} based on the given dimension id.
      * @param dimensionId the dimension id to identify the world with
      * @return an optional {@link World}
      */
     Optional<World> getOptional(int dimensionId);
}
