package com.tttsaurus.rayoff.toolbox.api.pattern;

import com.tttsaurus.rayoff.toolbox.api.math.MatrixStack;
import com.tttsaurus.rayoff.toolbox.impl.pattern.BufferEntry;
import com.tttsaurus.rayoff.toolbox.impl.pattern.QuadConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The calls in this class are important in that they're necessary in order for anything within this library to work.
 * From here, you can obtain a {@link Pattern} based on a block, block entity, entity, or item.
 * <br>
 * In each of these methods, you have the option to transform the pattern any way you want before it
 * is read as a {@link Pattern}. This is done via passing a {@link MatrixStack}.
 * @see com.tttsaurus.rayoff.toolbox.api.pattern.buffer.PatternBuffer
 * @see Pattern
 * @since 1.0.0
 */
public interface Disassembler {

    @NonNull
    static Pattern getBlock(@NonNull IBlockState blockState) {
        return getBlock(blockState, null);
    }

    @NonNull
    static Pattern getBlock(@NonNull IBlockState blockState, @Nullable MatrixStack transformation) {
        if (transformation == null) {
            transformation = new MatrixStack();
        }

        var client = Minecraft.getMinecraft();
        var consumer = QuadConsumer.create();
        var model = client.getBlockRenderer().getBlockModel(blockState);

        Minecraft.getMinecraft().getBlockRenderer().getModelRenderer()
                .renderModel(transformation.last(), consumer, blockState, model, 0, 0, 0, 0, 0);

        var entry = new BufferEntry(consumer, Pattern.Type.BLOCK, Block.getId(blockState));
        ClientPlayNetworking.send(new PatternPacket(entry));
        return entry;
    }

    @NonNull
    static Pattern getTileEntity(TileEntity tileEntity) {
        return getTileEntity(tileEntity, null);
    }

    @NonNull
    static Pattern getTileEntity(TileEntity tileEntity, @Nullable MatrixStack transformation) {
        if (transformation == null) {
            transformation = new MatrixStack();
        }

        var consumer = QuadConsumer.create();
        var renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tileEntity);

        if (renderer != null) {
            renderer.render(tileEntity, 0, transformation, consumer.asProvider(), 0, 0);
        }

        var entry = new BufferEntry(consumer, Pattern.Type.BLOCK, Block.getId(tileEntity.getBlockState()));
        ClientPlayNetworking.send(new PatternPacket(entry));
        return entry;
    }

    @NonNull
    static Pattern getEntity(Entity entity) {
        return getEntity(entity, null);
    }

    @NonNull
    static Pattern getEntity(Entity entity, @Nullable MatrixStack transformation) {
        if (transformation == null) {
            transformation = new MatrixStack();
        }

        var consumer = QuadConsumer.create();
        Minecraft.getMinecraft().getEntityRenderDispatcher().getRenderer(entity)
                .render(entity, 0, 0, transformation, consumer.asProvider(), 0);

        var entry = new BufferEntry(consumer, Pattern.Type.ENTITY, BuiltInRegistries.ENTITY_TYPE.getId(entity.getType()));
        ClientPlayNetworking.send(new PatternPacket(entry));
        return entry;
    }

    @NonNull
    static Pattern getItem(Item item) {
        return getItem(item, null);
    }

    @NonNull
    static Pattern getItem(Item item, @Nullable MatrixStack transformation) {
        if (transformation == null) {
            transformation = new MatrixStack();
        }

        var consumer = QuadConsumer.create();
        Minecraft.getMinecraft().getItemRenderer()
                .renderStatic(new ItemStack(item), ItemDisplayContext.GROUND, 0, 0, transformation, consumer.asProvider(), null, 0);

        var entry = new BufferEntry(consumer, Pattern.Type.ITEM, BuiltInRegistries.ITEM.getId(item));
        ClientPlayNetworking.send(new PatternPacket(entry));
        return entry;
    }
}
