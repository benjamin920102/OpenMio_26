package me.mioclient.render.mesh;

public final class VertexConsumerFactory {
    public static TrajectoriesVertexConsumer createSodiumConsumer(int i) {
        return new VertexBufferWriter(i);
    }
}
