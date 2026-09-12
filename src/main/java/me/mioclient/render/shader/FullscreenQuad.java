package me.mioclient.render.shader;

import me.mioclient.render.mesh.MeshBuilder;

import net.minecraft.client.util.math.MatrixStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FullscreenQuad.class */
public class FullscreenQuad {
    public static MeshBuilder byteBufferSearchHelper4;
    public static final MatrixStack matrixStack = new MatrixStack();

    public static void init() {
        byteBufferSearchHelper4 = new MeshBuilder(MeshBuilder.Mode_2.Triangles, MeshBuilder.Mode.Vec2);
        byteBufferSearchHelper4.beginBuild();
        byteBufferSearchHelper4.submitQuad(byteBufferSearchHelper4.writeScreenPosition(Double.longBitsToDouble(-4616189618054758400L), Double.longBitsToDouble(-4616189618054758400L)).nextVertexIndex(), byteBufferSearchHelper4.writeScreenPosition(Double.longBitsToDouble(-4616189618054758400L), Double.longBitsToDouble(4607182418800017408L)).nextVertexIndex(), byteBufferSearchHelper4.writeScreenPosition(Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L)).nextVertexIndex(), byteBufferSearchHelper4.writeScreenPosition(Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(-4616189618054758400L)).nextVertexIndex());
        byteBufferSearchHelper4.endBuild();
    }

    public static void beginOutlinePass() {
        byteBufferSearchHelper4.beginDraw(matrixStack);
    }

    public static void drawBuffer() {
        byteBufferSearchHelper4.drawMesh(matrixStack);
    }

    public static void do866() {
        byteBufferSearchHelper4.do866();
    }
}
