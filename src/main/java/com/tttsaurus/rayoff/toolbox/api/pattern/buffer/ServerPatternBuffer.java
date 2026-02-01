package com.tttsaurus.rayoff.toolbox.api.pattern.buffer;

import com.tttsaurus.rayoff.toolbox.api.pattern.Pattern;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A {@link PatternBuffer} extension that allows for modification.
 * @since 1.0.0
 */
public interface ServerPatternBuffer extends PatternBuffer {

    /**
     * Adds a pattern to the buffer.
     */
    void put(@NonNull Pattern pattern);

    /**
     * Clears the buffer.
     */
    void clear();

    /**
     * Returns all patterns in the buffer, regardless of type.
     */
    @NonNull List<@NonNull Pattern> getAll();
}
