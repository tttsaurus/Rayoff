package com.tttsaurus.rayoff.impl.event;

import com.jme3.math.Vector3f;
import com.tttsaurus.rayoff.api.EntityPhysicsElement;
import com.tttsaurus.rayoff.api.event.collision.PhysicsSpaceEvents;
import com.tttsaurus.rayoff.impl.bullet.collision.body.ElementRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.entity.ClientEntitySupplier;
import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import com.tttsaurus.rayoff.impl.event.network.EntityNetworking;
import com.tttsaurus.rayoff.impl.bullet.collision.body.EntityRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import com.tttsaurus.rayoff.impl.bullet.collision.space.storage.SpaceStorage;
import com.tttsaurus.rayoff.impl.bullet.collision.space.supplier.level.ClientLevelSupplier;
import com.tttsaurus.rayoff.impl.bullet.thread.PhysicsThread;
import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.impl.bullet.thread.util.ClientUtils;
import com.tttsaurus.rayoff.impl.util.debug.CollisionObjectDebugger;
import com.tttsaurus.rayoff.toolbox.api.event.ClientEvents;
import com.tttsaurus.rayoff.toolbox.api.math.QuaternionHelper;
import com.tttsaurus.rayoff.toolbox.api.math.VectorHelper;
import com.tttsaurus.rayoff.toolbox.api.network.PacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public final class ClientEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Client Events
        ClientEvents.Lifecycle.PRE_LOGIN.register(ClientEventHandler::onGameJoin);
        ClientEvents.Lifecycle.DISCONNECT.register(ClientEventHandler::onDisconnect);
        ClientEvents.Tick.END_CLIENT_TICK.register(ClientEventHandler::onClientTick);

        // Level Events
        ClientEvents.Lifecycle.LOAD_LEVEL.register(ClientEventHandler::onLevelLoad);
        ClientEvents.Tick.END_LEVEL_TICK.register(ClientEventHandler::onStartLevelTick);
        ClientEvents.Tick.END_LEVEL_TICK.register(ClientEventHandler::onEntityStartLevelTick);

        // Render Events
        ClientEvents.Render.BEFORE_DEBUG.register((tickDelta, level) -> ClientEventHandler.onDebugRender(level, tickDelta));

        // Entity Events
        ClientEvents.Entity.LOAD.register(ClientEventHandler::onEntityLoad);
        ClientEvents.Entity.UNLOAD.register(ClientEventHandler::onEntityUnload);
    }

    public static void onStartLevelTick(World level) {
        if (!ClientUtils.isPaused()) {
            MinecraftSpace.get(level).step();
        }
    }

    public static void onLevelLoad(Minecraft minecraft, WorldClient level) {
        var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        PhysicsSpaceEvents.INIT.invoke(space);
    }

    public static void onClientTick(Minecraft minecraft) {
        if (thread != null && thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onGameJoin(Minecraft minecraft) {
        var supplier = new ClientLevelSupplier(minecraft);
        thread = new PhysicsThread(minecraft, Thread.currentThread(), supplier, new ClientEntitySupplier(), "Client Physics Thread");
    }

    public static void onDisconnect(Minecraft minecraft, WorldClient level) {
        if (thread != null) {
            thread.destroy();
            thread = null;
        }
    }

    public static void onDebugRender(World level, float tickDelta) {
        if (CollisionObjectDebugger.isEnabled()) {
            CollisionObjectDebugger.renderSpace(MinecraftSpace.get(level), tickDelta);
        }
    }

    public static void onEntityLoad(Entity entity) {
        if (EntityPhysicsElement.is(entity)) {
            var level = entity.world;

            PhysicsThread.get(level).execute(
                    () -> MinecraftSpace.getOptional(level).ifPresent(
                            space -> space.addCollisionObject(EntityPhysicsElement.get(entity).getRigidBody())
                    )
            );
        }
    }

    public static void onEntityUnload(Entity entity) {
        if (EntityPhysicsElement.is(entity)) {
            var level = entity.world;

            PhysicsThread.get(level).execute(
                    () -> MinecraftSpace.getOptional(level).ifPresent(
                            space -> space.removeCollisionObject(EntityPhysicsElement.get(entity).getRigidBody())
                    )
            );
        }
    }

    public static void onEntityStartLevelTick(World level) {
        var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.step(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            var player = Minecraft.getMinecraft().player;

            /* Movement */
            if (rigidBody.isActive() && rigidBody.isPositionDirty() && player != null && player.equals(rigidBody.getPriorityPlayer())) {
                EntityNetworking.sendMovement(rigidBody);
            }

            /* Set entity position */
            var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            rigidBody.getElement().cast().setPosition(location.x, location.y, location.z);
        }
    }

    public static void onMovementPacketReceived(PacketRegistry.ClientboundContext context) {
        var buf = context.byteBuf();
        var entityId = buf.readInt();
        var rotation = Convert.toBullet(QuaternionHelper.fromBuffer(buf));
        var location = Convert.toBullet(VectorHelper.fromBuffer(buf));
        var linearVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        var angularVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        var level = Minecraft.getMinecraft().world;

        if (level != null) {
            var entity = level.getEntityByID(entityId);

            if (EntityPhysicsElement.is(entity)) {
                var rigidBody = EntityPhysicsElement.get(entity).getRigidBody();

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

    public static void onPropertiesPacketReceived(PacketRegistry.ClientboundContext context) {
        var buf = context.byteBuf();
        var entityId = buf.readInt();
        var mass = buf.readFloat();
        var dragCoefficient = buf.readFloat();
        var friction = buf.readFloat();
        var restitution = buf.readFloat();
        var terrainLoading = buf.readBoolean();
        var buoyancyType = buf.readEnumValue(ElementRigidBody.BuoyancyType.class);
        var dragType = buf.readEnumValue(ElementRigidBody.DragType.class);
        var priorityPlayer = buf.readUniqueId();
        var level = Minecraft.getMinecraft().world;

        if (level != null) {
            var entity = level.getEntityByID(entityId);

            if (EntityPhysicsElement.is(entity)) {
                var rigidBody = EntityPhysicsElement.get(entity).getRigidBody();

                PhysicsThread.get(level).execute(() -> {
                    rigidBody.setMass(mass);
                    rigidBody.setDragCoefficient(dragCoefficient);
                    rigidBody.setFriction(friction);
                    rigidBody.setRestitution(restitution);
                    rigidBody.setTerrainLoadingEnabled(terrainLoading);
                    rigidBody.setBuoyancyType(buoyancyType);
                    rigidBody.setDragType(dragType);
                    rigidBody.prioritize(rigidBody.getSpace().getWorld().getPlayerEntityByUUID(priorityPlayer));
                    rigidBody.activate();
                });
            }
        }
    }
}
