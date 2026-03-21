package com.tttsaurus.rayoff.impl.util.debug;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.tttsaurus.rayoff.api.event.render.DebugRenderEvents;
import com.tttsaurus.rayoff.impl.bullet.collision.body.MinecraftRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.body.shape.MinecraftShape;
import com.tttsaurus.rayoff.impl.bullet.collision.body.ElementRigidBody;
import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.toolbox.api.math.QuaternionHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * This class handles debug rendering on the client. Press F3+r to render
 * all {@link ElementRigidBody} objects present in the {@link MinecraftSpace}.
 */
public final class CollisionObjectDebugger {
    private static boolean enabled;

    private CollisionObjectDebugger() {}

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void renderSpace(MinecraftSpace space, float tickDelta) {
        final BufferBuilder builder = Tessellator.getInstance().getBuffer();
        DebugRenderEvents.BEFORE_RENDER.invoke(new DebugRenderEvents.Context(space));
        builder.begin(1, DefaultVertexFormats.POSITION_COLOR);

        space.getTerrainMap().values().forEach(terrain -> CollisionObjectDebugger.renderBody(terrain, builder, tickDelta));
        space.getRigidBodiesByClass(ElementRigidBody.class).forEach(elementRigidBody -> CollisionObjectDebugger.renderBody(elementRigidBody, builder, tickDelta));
        Tessellator.getInstance().draw();
    }

    public static void renderBody(MinecraftRigidBody rigidBody, BufferBuilder builder, float tickDelta) {
        final var position = rigidBody.isStatic() ?
                rigidBody.getPhysicsLocation(new Vector3f()) :
                ((ElementRigidBody) rigidBody).getFrame().getLocation(new Vector3f(), tickDelta);

        final var rotation = rigidBody.isStatic() ?
                rigidBody.getPhysicsRotation(new Quaternion()) :
                ((ElementRigidBody) rigidBody).getFrame().getRotation(new Quaternion(), tickDelta);

        renderShape(rigidBody.getMinecraftShape(), position, rotation, builder, rigidBody.getOutlineColor(), 1.0f);
    }

    public static void renderShape(MinecraftShape shape, Vector3f position, Quaternion rotation, BufferBuilder builder, Vector3f color, float alpha) {
        final var triangles = shape.getTriangles(Quaternion.IDENTITY);
        final var camera = Minecraft.getMinecraft().getRenderViewEntity();
        final double cameraX = camera == null ? 0.0 : camera.lastTickPosX + (camera.posX - camera.lastTickPosX);
        final double cameraY = camera == null ? 0.0 : camera.lastTickPosY + (camera.posY - camera.lastTickPosY);
        final double cameraZ = camera == null ? 0.0 : camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ);
        final var angles = QuaternionHelper.toEulerAngles(com.tttsaurus.rayoff.toolbox.api.compat.Convert.toJomlQuat(rotation));
        final float pitch = (float) Math.toDegrees(angles.x());
        final float yaw = (float) Math.toDegrees(angles.y());
        final float roll = (float) Math.toDegrees(angles.z());

        for (var triangle : triangles) {
            final var vertices = triangle.getVertices();

            GlStateManager.pushMatrix();
            GlStateManager.translate(position.x - cameraX, position.y - cameraY, position.z - cameraZ);
            GlStateManager.rotate(roll, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotate(yaw, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(pitch, 1.0f, 0.0f, 0.0f);
            final var p1 = vertices[0];
            final var p2 = vertices[1];
            final var p3 = vertices[2];

            builder.pos(p1.x, p1.y, p1.z).color(color.x, color.y, color.z, alpha).endVertex();
            builder.pos(p2.x, p2.y, p2.z).color(color.x, color.y, color.z, alpha).endVertex();
            builder.pos(p3.x, p3.y, p3.z).color(color.x, color.y, color.z, alpha).endVertex();
            builder.pos(p1.x, p1.y, p1.z).color(color.x, color.y, color.z, alpha).endVertex();
            GlStateManager.popMatrix();
        }
    }
}
