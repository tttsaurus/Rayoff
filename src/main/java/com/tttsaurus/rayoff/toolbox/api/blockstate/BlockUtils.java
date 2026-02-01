package com.tttsaurus.rayoff.toolbox.api.blockstate;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class BlockUtils {

    private BlockUtils() {
    }

    @SuppressWarnings("DataFlowIssue")
    @NonNull
    public static String toString(@Nullable IBlockState blockState) {
        if (blockState == null) {
            return "";
        } else {
            return blockState.getBlock().getRegistryName().toString() + ":" + blockState.getBlock().getMetaFromState(blockState);
        }
    }

    public static int getMeta(@Nullable IBlockState blockState) {
        if (blockState == null) {
            return 0;
        }
        else {
            return blockState.getBlock().getMetaFromState(blockState);
        }
    }

    @NonNull
    public static ItemStack getItemStack(@NonNull IBlockState blockState) {
        Preconditions.checkNotNull(blockState);

        return getItemStack(blockState, 1);
    }

    @NonNull
    public static ItemStack getItemStack(@NonNull IBlockState blockState, int amount) {
        Preconditions.checkNotNull(blockState);

        return new ItemStack(blockState.getBlock(), amount, getMeta(blockState));
    }

    @NonNull
    public static Block getBlock(@NonNull String id) {
        Preconditions.checkNotNull(id);

        return Block.REGISTRY.getObject(new ResourceLocation(id));
    }

    @SuppressWarnings("deprecation")
    @NonNull
    public static IBlockState getBlockState(@NonNull String id, int meta) {
        Preconditions.checkNotNull(id);

        return getBlock(id).getStateFromMeta(meta);
    }

    @NonNull
    public static IBlockState getBlockState(@NonNull String id) {
        Preconditions.checkNotNull(id);

        return getBlockState(id, 0);
    }

    @SuppressWarnings("DataFlowIssue")
    @NonNull
    public static IBlockState getBlockState(@NonNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack);

        return getBlockState(itemStack.getItem().getRegistryName().toString(), itemStack.getMetadata());
    }
}
