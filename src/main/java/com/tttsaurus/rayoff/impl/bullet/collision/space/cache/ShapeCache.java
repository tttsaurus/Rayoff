package com.tttsaurus.rayoff.impl.bullet.collision.space.cache;

import com.tttsaurus.rayoff.impl.bullet.collision.body.shape.MinecraftShape;
import com.tttsaurus.rayoff.impl.bullet.collision.space.block.BlockProperty;
import com.tttsaurus.rayoff.toolbox.api.pattern.Pattern;
import com.tttsaurus.rayoff.toolbox.impl.Transporter;
import net.minecraft.block.Block;
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
    private static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    public static MinecraftShape getShapeFor(IBlockState blockState, World world, BlockPos blockPos) {
        final var shapes = getShapes(world.isRemote);
        var shape = shapes.get(blockState);

        if (shape == null) {
            shape = createShapeFor(blockState, world, ORIGIN);
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

        if ((blockState.getCollisionBoundingBox(world, blockPos) != Block.NULL_AABB && !blockState.isFullCube()) || (properties != null && !properties.isFullBlock())) {
            Pattern pattern;

            if (world.isRemote) {
                pattern = ChunkCache.genShapeForBlock(world, blockPos, blockState);
            } else {
                pattern = Transporter.getPatternBuffer().getBlock(Block.getIdFromBlock(blockState.getBlock()));
            }

            if (pattern != null && !pattern.getQuads().isEmpty()) {
                shape = MinecraftShape.concave(pattern);
            }
        }

        if (shape == null) {
            final AxisAlignedBB box = blockState.getCollisionBoundingBox(world, blockPos);
            if (box != Block.NULL_AABB) {
                shape = MinecraftShape.convex(box.offset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ()));
            } else {
                shape = FALLBACK_SHAPE;
            }
        }
        return shape;
    }
}
