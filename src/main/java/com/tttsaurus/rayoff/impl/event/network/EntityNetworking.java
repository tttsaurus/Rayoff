package com.tttsaurus.rayoff.impl.event.network;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.tttsaurus.rayoff.Reference;
import com.tttsaurus.rayoff.impl.bullet.collision.body.EntityRigidBody;
import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import com.tttsaurus.rayoff.impl.event.ClientEventHandler;
import com.tttsaurus.rayoff.impl.event.ServerEventHandler;
import com.tttsaurus.rayoff.toolbox.api.math.QuaternionHelper;
import com.tttsaurus.rayoff.toolbox.api.math.VectorHelper;
import com.tttsaurus.rayoff.toolbox.api.network.ClientNetworking;
import com.tttsaurus.rayoff.toolbox.api.network.PacketRegistry;
import com.tttsaurus.rayoff.toolbox.api.network.ServerNetworking;
import com.tttsaurus.rayoff.toolbox.api.util.PlayerUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;
import java.util.function.Consumer;

public interface EntityNetworking {
    ResourceLocation MOVEMENT = new ResourceLocation(Reference.MOD_ID, "movement");
    ResourceLocation PROPERTIES = new ResourceLocation(Reference.MOD_ID, "properties");

    static void registerServer() {
        PacketRegistry.registerServerbound(MOVEMENT, ServerEventHandler::onMovementPacketReceived);
    }

    static void registerClient() {
        PacketRegistry.registerClientbound(PROPERTIES, ClientEventHandler::onPropertiesPacketReceived);
        PacketRegistry.registerClientbound(MOVEMENT, ClientEventHandler::onMovementPacketReceived);
    }

    static void sendMovement(EntityRigidBody rigidBody) {
        final Consumer<PacketBuffer> encoder = buf -> {
            buf.writeInt(rigidBody.getElement().cast().getEntityId());
            QuaternionHelper.toBuffer(buf, Convert.toJomlQuat(rigidBody.getPhysicsRotation(new Quaternion())));
            VectorHelper.toBuffer(buf, Convert.toJomlVec3(rigidBody.getPhysicsLocation(new Vector3f())));
            VectorHelper.toBuffer(buf, Convert.toJomlVec3(rigidBody.getLinearVelocity(new Vector3f())));
            VectorHelper.toBuffer(buf, Convert.toJomlVec3(rigidBody.getAngularVelocity(new Vector3f())));
        };

        if (rigidBody.getSpace().isServer()) {
            PlayerUtils.tracking(rigidBody.getElement().cast()).forEach(player -> {
                if (!player.equals(rigidBody.getPriorityPlayer())) {
                    ServerNetworking.send(player, MOVEMENT, encoder);
                }
            });
        } else {
            ClientNetworking.send(MOVEMENT, encoder);
        }
    }

    static void sendProperties(EntityRigidBody rigidBody) {
        final Consumer<PacketBuffer> encoder = buf -> {
            buf.writeInt(rigidBody.getElement().cast().getEntityId());
            buf.writeFloat(rigidBody.getMass());
            buf.writeFloat(rigidBody.getDragCoefficient());
            buf.writeFloat(rigidBody.getFriction());
            buf.writeFloat(rigidBody.getRestitution());
            buf.writeBoolean(rigidBody.terrainLoadingEnabled());
            buf.writeEnumValue(rigidBody.getBuoyancyType());
            buf.writeEnumValue(rigidBody.getDragType());
            buf.writeUniqueId(rigidBody.getPriorityPlayer() == null ? new UUID(0, 0) : rigidBody.getPriorityPlayer().getUniqueID());
        };

        if (rigidBody.getSpace().isServer()) {
            PlayerUtils.tracking(rigidBody.getElement().cast()).forEach(player -> ServerNetworking.send(player, PROPERTIES, encoder));
        }
    }
}
