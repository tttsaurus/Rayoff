package com.tttsaurus.rayoff.toolbox.api.math;

import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class QuaternionHelper {
    private QuaternionHelper() {
    }

    public static Quaternionf rotateX(Quaternionf quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.mul(new Quaternionf((float) Math.sin(radHalfAngle), 0.0f, 0.0f, (float) Math.cos(radHalfAngle)));
        return quat;
    }

    public static Quaternionf rotateY(Quaternionf quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.mul(new Quaternionf(0.0f, (float) Math.sin(radHalfAngle), 0.0f, (float) Math.cos(radHalfAngle)));
        return quat;
    }

    public static Quaternionf rotateZ(Quaternionf quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.mul(new Quaternionf(0.0f, 0.0f, (float) Math.sin(radHalfAngle), (float) Math.cos(radHalfAngle)));
        return quat;
    }

    public static Vector3f toEulerAngles(Quaternionf quat) {
        Quaternionf q = new Quaternionf(quat);
        float roll;
        float pitch;
        float yaw;

        float sinrCosp = 2.0f * (q.w() * q.x() + q.y() * q.z());
        float cosrCosp = 1.0f - 2.0f * (q.x() * q.x() + q.y() * q.y());
        roll = (float) Math.atan2(sinrCosp, cosrCosp);

        float sinp = 2.0f * (q.w() * q.y() - q.z() * q.x());
        if (Math.abs(sinp) >= 1.0f) {
            pitch = (float) Math.copySign(Math.PI / 2.0, sinp);
        } else {
            pitch = (float) Math.asin(sinp);
        }

        float sinyCosp = 2.0f * (q.w() * q.z() + q.x() * q.y());
        float cosyCosp = 1.0f - 2.0f * (q.y() * q.y() + q.z() * q.z());
        yaw = (float) Math.atan2(sinyCosp, cosyCosp);

        return new Vector3f(roll, pitch, yaw);
    }

    public static NBTTagCompound toTag(Quaternionf quat) {
        return Convert.toQuatTag(quat);
    }

    public static Quaternionf fromTag(NBTTagCompound tag) {
        return Convert.fromTagJomlQuat(tag);
    }

    public static void toBuffer(ByteBuf buf, Quaternionf quat) {
        Convert.quatToBuffer(buf, quat);
    }

    public static Quaternionf fromBuffer(ByteBuf buf) {
        return Convert.fromBufferJomlQuat(buf);
    }

    public static float getYaw(Quaternionf quat) {
        return -1.0f * (float) Math.toDegrees(toEulerAngles(quat).z());
    }

    public static float getPitch(Quaternionf quat) {
        return (float) Math.toDegrees(toEulerAngles(quat).y());
    }

    public static float getRoll(Quaternionf quat) {
        return (float) Math.toDegrees(toEulerAngles(quat).x());
    }

    public static Quaternionf slerp(Quaternionf q1, Quaternionf q2, float t) {
        Quaternionf start = new Quaternionf(q1).normalize();
        Quaternionf end = new Quaternionf(q2).normalize();

        if (start.x() == end.x() && start.y() == end.y() && start.z() == end.z() && start.w() == end.w()) {
            return new Quaternionf(start);
        }

        float result = dot(start, end);
        if (result < 0.0f) {
            end.set(-end.x(), -end.y(), -end.z(), -end.w());
            result = -result;
        }

        float scale0 = 1.0f - t;
        float scale1 = t;

        if ((1.0f - result) > 0.1f) {
            float theta = (float) Math.acos(result);
            float invSinTheta = 1.0f / (float) Math.sin(theta);
            scale0 = (float) Math.sin((1.0f - t) * theta) * invSinTheta;
            scale1 = (float) Math.sin(t * theta) * invSinTheta;
        }

        Quaternionf out = new Quaternionf(
                scale0 * start.x() + scale1 * end.x(),
                scale0 * start.y() + scale1 * end.y(),
                scale0 * start.z() + scale1 * end.z(),
                scale0 * start.w() + scale1 * end.w()
        );
        return out.normalize();
    }

    public static float dot(Quaternionf q1, Quaternionf q2) {
        return q1.x() * q2.x() + q1.y() * q2.y() + q1.z() * q2.z() + q1.w() * q2.w();
    }
}
