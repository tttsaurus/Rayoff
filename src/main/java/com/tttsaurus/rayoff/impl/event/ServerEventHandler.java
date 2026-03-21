package com.tttsaurus.rayoff.impl.event;

import com.google.common.base.Preconditions;
import com.jme3.math.Vector3f;
import com.tttsaurus.rayoff.api.EntityPhysicsElement;
import com.tttsaurus.rayoff.api.event.collision.PhysicsSpaceEvents;
import com.tttsaurus.rayoff.impl.bullet.collision.body.ElementRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.space.generator.PressureGenerator;
import com.tttsaurus.rayoff.impl.bullet.collision.space.generator.TerrainGenerator;
import com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.entity.ServerEntitySupplier;
import com.tttsaurus.rayoff.impl.event.network.EntityNetworking;
import com.tttsaurus.rayoff.impl.bullet.collision.body.EntityRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import com.tttsaurus.rayoff.impl.bullet.collision.space.storage.SpaceStorage;
import com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import com.tttsaurus.rayoff.impl.bullet.thread.PhysicsThread;
import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.impl.bullet.thread.util.ClientUtils;
import com.tttsaurus.rayoff.toolbox.api.event.ServerEvents;
import com.tttsaurus.rayoff.toolbox.api.math.QuaternionHelper;
import com.tttsaurus.rayoff.toolbox.api.math.VectorHelper;
import com.tttsaurus.rayoff.toolbox.api.network.PacketRegistry;
import com.tttsaurus.rayoff.toolbox.api.util.PlayerUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class ServerEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Rayoff Events
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);
        PhysicsSpaceEvents.STEP.register(TerrainGenerator::step);
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onElementAddedToSpace);

        // Server Events
        ServerEvents.Lifecycle.LOAD_SERVER.register(ServerEventHandler::onServerStart);
        ServerEvents.Lifecycle.UNLOAD_SERVER.register(ServerEventHandler::onServerStop);
        ServerEvents.Tick.END_SERVER_TICK.register(ServerEventHandler::onServerTick);

        // Level Events
        ServerEvents.Lifecycle.LOAD_LEVEL.register(ServerEventHandler::onLevelLoad);
        ServerEvents.Tick.START_LEVEL_TICK.register(ServerEventHandler::onStartLevelTick);
        ServerEvents.Tick.START_LEVEL_TICK.register(ServerEventHandler::onEntityStartLevelTick);
        ServerEvents.Block.BLOCK_UPDATE.register(ServerEventHandler::onBlockUpdate);

        // Entity Events
        ServerEvents.Entity.LOAD.register(ServerEventHandler::onEntityLoad);
        ServerEvents.Entity.START_TRACKING.register(ServerEventHandler::onStartTrackingEntity);
        ServerEvents.Entity.STOP_TRACKING.register(ServerEventHandler::onStopTrackingEntity);
    }

    public static void onBlockUpdate(World level, IBlockState blockState, BlockPos blockPos) {
        MinecraftSpace.getOptional(level).ifPresent(space -> space.doBlockUpdate(blockPos));
    }

    public static void onServerStart(MinecraftServer server) {
        thread = new PhysicsThread(server, Thread.currentThread(), new ServerLevelSupplier(server), new ServerEntitySupplier(), "Server Physics Thread");
    }

    public static void onServerStop(MinecraftServer server) {
        if (thread != null) {
            thread.destroy();
            thread = null;
        }
    }

    public static void onServerTick(MinecraftServer server) {
        if (thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onStartLevelTick(World level) {
        if (!ClientUtils.isPaused()) {
            MinecraftSpace.get(level).step();
        }
    }

    public static void onLevelLoad(MinecraftServer server, WorldServer world) {
        final var space = new MinecraftSpace(thread, world);
        ((SpaceStorage) world).setSpace(space);
        PhysicsSpaceEvents.INIT.invoke(space);
    }

    public static void onElementAddedToSpace(MinecraftSpace space, ElementRigidBody rigidBody) {
        if (rigidBody instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().cast().getPositionVector();
            entityBody.setPhysicsLocation(Convert.toBullet(pos));
        }
    }

    public static void onEntityLoad(Entity entity) {
        if (EntityPhysicsElement.is(entity) && !PlayerUtils.tracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.world);
            space.getWorkerThread().execute(() -> space.addCollisionObject(
                    Preconditions.checkNotNull(EntityPhysicsElement.get(entity).getRigidBody())));
        }
    }

    public static void onStartTrackingEntity(Entity entity, EntityPlayerMP player) {
        if (EntityPhysicsElement.is(entity)) {
            var space = MinecraftSpace.get(entity.world);
            space.getWorkerThread().execute(() -> space.addCollisionObject(
                    Preconditions.checkNotNull(EntityPhysicsElement.get(entity).getRigidBody())));
        }
    }

    public static void onStopTrackingEntity(Entity entity, EntityPlayerMP player) {
        if (EntityPhysicsElement.is(entity) && PlayerUtils.tracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.world);
            space.getWorkerThread().execute(() -> space.removeCollisionObject(
                    Preconditions.checkNotNull(EntityPhysicsElement.get(entity).getRigidBody())));
        }
    }

    public static void onEntityStartLevelTick(World level) {
        var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.step(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            if (rigidBody.isActive()) {
                // movement
                if (rigidBody.isPositionDirty()) {
                    EntityNetworking.sendMovement(rigidBody);
                }

                // properties
                if (rigidBody.arePropertiesDirty()) {
                    EntityNetworking.sendProperties(rigidBody);
                }
            }

            // set entity position
            var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            rigidBody.getElement().cast().setPosition(location.x, location.y, location.z);
        }
    }

    public static void onMovementPacketReceived(PacketRegistry.ServerboundContext context) {
        var buf = context.byteBuf();
        var entityId = buf.readInt();
        var rotation = Convert.toBullet(QuaternionHelper.fromBuffer(buf));
        var location = Convert.toBullet(VectorHelper.fromBuffer(buf));
        var linearVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        var angularVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        var player = context.player();
        var level = player.world;
        var entity = level.getEntityByID(entityId);

        if (EntityPhysicsElement.is(entity)) {
            var rigidBody = EntityPhysicsElement.get(entity).getRigidBody();
            Preconditions.checkNotNull(rigidBody);

            if (player.equals(rigidBody.getPriorityPlayer())) {
                PhysicsThread.get(level).execute(() -> {
                    rigidBody.setPhysicsRotation(rotation);
                    rigidBody.setPhysicsLocation(location);
                    rigidBody.setLinearVelocity(linearVelocity);
                    rigidBody.setAngularVelocity(angularVelocity);
                    rigidBody.activate();
                });
            }
        }
    }
}
