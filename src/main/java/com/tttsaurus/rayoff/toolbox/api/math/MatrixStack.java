package com.tttsaurus.rayoff.toolbox.api.math;

import com.google.common.base.Preconditions;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Deque;

public final class MatrixStack {
    private final Deque<Matrix4f> stack = new ArrayDeque<>();

    public MatrixStack() {
        stack.push(new Matrix4f().identity());
    }

    public void push() {
        stack.push(new Matrix4f(stack.peek()));
    }

    public void pop() {
        stack.pop();
    }

    public void scale(float x, float y, float z) {
        Preconditions.checkNotNull(stack.peek());

        stack.peek().scale(x, y, z);
    }

    public void translate(float x, float y, float z) {
        Preconditions.checkNotNull(stack.peek());

        stack.peek().translate(x, y, z);
    }

    public Matrix4f current() {
        return stack.peek();
    }
}
