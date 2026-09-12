package me.mioclient.render.shape;

import me.mioclient.render.RenderUtil;

import me.mioclient.render.mesh.TrajectoriesVertexConsumer;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.event.EventBus;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Comparator;
import java.util.List;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.event.Listen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32C;
import me.mioclient.render.mesh.TrajectoriesVertexConsumer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BoxRenderer.class */
public final class BoxRenderer implements ClientAccessor {
    public static final TrajectoriesVertexConsumer trajectoriesVertexConsumer = TrajectoriesVertexConsumer.createDefault();
    public static final TrajectoriesVertexConsumer trajectoriesVertexConsumer2 = TrajectoriesVertexConsumer.createDefault();
    public static final List<Record> list = new ObjectArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/PhaseESPSearchHelper4$Record.class */
    public static final class Record {
        public final Matrix4f matrix4f;
        public final Matrix3f matrix3f;
        public final Box box;
        public final Color color;
        public final float val;

        public Record(Matrix4f matrix4f, Matrix3f matrix3f, Box box, Color color, float f) {
            this.matrix4f = matrix4f;
            this.matrix3f = matrix3f;
            this.box = box;
            this.color = color;
            this.val = f;
        }

        public static Record create(MatrixStack matrixStack, Box box, Color color, float f) {
            return new Record(new Matrix4f(matrixStack.peek().getPositionMatrix()), new Matrix3f(matrixStack.peek().getNormalMatrix()), box, color, f);
        }




        public Matrix4f getPositionMatrix() {
            return this.matrix4f;
        }

        public Matrix3f getNormalMatrix() {
            return this.matrix3f;
        }

        public Box getBox() {
            return this.box;
        }

        public Color getColor() {
            return this.color;
        }

        public float getLineWidth() {
            return this.val;
        }
    }

    @Listen(get219= -9999)
    public static void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        resetShadowOffset();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(1.0f, -1500000.0f);
        GL20.glDepthRange(0.0d, 0.1d);
        Shape2DRenderer.resetShadowOffset();
        GL20.glDepthRange(0.0d, 1.0d);
        RenderSystem.polygonOffset(1.0f, 1500000.0f);
        RenderSystem.disablePolygonOffset();
    }

    public static void drawFilledBox(MatrixStack matrixStack, BlockPos blockPos, Color color) {
        drawFilledBox(matrixStack, new Box(blockPos), color);
    }

    public static void drawFilledBox(MatrixStack matrixStack, Box box, Color color) {
        if (color.getAlpha() == 0) {
            return;
        }
        float x = (float) (box.minX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (box.minY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (box.minZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (box.maxX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (box.maxY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (box.maxZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        int hashCode = applyShaderColor(color).hashCode();
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z).color(hashCode);
    }

    public static void drawFadingBox(MatrixStack matrixStack, Box box, Color color) {
        if (color.getAlpha() == 0) {
            return;
        }
        float x = (float) (box.minX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (box.minY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (box.minZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (box.maxX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (box.maxY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (box.maxZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        int hashCode = color.hashCode();
        int i = ColorUtils.toIntWithAlpha(color, 0);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z).color(i);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z).color(i);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z).color(i);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z2).color(i);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x2, y2, z2).color(i);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z2).color(i);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z2).color(i);
        trajectoriesVertexConsumer.vertex(matrixStack.peek().getPositionMatrix(), x, y2, z).color(i);
    }

    public static void drawBoxOutline(MatrixStack matrixStack, BlockPos blockPos, Color color, float f) {
        drawBoxOutline(matrixStack, new Box(blockPos), color, f);
    }

    public static void drawBoxOutline(MatrixStack matrixStack, Box box, Color color, float f) {
        if (color.getAlpha() != 0 && f > 0.0f) {
            list.add(Record.create(matrixStack, box, applyShaderColor(color), f));
        }
    }

    public static void renderOutlineRecord(Record record) {
        Matrix4f matrix4f = record.matrix4f;
        Matrix3f matrix3f = record.matrix3f;
        float x = (float) (record.box.minX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (record.box.minY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (record.box.minZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (record.box.maxX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (record.box.maxY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (record.box.maxZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        int hashCode = record.color.hashCode();
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y2, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y2, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y2, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y2, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y2, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y2, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y2, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x, y2, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y2, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y2, z2).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y2, z).color(hashCode);
        trajectoriesVertexConsumer2.vertex(matrix4f, x2, y2, z2).color(hashCode);
    }

    public static Color applyShaderColor(Color color) {
        return applyShaderColor(color, RenderSystem.getShaderColor());
    }

    public static Color applyShaderColor(Color color, float[] fArr) {
        return new Color((int) (color.getRed() * fArr[0]), (int) (color.getGreen() * fArr[1]), (int) (color.getBlue() * fArr[2]), (int) (color.getAlpha() * fArr[3]));
    }

    public static void resetShadowOffset() {
        RenderUtil.INSTANCE.setupRenderState();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        trajectoriesVertexConsumer.drawBuffer();
        list.sort(Comparator.comparing(record -> {
            return Float.valueOf(record.val);
        }));
        float f = 0.0f;
        for (Record record2 : list) {
            if (record2.val != f) {
                flushLines();
            }
            f = record2.val;
            setLineWidth(f);
            renderOutlineRecord(record2);
        }
        list.clear();
        flushLines();
        RenderUtil.INSTANCE.restoreRenderState();
    }

    public static void setLineWidth(float f) {
        GL32C.glLineWidth(f);
    }

    public static void flushLines() {
        trajectoriesVertexConsumer2.drawBuffer();
        GL32C.glLineWidth(1.0f);
    }

    static {
        eventBus.registerStatic(BoxRenderer.class);
        trajectoriesVertexConsumer.setDefaultFormat(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        trajectoriesVertexConsumer2.setDefaultFormat(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
    }
}
