package com.tttsaurus.rayoff.toolbox.impl.pattern;

import com.tttsaurus.rayoff.toolbox.api.pattern.Disassembler;
import com.tttsaurus.rayoff.toolbox.api.pattern.Pattern;
import com.tttsaurus.rayoff.toolbox.api.pattern.model.Quad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

/**
 * Another implementation of {@link Pattern} other than {@link BufferEntry}, this class
 * is used by {@link Disassembler} to capture vertex information and translate it into
 * a list of {@link Quad}s.
 * @see Disassembler
 * @see Quad
 */
public class QuadConsumer extends BufferBuilder implements Pattern {

    protected final List<Quad> quads = new LinkedList<>();
    protected final List<Vec3d> points = new LinkedList<>();

    public static QuadConsumer create() {
        return new QuadConsumer();
    }

    public QuadConsumer() {
        super(0x200000);
        this.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
    }

    @Override
    public @NotNull BufferBuilder pos(double x, double y, double z) {
        points.add(new Vec3d(x, y, z));
        return this;
    }

    /**
     * For every four points, create a new {@link Quad} and add it to quads.
     */
    @Override
    public void endVertex() {
        if (points.size() >= 4) {
            quads.add(new Quad(points));
            points.clear();
        }
    }

    @Override
    public @NonNull List<@NonNull Quad> getQuads() {
        return this.quads;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuadConsumer quadConsumer) {
            return quadConsumer.getQuads().equals(getQuads());
        }
        return false;
    }
}
