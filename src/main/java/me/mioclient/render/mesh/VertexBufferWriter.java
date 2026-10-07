package me.mioclient.render.mesh;

import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.system.MemoryStack;

public class VertexBufferWriter extends TrajectoriesVertexConsumer implements net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter {
    public final boolean flag;

    public VertexBufferWriter(int i) {
        super(i);
        this.flag = this.bufferBuilder instanceof net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
    }

    @Override
    public void push(MemoryStack memoryStack, long j, int i, VertexFormat vertexFormatDescription) {
        if (this.flag && this.bufferBuilder != null) {
            ((net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter) (Object) this.bufferBuilder).push(memoryStack, j, i, vertexFormatDescription);
        }
    }

    @Override
    public boolean canUseIntrinsics() {
        return this.flag;
    }
}
