package com.tttsaurus.rayoff.toolbox.api.pattern.model;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class Quad {
    private final Vec3d p1;
    private final Vec3d p2;
    private final Vec3d p3;
    private final Vec3d p4;
    private final List<Vec3d> points;

    public Quad(@NonNull List<@NonNull Vec3d> points) {
        this(points.get(0), points.get(1), points.get(2), points.get(3));
    }

    public Quad(@NonNull Vec3d p1, @NonNull Vec3d p2, @NonNull Vec3d p3, @NonNull Vec3d p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        points = ImmutableList.of(p1, p2, p3, p4);
    }

    public @NonNull List<@NonNull Vec3d> getPoints() {
        return points;
    }

    public void serialize(@NonNull ByteBuf buf) {
        for (var point : getPoints()) {
            buf.writeDouble(point.x);
            buf.writeDouble(point.y);
            buf.writeDouble(point.z);
        }
    }

    @NonNull
    public static Quad deserialize(@NonNull ByteBuf buf) {
        var points = new ArrayList<Vec3d>();
        for (var j = 0; j < 4; j++) {
            points.add(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return new Quad(points);
    }

    @Override
    public boolean equals(@NonNull Object obj) {
        if (obj instanceof Quad quad) {
            return quad.getPoints().equals(getPoints());
        }
        return false;
    }
}
