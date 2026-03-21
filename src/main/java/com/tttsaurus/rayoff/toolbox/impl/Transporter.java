package com.tttsaurus.rayoff.toolbox.impl;

import com.tttsaurus.rayoff.toolbox.api.pattern.buffer.PatternBuffer;
import com.tttsaurus.rayoff.toolbox.impl.pattern.buffer.PatternBufferImpl;

public final class Transporter {
    private Transporter() {
    }

    private static PatternBuffer buffer;

    public static void initialize() {
    }

    public static PatternBuffer getPatternBuffer() {
        if (buffer == null) {
            buffer = new PatternBufferImpl();
        }

        return buffer;
    }
}
