package com.tttsaurus.rayoff.toolbox.impl.pattern;

import com.tttsaurus.rayoff.toolbox.api.pattern.Pattern;
import com.tttsaurus.rayoff.toolbox.api.pattern.model.Quad;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * The main implementation of {@link Pattern}. This is what is stored within a {@link PatternBuffer}.
 * @see com.tttsaurus.rayoff.toolbox.impl.pattern.buffer.PatternBufferImpl
 * @see com.tttsaurus.rayoff.toolbox.api.pattern.Disassembler
 */
public class BufferEntry implements Pattern {

    private final List<Quad> quads;
    private final Pattern.Type type;
    private final int registryId;

    public BufferEntry(@NonNull List<@NonNull Quad> quads, Pattern.@NonNull Type type, int registryId) {
        this.quads = quads;
        this.type = type;
        this.registryId = registryId;
    }

    public BufferEntry(@NonNull Pattern pattern, Pattern.@NonNull Type type, int registryId) {
        this(pattern.getQuads(), type, registryId);
    }

    public Pattern.Type getType() {
        return this.type;
    }

    public int getRegistryId() {
        return this.registryId;
    }

    @Override
    public @NonNull List<@NonNull Quad> getQuads() {
        return this.quads;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BufferEntry entry) {
            return entry.getQuads().equals(getQuads()) && entry.getRegistryId() == registryId;
        }
        return false;
    }

    @NonNull
    public static BufferEntry deserialize(@NonNull ByteBuf buf) {
        var type = readEnumFromByteBuf(buf, Pattern.Type.class);
        int registryId = buf.readInt();
        int quadCount = buf.readInt();
        var quads = new LinkedList<Quad>();
        for (int i = 0; i < quadCount; i++) {
            quads.add(Quad.deserialize(buf));
        }
        return new BufferEntry(quads, type, registryId);
    }

    private static <T extends Enum<T>> T readEnumFromByteBuf(@NonNull ByteBuf buf, @NonNull Class<T> enumClass) {
        int index = buf.readInt();
        T[] constants = enumClass.getEnumConstants();
        if (index < 0 || index >= constants.length) {
            throw new IndexOutOfBoundsException("Invalid enum index: " + index + " for " + enumClass.getName() + ".");
        }
        return constants[index];
    }
}
