package com.tttsaurus.rayoff.toolbox.impl.pattern.buffer;

import com.tttsaurus.rayoff.toolbox.api.pattern.Pattern;
import com.tttsaurus.rayoff.toolbox.api.pattern.buffer.ServerPatternBuffer;
import com.tttsaurus.rayoff.toolbox.impl.pattern.BufferEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jspecify.annotations.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @see com.tttsaurus.rayoff.toolbox.api.pattern.buffer.PatternBuffer
 */
public class PatternBufferImpl implements ServerPatternBuffer {

    private final Int2ObjectMap<BufferEntry> entityPatterns = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<BufferEntry> itemPatterns = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<BufferEntry> blockPatterns = new Int2ObjectOpenHashMap<>();

    @Override
    @NonNull
    public Pattern get(Pattern.@NonNull Type type, int registryId) {
        return (Pattern) switch (type) {
            case ENTITY -> entityPatterns.get(registryId);
            case ITEM -> itemPatterns.get(registryId);
            case BLOCK -> blockPatterns.get(registryId);
        };
    }

    @Override
    public boolean contains(Pattern.@NonNull Type type, int registryId) {
        return switch (type) {
            case ENTITY -> entityPatterns.containsKey(registryId);
            case ITEM -> itemPatterns.containsKey(registryId);
            case BLOCK -> blockPatterns.containsKey(registryId);
        };
    }

    @Override
    public int size() {
        return entityPatterns.size() + itemPatterns.size() + blockPatterns.size();
    }

    @Override
    public void put(@NonNull Pattern pattern) {
        var bufferEntry = (BufferEntry) pattern;
        var registryId = bufferEntry.getRegistryId();

        switch (bufferEntry.getType()) {
            case ENTITY -> entityPatterns.put(registryId, (BufferEntry) pattern);
            case ITEM -> itemPatterns.put(registryId, (BufferEntry) pattern);
            case BLOCK -> blockPatterns.put(registryId, (BufferEntry) pattern);
        }
    }

    @Override
    public void clear() {
        entityPatterns.clear();
        itemPatterns.clear();
        blockPatterns.clear();
    }

    @Override
    public @NonNull List<@NonNull Pattern> getAll() {
        var out = new LinkedList<Pattern>();
        out.addAll(entityPatterns.values());
        out.addAll(itemPatterns.values());
        out.addAll(blockPatterns.values());
        return out;
    }
}
