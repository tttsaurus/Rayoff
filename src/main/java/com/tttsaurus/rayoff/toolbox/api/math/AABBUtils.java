package com.tttsaurus.rayoff.toolbox.api.math;

import net.minecraft.util.math.AxisAlignedBB;
import org.jspecify.annotations.NonNull;

public final class AABBUtils {

    private AABBUtils() {
    }

    @NonNull
    public static AxisAlignedBB inflate(@NonNull AxisAlignedBB box, double amount) {
        return new AxisAlignedBB(
                box.minX - amount,
                box.minY - amount,
                box.minZ - amount,
                box.maxX + amount,
                box.maxY + amount,
                box.maxZ + amount);
    }
}
