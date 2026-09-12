package me.mioclient.render.mesh;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Helper_21.class */
public final class VertexConsumerFactory {
    public static TrajectoriesVertexConsumer createSodiumConsumer(int i) {
        return new VertexBufferWriter(i);
    }
}
