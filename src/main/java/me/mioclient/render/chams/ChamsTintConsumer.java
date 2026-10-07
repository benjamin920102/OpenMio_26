package me.mioclient.render.chams;

import com.mojang.blaze3d.vertex.VertexConsumer;

/* Textured chams tint: wraps the live model vertex consumer and forces every
   vertex color to the chams fill color. UVs, light and overlay pass through, so
   the model keeps its texture and shading, just tinted. Alpha comes from the
   fill color itself. */
public final class ChamsTintConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final int tint;

    public ChamsTintConsumer(VertexConsumer delegate, int tint) {
        this.delegate = delegate;
        this.tint = tint;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        this.delegate.setColor((this.tint >> 16) & 255, (this.tint >> 8) & 255, this.tint & 255, (this.tint >>> 24));
        return this;
    }

    @Override
    public VertexConsumer setColor(int argb) {
        this.delegate.setColor(this.tint);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        this.delegate.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        this.delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        this.delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        this.delegate.setNormal(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float width) {
        this.delegate.setLineWidth(width);
        return this;
    }
}
