package com.tttsaurus.rayoff.toolbox.api.math;

import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public final class VectorHelper {
    private VectorHelper() {
    }

    public static Vector3f toVector3f(Vec3d vec3) {
        return new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
    }

    public static Vec3d toVec3(Vector3f vector3f) {
        return new Vec3d(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static Vector3f lerp(Vector3f vec1, Vector3f vec2, float delta) {
        return new Vector3f(
                (float) MathHelper.clampedLerp(vec1.x(), vec2.x(), delta),
                (float) MathHelper.clampedLerp(vec1.y(), vec2.y(), delta),
                (float) MathHelper.clampedLerp(vec1.z(), vec2.z(), delta)
        );
    }

    public static Vec3d lerp(Vec3d vec1, Vec3d vec2, float delta) {
        return new Vec3d(
                MathHelper.clampedLerp(vec1.x, vec2.x, delta),
                MathHelper.clampedLerp(vec1.y, vec2.y, delta),
                MathHelper.clampedLerp(vec1.z, vec2.z, delta)
        );
    }

    public static NBTTagCompound toTag(Vector3f vec) {
        return Convert.toVec3Tag(vec);
    }

    public static Vector3f fromTag(NBTTagCompound tag) {
        return Convert.fromTagJomlVec3(tag);
    }

    public static void toBuffer(ByteBuf buf, Vector3f vec) {
        Convert.vec3ToBuffer(buf, vec);
    }

    public static Vector3f fromBuffer(ByteBuf buf) {
        return Convert.fromBufferJomlVec3(buf);
    }
}
