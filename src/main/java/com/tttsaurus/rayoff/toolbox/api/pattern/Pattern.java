package com.tttsaurus.rayoff.toolbox.api.pattern;

import com.tttsaurus.rayoff.toolbox.api.pattern.model.Quad;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface Pattern {
    @NonNull List<@NonNull Quad> getQuads();

    enum Type {
        BLOCK, ITEM, ENTITY
    }
}
