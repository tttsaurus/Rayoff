package com.tttsaurus.rayoff.impl.bullet.collision.space.cache;

import com.jme3.math.Vector3f;
import com.tttsaurus.rayoff.toolbox.api.blockstate.AdvBlockState;
import com.tttsaurus.rayoff.toolbox.api.math.MatrixStack;
import com.tttsaurus.rayoff.impl.bullet.collision.body.shape.MinecraftShape;
import com.tttsaurus.rayoff.impl.bullet.collision.space.MinecraftSpace;
import com.tttsaurus.rayoff.impl.bullet.collision.space.block.BlockProperty;
import com.tttsaurus.rayoff.toolbox.api.compat.Convert;
import com.tttsaurus.rayoff.toolbox.api.pattern.Disassembler;
import com.tttsaurus.rayoff.toolbox.api.pattern.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * Used for storing block that can be queried during physics execution.
 * An implementation of this should be updated/reloaded every tick on the
 * main game thread.
 * @see MinecraftSpace#step
 */
public interface ChunkCache {
    static ChunkCache create(MinecraftSpace space) {
        return new SimpleChunkCache(space);
    }

    static boolean isValidBlock(IBlockState blockState, World world, BlockPos pos) {
        if (blockState == null) {
            return false;
        }

        final var block = blockState.getBlock();
        final var properties = BlockProperty.getBlockProperty(block);

        return properties != null ? properties.collidable() :
                (blockState.getBlock() != Blocks.AIR)
                        && (!block.isPassable(world, pos))
                        && (blockState.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)
                        && (!blockState.getMaterial().isLiquid());
    }

    static Pattern genShapeForBlock(World world, BlockPos blockPos, IBlockState blockState) {
        final var tileEntity = world.getTileEntity(blockPos);
        final var transformation = new MatrixStack();
        transformation.scale(0.95f, 0.95f, 0.95f);
        transformation.translate(-0.5f, -0.5f, -0.5f);

        try {
            if (tileEntity != null) {
                return Disassembler.getTileEntity(tileEntity, transformation);
            } else {
                return Disassembler.getBlock(blockState, transformation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void refreshAll();
    void loadBlockData(BlockPos blockPos);
    void loadFluidData(BlockPos blockPos);

    MinecraftSpace getSpace();
    List<BlockData> getBlockData();
    List<FluidColumn> getFluidColumns();
    Optional<BlockData> getBlockData(BlockPos blockPos);
    Optional<FluidColumn> getFluidColumn(BlockPos blockPos);

    boolean isActive(BlockPos blockPos);

    record BlockData (World level, BlockPos blockPos, IBlockState blockState, MinecraftShape shape) { }
    record FluidData (World level, BlockPos blockPos, IBlockState fluidState) { }

    class FluidColumn {
        private final FluidData top;
        private final FluidData bottom;
        private final Vector3f flow;
        private final float height;
        private long index;

        public FluidColumn(BlockPos start, World world) {
            this.index = Integer.toUnsignedLong(start.getX()) << 32L | Integer.toUnsignedLong(start.getZ());
            final var cursor = new BlockPos.MutableBlockPos(start);
            var blockState = AdvBlockState.getFrom(world, cursor);

            // find bottom block
            while (blockState.isFluid()) {
                cursor.offset(EnumFacing.DOWN);
                blockState = AdvBlockState.getFrom(world, cursor);
            }

            cursor.offset(EnumFacing.UP); // the above loop ends at one below the bottom
            blockState = AdvBlockState.getFrom(world, cursor);
            this.bottom = new FluidData(world, new BlockPos(cursor), blockState.getBlockState());

            // find top block
            while (blockState.isFluid()) {
                cursor.offset(EnumFacing.UP);
                blockState = AdvBlockState.getFrom(world, cursor);
            }

            cursor.offset(EnumFacing.DOWN);
            blockState = AdvBlockState.getFrom(world, cursor);

            this.top = new FluidData(world, new BlockPos(cursor), blockState.getBlockState());
            this.height = blockState.isFluidSource() ? 1f : blockState.getBlockState().getValue(BlockLiquid.LEVEL) / 16f;

            // Water flow direction
            this.flow = Convert.toBulletVec3(blockState.getFlowVector());
        }

        public boolean contains(BlockPos blockPos) {
            return top.blockPos.getX() == blockPos.getX()
                    && top.blockPos.getZ() == blockPos.getZ()
                    && top.blockPos.getY() >= blockPos.getY()
                    && bottom.blockPos.getY() <= blockPos.getY();
        }

        public FluidData getTop() {
            return this.top;
        }

        public FluidData getBottom() {
            return this.bottom;
        }

        public float getTopHeight(Vector3f position) {
//            if (flow.lengthSquared() == 0) {
//                return 0.875f;
//            }
//            final var x = position.x;
//            final var z = position.z;
//
//            final var minHeight = 0.125f;
//            final var maxHeight = height;
//
//            final var xhat = flow.dot(new Vector3f(1, 0, 0));
//            final var zhat = flow.dot(new Vector3f(0, 0, 1));
//
//            if (xhat == 0) {
//                if (zhat > 0) {
//                    return (1.0f - z) * maxHeight + z * minHeight;
//                } else if (zhat < 0) {
//                    return (1.0f - z) * minHeight + z * maxHeight;
//                }
//            } else if (zhat == 0) {
//                if (xhat > 0) {
//                    return (1.0f - x) * maxHeight + x * minHeight;
//                } else if (xhat < 0) {
//                    return (1.0f - x) * minHeight + x * maxHeight;
//                }
//            }

//            final var hitResult = topShape.clip(VectorHelper.toVec3(Convert.toMinecraft(position)), new Vec3(top.blockPos.getX() + 0.5f, top.blockPos.getY(), top.blockPos.getZ() + 0.5f), top.blockPos);
//            final var y = topShape.collide(Direction.Axis.Y, new AABB(top.blockPos).move(VectorHelper.toVec3(Convert.toMinecraft(position))), 0.875f);

//            if (hitResult != null) {
//                return position.y - (float) hitResult.getLocation().y;
//            }

            return height;
        }

        public int getHeight() {
            return this.top.blockPos.getY() - this.bottom.blockPos.getY() + 1;
        }

        public Vector3f getFlow() {
            return this.flow;
        }

        public long getIndex() {
            return this.index;
        }
    }
}