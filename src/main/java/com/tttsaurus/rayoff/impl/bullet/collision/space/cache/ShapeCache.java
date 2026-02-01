package com.tttsaurus.rayoff.impl.bullet.collision.space.cache;

import com.tttsaurus.rayoff.impl.bullet.collision.body.shape.MinecraftShape;
import com.tttsaurus.rayoff.impl.bullet.collision.space.block.BlockProperty;
import com.tttsaurus.rayoff.toolbox.api.pattern.Pattern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ShapeCache {
    private static final MinecraftShape FALLBACK_SHAPE = MinecraftShape.convex(new AxisAlignedBB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f));

    private static final IdentityHashMap<IBlockState, MinecraftShape> SHAPES_SERVER = new IdentityHashMap<>();
    private static final IdentityHashMap<IBlockState, MinecraftShape> SHAPES_CLIENT = new IdentityHashMap<>();

    public static MinecraftShape getShapeFor(IBlockState blockState, World world, BlockPos blockPos) {
        if (blockState.getBlock().hasDynamicShape()) {
            return createShapeFor(blockState, world, blockPos);
        }

        final var shapes = getShapes(world.isClientSide);
        var shape = shapes.get(blockState);

        if (shape == null) {
            shape = createShapeFor(blockState, world, BlockPos.ZERO);
            shapes.put(blockState, shape);
        }

        return shape;
    }

    private static Map<IBlockState, MinecraftShape> getShapes(boolean isClientSide) {
        return isClientSide ? SHAPES_CLIENT : SHAPES_SERVER;
    }

    @Nullable
    private static MinecraftShape createShapeFor(IBlockState blockState, World world, BlockPos blockPos) {
        final var properties = BlockProperty.getBlockProperty(blockState.getBlock());
        MinecraftShape shape = null;

        if (!blockState.isCollisionShapeFullBlock(world, blockPos) || (properties != null && !properties.isFullBlock())) {
            Pattern pattern;

            if (world.isClientSide) {
                pattern = ChunkCache.genShapeForBlock(world, blockPos, blockState);
            } else {
                pattern = Transporter.getPatternBuffer().getBlock(Block.getId(blockState));
            }

            if (pattern != null && !pattern.getQuads().isEmpty()) {
                shape = MinecraftShape.concave(pattern);
            }
        }

        if (shape == null) {
            final var voxelShape = blockState.getCollisionShape(world, blockPos);
            if (!voxelShape.isEmpty()) {
                shape = MinecraftShape.convex(voxelShape);
            } else {
                shape = FALLBACK_SHAPE;
            }
        }
        return shape;
    }
}
