package me.mioclient.render.mesh;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

public class TrajectoriesVertexConsumer implements VertexConsumer {
    public static final boolean flag;
    public final ByteBufferBuilder byteBufferBuilder;
    public BufferBuilder bufferBuilder;
    public PrimitiveTopology primitiveTopology;
    public VertexFormat vertexFormat;

    public static TrajectoriesVertexConsumer createDefault() {
        return createWithCapacity(786432);
    }

    public static TrajectoriesVertexConsumer createWithCapacity(int i) {
        return flag ? VertexConsumerFactory.createSodiumConsumer(i) : new TrajectoriesVertexConsumer(i);
    }

    public TrajectoriesVertexConsumer(int i) {
        this.byteBufferBuilder = new ByteBufferBuilder(i);
    }

    public TrajectoriesVertexConsumer setDefaultFormat(PrimitiveTopology topology, VertexFormat format) {
        this.primitiveTopology = topology;
        this.vertexFormat = format;
        return this;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return getOrCreateBuffer().addVertex(x, y, z);
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        return getOrCreateBuffer().setColor(r, g, b, a);
    }

    @Override
    public VertexConsumer setColor(int color) {
        return getOrCreateBuffer().setColor(color);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return getOrCreateBuffer().setUv(u, v);
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return getOrCreateBuffer().setUv1(u, v);
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return getOrCreateBuffer().setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        return getOrCreateBuffer().setNormal(x, y, z);
    }

    @Override
    public VertexConsumer setLineWidth(float width) {
        return getOrCreateBuffer().setLineWidth(width);
    }

    public boolean isBuilding() {
        return this.bufferBuilder != null;
    }

    public BufferBuilder beginBuffer(PrimitiveTopology topology, VertexFormat format) {
        this.primitiveTopology = topology;
        this.vertexFormat = format;
        this.bufferBuilder = new BufferBuilder(this.byteBufferBuilder, topology, format);
        return this.bufferBuilder;
    }

    public BufferBuilder getOrCreateBuffer() {
        if (this.bufferBuilder == null) {
            if (this.primitiveTopology == null || this.vertexFormat == null) {
                throw new IllegalArgumentException("Tried to invoke empty not caching buffer");
            }
            this.bufferBuilder = new BufferBuilder(this.byteBufferBuilder, this.primitiveTopology, this.vertexFormat);
        }
        return this.bufferBuilder;
    }

    public MeshData endBuffer() {
        if (this.bufferBuilder == null) {
            throw new IllegalArgumentException("Tried to end empty buffer");
        }
        MeshData built = this.bufferBuilder.build();
        this.bufferBuilder = null;
        return built;
    }

    public void drawBuffer() {
        if (this.bufferBuilder == null) {
            return;
        }
        MeshData built = this.bufferBuilder.build();
        this.bufferBuilder = null;
        if (built != null) {
            built.close();
        }
    }

    public static void flush(TrajectoriesVertexConsumer consumer) {
        if (consumer.isBuilding()) {
            MeshData built = consumer.bufferBuilder.build();
            consumer.bufferBuilder = null;
            if (built != null) {
                built.close();
            }
        }
    }

    public static void endAndDraw(BufferBuilder builder) {
        if (builder == null) {
            return;
        }
        MeshData built = builder.build();
        if (built == null) {
            return;
        }
        built.close();
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
