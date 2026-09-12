package me.mioclient.render.mesh;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.BufferAllocator;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/TrajectoriesVertexConsumer.class */
public class TrajectoriesVertexConsumer implements VertexConsumer {
    public static final boolean flag;
    public final BufferAllocator bufferAllocator;
    public BufferBuilder bufferBuilder;
    public VertexFormat.DrawMode drawMode;
    public VertexFormat vertexFormat;

    public static TrajectoriesVertexConsumer createDefault() {
        return createWithCapacity(786432);
    }

    public static TrajectoriesVertexConsumer createWithCapacity(int i) {
        return flag ? VertexConsumerFactory.createSodiumConsumer(i) : new TrajectoriesVertexConsumer(i);
    }

    public TrajectoriesVertexConsumer(int i) {
        this.bufferAllocator = new BufferAllocator(i);
    }

    public TrajectoriesVertexConsumer setDefaultFormat(VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
        this.drawMode = drawMode;
        this.vertexFormat = vertexFormat;
        return this;
    }

    public VertexConsumer vertex(float f, float f2, float f3) {
        return getOrCreateBuffer().vertex(f, f2, f3);
    }

    public VertexConsumer color(int i, int i2, int i3, int i4) {
        return getOrCreateBuffer().color(i, i2, i3, i4);
    }

    public VertexConsumer texture(float f, float f2) {
        return getOrCreateBuffer().texture(f, f2);
    }

    public VertexConsumer overlay(int i, int i2) {
        return getOrCreateBuffer().overlay(i, i2);
    }

    public VertexConsumer light(int i, int i2) {
        return getOrCreateBuffer().light(i, i2);
    }

    public VertexConsumer normal(float f, float f2, float f3) {
        return getOrCreateBuffer().normal(f, f2, f3);
    }

    public boolean isBuilding() {
        return this.bufferBuilder != null;
    }

    public BufferBuilder beginBuffer(VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
        this.bufferBuilder = new BufferBuilder(this.bufferAllocator, drawMode, vertexFormat);
        return this.bufferBuilder;
    }

    public BufferBuilder getOrCreateBuffer() {
        if (this.bufferBuilder == null) {
            if (this.drawMode == null || this.vertexFormat == null) {
                throw new IllegalArgumentException("Tried to invoke empty not caching buffer");
            }
            this.bufferBuilder = new BufferBuilder(this.bufferAllocator, this.drawMode, this.vertexFormat);
        }
        return this.bufferBuilder;
    }

    public BuiltBuffer endBuffer() {
        if (this.bufferBuilder == null) {
            throw new IllegalArgumentException("Tried to end empty buffer");
        }
        BuiltBuffer endNullable = this.bufferBuilder.endNullable();
        this.bufferBuilder = null;
        return endNullable;
    }

    public void drawBuffer() {
        if (this.bufferBuilder == null) {
            return;
        }
        endAndDraw(this.bufferBuilder);
        this.bufferBuilder = null;
    }

    public static void flush(TrajectoriesVertexConsumer trajectoriesVertexConsumer) {
        if (trajectoriesVertexConsumer.isBuilding()) {
            endAndDraw(trajectoriesVertexConsumer.bufferBuilder);
            trajectoriesVertexConsumer.bufferBuilder = null;
        }
    }

    public static void endAndDraw(BufferBuilder bufferBuilder) {
        BuiltBuffer endNullable;
        if (bufferBuilder == null || (endNullable = bufferBuilder.endNullable()) == null) {
            return;
        }
        BufferRenderer.drawWithGlobalProgram(endNullable);
    }

    static {
        boolean z;
        try {
            Class.forName("net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter");
            z = true;
        } catch (ClassNotFoundException e) {
            z = false;
        }
        flag = z;
    }
}
