package com.tttsaurus.rayoff.toolbox.api.pattern;

import com.tttsaurus.rayoff.toolbox.api.math.MatrixStack;
import com.tttsaurus.rayoff.toolbox.impl.pattern.BufferEntry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

// todo: fix
public interface Disassembler {

    @NonNull
    static Pattern getBlock(@NonNull IBlockState blockState) {
        return getBlock(blockState, null);
    }

    @NonNull
    static Pattern getBlock(@NonNull IBlockState blockState, @Nullable MatrixStack transformation) {
        return new BufferEntry(List.of(), Pattern.Type.BLOCK, Block.getIdFromBlock(blockState.getBlock()));
    }

    @NonNull
    static Pattern getTileEntity(TileEntity tileEntity) {
        return getTileEntity(tileEntity, null);
    }

    @NonNull
    static Pattern getTileEntity(TileEntity tileEntity, @Nullable MatrixStack transformation) {
        return new BufferEntry(List.of(), Pattern.Type.BLOCK, Block.getIdFromBlock(tileEntity.getBlockType()));
    }

    @NonNull
    static Pattern getEntity(Entity entity) {
        return getEntity(entity, null);
    }

    @NonNull
    static Pattern getEntity(Entity entity, @Nullable MatrixStack transformation) {
        return new BufferEntry(List.of(), Pattern.Type.ENTITY, entity.getEntityId());
    }

    @NonNull
    static Pattern getItem(Item item) {
        return getItem(item, null);
    }

    @NonNull
    static Pattern getItem(Item item, @Nullable MatrixStack transformation) {
        return new BufferEntry(List.of(), Pattern.Type.ITEM, Item.getIdFromItem(item));
    }
}
