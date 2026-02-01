package com.tttsaurus.rayoff.toolbox.api.blockstate;

import com.cleanroommc.kirino.utils.ReflectionUtils;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;

public final class AdvBlockState {

    public enum StateType {
        BLOCK,
        FLUID
    }

    private AdvBlockState() {
    }

    public static final String KEYWORD_BLOCK = "BLOCK";
    public static final String KEYWORD_FLUID_SOURCE = "FLUID_SOURCE";
    public static final String KEYWORD_FLUID_FLOWING = "FLUID_FLOWING";

    public static final AdvBlockState AIR = new AdvBlockState(Blocks.AIR.getDefaultState());
    public static final AdvBlockState SOURCE_WATER = new AdvBlockState(FluidRegistry.WATER, true);
    public static final AdvBlockState FLOWING_WATER = new AdvBlockState(FluidRegistry.WATER, false);
    public static final AdvBlockState SOURCE_LAVA = new AdvBlockState(FluidRegistry.LAVA, true);
    public static final AdvBlockState FLOWING_LAVA = new AdvBlockState(FluidRegistry.LAVA, false);

    private StateType stateType;
    private Fluid fluid;
    private boolean isFluidSource;
    private IBlockState blockState;
    private Vec3d flowVector = new Vec3d(0, 0, 0);

    public boolean isFluid() {
        return stateType == StateType.FLUID;
    }

    @NonNull
    public StateType getStateType() {
        return stateType;
    }

    @Nullable
    public Fluid getFluid() {
        return fluid;
    }

    public boolean isFluidSource() {
        return isFluidSource;
    }

    @Nullable
    public IBlockState getBlockState() {
        return blockState;
    }

    @NonNull
    public Vec3d getFlowVector() {
        return flowVector;
    }

    public void setIsFluidSource(boolean isFluidSource) {
        this.isFluidSource = isFluidSource;
    }

    @NonNull
    @Override
    public String toString() {
        if (stateType == StateType.BLOCK && blockState != null) {
            return KEYWORD_BLOCK + "{" + BlockUtils.toString(blockState) + "}";
        } else if (stateType == StateType.FLUID && fluid != null) {
            if (isFluidSource) {
                return KEYWORD_FLUID_SOURCE + "{" + fluid.getName() + "}";
            } else {
                return KEYWORD_FLUID_FLOWING + "{" + fluid.getName() + "}";
            }
        } else {
            return "";
        }
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AdvBlockState && object.toString().equals(this.toString());
    }

    public AdvBlockState(@NonNull Fluid fluid, boolean isFluidSource) {
        Preconditions.checkNotNull(fluid);

        this.stateType = StateType.FLUID;
        this.fluid = fluid;
        this.isFluidSource = isFluidSource;
        this.blockState = null;
    }

    public AdvBlockState(@NonNull IBlockState blockState) {
        Preconditions.checkNotNull(blockState);

        this.stateType = StateType.BLOCK;
        this.fluid = null;
        this.isFluidSource = false;
        this.blockState = blockState;
    }

    @NonNull
    public static AdvBlockState getFrom(@NonNull World world, @NonNull BlockPos pos) {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        IBlockState blockState = world.getBlockState(pos);

        Block block = blockState.getBlock();
        ResourceLocation rl = block.getRegistryName();
        if (rl == null) {
            return AIR;
        }

        String registryName = rl.toString();

        // air
        if (registryName.equals("minecraft:air")) {
            return AIR;
        }

        AdvBlockState res = new AdvBlockState();
        res.blockState = blockState;

        // vanilla liquid
        if (block instanceof BlockLiquid fluidBase) {
            res.stateType = StateType.FLUID;

            if (registryName.equals("minecraft:water") || registryName.equals("minecraft:flowing_water")) {
                res.fluid = FluidRegistry.WATER;
            } else if (registryName.equals("minecraft:lava") || registryName.equals("minecraft:flowing_lava")) {
                res.fluid = FluidRegistry.LAVA;
            }

            res.isFluidSource = blockState.getValue(BlockLiquid.LEVEL) == 0;
            res.flowVector = MethodHolder.getFlow(fluidBase, world, pos, blockState);
        }
        // modded fluid
        else if (block instanceof BlockFluidBase fluidBase) {
            res.stateType = StateType.FLUID;
            res.fluid = fluidBase.getFluid();
            res.isFluidSource = blockState.getValue(BlockLiquid.LEVEL) == 0;
            res.flowVector = fluidBase.getFlowVector(world, pos);
        }
        // solid block
        else {
            res.stateType = StateType.BLOCK;
        }

        return res;
    }

    private static class MethodHolder {
        private static final Delegate DELEGATE;

        static {
            DELEGATE = new Delegate(ReflectionUtils.getMethod(
                    BlockLiquid.class,
                    "getFlow",
                    "func_189543_a(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/math/Vec3d;",
                    Vec3d.class,
                    IBlockAccess.class, BlockPos.class, IBlockState.class));

            Preconditions.checkNotNull(DELEGATE.getFlow);
        }

        static Vec3d getFlow(BlockLiquid owner, IBlockAccess worldIn, BlockPos pos, IBlockState state) {
            try {
                return (Vec3d) DELEGATE.getFlow.invokeExact(owner, worldIn, pos, state);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        record Delegate(MethodHandle getFlow) {
        }
    }
}
