package com.tttsaurus.rayoff.toolbox.api.compat;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

public final class Convert {

    private Convert() {
    }

    //<editor-fold desc="aabb conversion">
    @NonNull
    public static BoundingBox toBulletAABB(@NonNull AxisAlignedBB aabb) {
        return new BoundingBox(toBulletVec3(aabb.getCenter()),
                (float) (aabb.maxX - aabb.minX),
                (float) (aabb.maxY - aabb.minY),
                (float) (aabb.maxZ - aabb.minZ));
    }

    @NonNull
    public static AxisAlignedBB toMcAABB(@NonNull BoundingBox aabb) {
        var min = aabb.getMin(new Vector3f());
        var max = aabb.getMax(new Vector3f());
        return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    //</editor-fold>

    //<editor-fold desc="quat conversion">
    @NonNull
    public static Quaternion toBulletQuat(@NonNull Quaternionf quat) {
        return new Quaternion(quat.x(), quat.y(), quat.z(), quat.w());
    }

    @NonNull
    public static Quaternionf toJomlQuat(@NonNull Quaternion quat) {
        return new Quaternionf(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }
    //</editor-fold>

    //<editor-fold desc="vec3 conversion">
    @NonNull
    public static Vector3f toBulletVec3(org.joml.@NonNull Vector3f vec3) {
        return new Vector3f(vec3.x(), vec3.y(), vec3.z());
    }

    @NonNull
    public static Vector3f toBulletVec3(@NonNull BlockPos blockPos) {
        return new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
    }

    @NonNull
    public static Vector3f toBulletVec3(@NonNull Vec3d vec3) {
        return new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
    }

    public static org.joml.@NonNull Vector3f toJomlVec3(@NonNull Vector3f vec3) {
        return new org.joml.Vector3f(vec3.x, vec3.y, vec3.z);
    }

    public static org.joml.@NonNull Vector3f toJomlVec3(@NonNull BlockPos blockPos) {
        return new org.joml.Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
    }

    public static org.joml.@NonNull Vector3f toJomlVec3(@NonNull Vec3d vec3) {
        return new org.joml.Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
    }

    @NonNull
    public static Vec3d toMcVec3(@NonNull Vector3f vec3) {
        return new Vec3d(vec3.x, vec3.y, vec3.z);
    }

    @NonNull
    public static Vec3d toMcVec3(org.joml.Vector3f vec3) {
        return new Vec3d(vec3.x, vec3.y, vec3.z);
    }
    //</editor-fold>

    //<editor-fold desc="vec3 tag">
    @NonNull
    public static NBTTagCompound toVec3Tag(org.joml.@NonNull Vector3f vec3) {
        final var tag = new NBTTagCompound();
        tag.setFloat("x", vec3.x());
        tag.setFloat("y", vec3.y());
        tag.setFloat("z", vec3.z());
        return tag;
    }

    @NonNull
    public static NBTTagCompound toVec3Tag(@NonNull Vector3f vec3) {
        final var tag = new NBTTagCompound();
        tag.setFloat("x", vec3.x);
        tag.setFloat("y", vec3.y);
        tag.setFloat("z", vec3.z);
        return tag;
    }

    @NonNull
    public static NBTTagCompound toVec3Tag(@NonNull Vec3d vec3) {
        final var tag = new NBTTagCompound();
        tag.setFloat("x", (float) vec3.x);
        tag.setFloat("y", (float) vec3.y);
        tag.setFloat("z", (float) vec3.z);
        return tag;
    }

    public static org.joml.@NonNull Vector3f fromTagJomlVec3(@NonNull NBTTagCompound tag) {
        return new org.joml.Vector3f(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z"));
    }

    @NonNull
    public static Vector3f fromTagBulletVec3(@NonNull NBTTagCompound tag) {
        return new Vector3f(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z"));
    }

    @NonNull
    public static Vec3d fromTagMcVec3(@NonNull NBTTagCompound tag) {
        return new Vec3d(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z"));
    }
    //</editor-fold>

    //<editor-fold desc="vec3 buf">
    public static void vec3ToBuffer(@NonNull ByteBuf buf, org.joml.@NonNull Vector3f vec3) {
        buf.writeFloat(vec3.x());
        buf.writeFloat(vec3.y());
        buf.writeFloat(vec3.z());
    }

    public static void vec3ToBuffer(@NonNull ByteBuf buf, @NonNull Vector3f vec3) {
        buf.writeFloat(vec3.x);
        buf.writeFloat(vec3.y);
        buf.writeFloat(vec3.z);
    }

    public static void vec3ToBuffer(@NonNull ByteBuf buf, @NonNull Vec3d vec3) {
        buf.writeFloat((float) vec3.x);
        buf.writeFloat((float) vec3.y);
        buf.writeFloat((float) vec3.z);
    }

    public static org.joml.@NonNull Vector3f fromBufferJomlVec3(@NonNull ByteBuf buf) {
        return new org.joml.Vector3f(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat());
    }

    @NonNull
    public static Vector3f fromBufferBulletVec3(@NonNull ByteBuf buf) {
        return new Vector3f(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat());
    }

    @NonNull
    public static Vec3d fromBufferMcVec3(@NonNull ByteBuf buf) {
        return new Vec3d(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat());
    }
    //</editor-fold>

    //<editor-fold desc="quat tag">
    @NonNull
    public static NBTTagCompound toQuatTag(@NonNull Quaternionf quat) {
        final var tag = new NBTTagCompound();
        tag.setFloat("i", quat.x());
        tag.setFloat("j", quat.y());
        tag.setFloat("k", quat.z());
        tag.setFloat("r", quat.w());
        return tag;
    }

    @NonNull
    public static NBTTagCompound toQuatTag(@NonNull Quaternion quat) {
        final var tag = new NBTTagCompound();
        tag.setFloat("i", quat.getX());
        tag.setFloat("j", quat.getY());
        tag.setFloat("k", quat.getZ());
        tag.setFloat("r", quat.getW());
        return tag;
    }

    @NonNull
    public static Quaternionf fromTagJomlQuat(@NonNull NBTTagCompound tag) {
        return new Quaternionf(
                tag.getFloat("i"),
                tag.getFloat("j"),
                tag.getFloat("k"),
                tag.getFloat("r"));
    }

    @NonNull
    public static Quaternion fromTagBulletQuat(@NonNull NBTTagCompound tag) {
        return new Quaternion(
                tag.getFloat("i"),
                tag.getFloat("j"),
                tag.getFloat("k"),
                tag.getFloat("r"));
    }
    //</editor-fold>

    //<editor-fold desc="quat buf">
    public static void quatToBuffer(@NonNull ByteBuf buf, @NonNull Quaternionf quat) {
        buf.writeFloat(quat.x());
        buf.writeFloat(quat.y());
        buf.writeFloat(quat.z());
        buf.writeFloat(quat.w());
    }

    public static void quatToBuffer(@NonNull ByteBuf buf, @NonNull Quaternion quat) {
        buf.writeFloat(quat.getX());
        buf.writeFloat(quat.getY());
        buf.writeFloat(quat.getZ());
        buf.writeFloat(quat.getW());
    }

    @NonNull
    public static Quaternionf fromBufferJomlQuat(@NonNull ByteBuf buf) {
        return new Quaternionf(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat());
    }

    @NonNull
    public static Quaternion fromBufferBulletQuat(@NonNull ByteBuf buf) {
        return new Quaternion(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat());
    }
    //</editor-fold>
}
