package me.mioclient.render.shape;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.mesh.TrajectoriesVertexConsumer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class Shape2DRenderer {
    public static final TrajectoriesVertexConsumer trajectoriesVertexConsumer = TrajectoriesVertexConsumer.createDefault();
    /* GUI/HUD phase extractor. Set by the screen/HUD drivers every frame. During the
       world phase there is no extractor: world plates go out as vanilla gizmos. */
    private static net.minecraft.client.gui.GuiGraphicsExtractor extractor;

    public static void setPendingExtractor(net.minecraft.client.gui.GuiGraphicsExtractor drawContext) {
        extractor = drawContext;
    }

    public static void drawHLine(PoseStack matrixStack, float f, float f2, float f3, float f4, java.awt.Color color) {
        drawRect(matrixStack, f, f3, f2 + f4, f3 + f4, color);
    }

    public static void drawVLine(PoseStack matrixStack, float f, float f2, float f3, float f4, java.awt.Color color) {
        drawRect(matrixStack, f, f2 + f4, f + f4, f3, color);
    }

    public static void drawOutline(PoseStack matrixStack, float f, float f2, float f3, float f4, java.awt.Color color) {
        drawHLine(matrixStack, f, f3, f2, 1.0f, color);
        drawVLine(matrixStack, f3, f2, f4, 1.0f, color);
        drawHLine(matrixStack, f, f3, f4, 1.0f, color);
        drawVLine(matrixStack, f, f2, f4, 1.0f, color);
    }

    public static void drawOutline(PoseStack matrixStack, float f, float f2, float f3, float f4, float f5, java.awt.Color color) {
        drawHLine(matrixStack, f, f3, f2, f5, color);
        drawVLine(matrixStack, f3, f2, f4, f5, color);
        drawHLine(matrixStack, f, f3, f4, f5, color);
        drawVLine(matrixStack, f, f2, f4, f5, color);
    }

    public static void drawRect(PoseStack matrixStack, float f, float f2, float f3, float f4, java.awt.Color color) {
        drawRect(matrixStack, f, f2, f3, f4, color.hashCode());
    }

    public static void drawRect(PoseStack matrixStack, float f, float f2, float f3, float f4, int i) {
        float f5 = ((i >> 24) & 255) / 255.0f;
        float f6 = ((i >> 16) & 255) / 255.0f;
        float f7 = ((i >> 8) & 255) / 255.0f;
        float f8 = (i & 255) / 255.0f;
        if (f5 == 0.0f) {
            return;
        }
        if (me.mioclient.render.GizmoRenderer.isWorldPhase() && matrixStack != null) {
            /* 26.2 backend: billboard/world plates go out as vanilla gizmo rects. The
               corners are transformed to world space by the stack (stacks handed down
               here are camera-relative). */
            try {
                net.minecraft.world.phys.Vec3 cam = Minecraft.getInstance().gameRenderer.mainCamera().position();
                PoseStack.Pose worldPose = matrixStack.last();
                org.joml.Vector3f v1 = new org.joml.Vector3f(f, f4, 0.0f);
                org.joml.Vector3f v2 = new org.joml.Vector3f(f3, f4, 0.0f);
                org.joml.Vector3f v3 = new org.joml.Vector3f(f3, f2, 0.0f);
                org.joml.Vector3f v4 = new org.joml.Vector3f(f, f2, 0.0f);
                worldPose.pose().transformPosition(v1);
                worldPose.pose().transformPosition(v2);
                worldPose.pose().transformPosition(v3);
                worldPose.pose().transformPosition(v4);
                me.mioclient.render.GizmoRenderer.quad(
                        new net.minecraft.world.phys.Vec3(v1.x + cam.x, v1.y + cam.y, v1.z + cam.z),
                        new net.minecraft.world.phys.Vec3(v2.x + cam.x, v2.y + cam.y, v2.z + cam.z),
                        new net.minecraft.world.phys.Vec3(v3.x + cam.x, v3.y + cam.y, v3.z + cam.z),
                        new net.minecraft.world.phys.Vec3(v4.x + cam.x, v4.y + cam.y, v4.z + cam.z), i);
                return;
            } catch (Exception ignored) {
            }
        }
        if (extractor != null) {
            try {
                extractor.fill(Math.round(f), Math.round(f2), Math.round(f3), Math.round(f4), i);
                return;
            } catch (Exception ignored) {
            }
        }
        PoseStack.Pose pose = matrixStack.last();
        trajectoriesVertexConsumer.addVertex(pose, f, f4, 0.0f).setColor(f6, f7, f8, f5);
        trajectoriesVertexConsumer.addVertex(pose, f3, f4, 0.0f).setColor(f6, f7, f8, f5);
        trajectoriesVertexConsumer.addVertex(pose, f3, f2, 0.0f).setColor(f6, f7, f8, f5);
        trajectoriesVertexConsumer.addVertex(pose, f, f2, 0.0f).setColor(f6, f7, f8, f5);
    }

    public static void drawGradientRect(Matrix4f matrix4f, float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
        if (extractor != null) {
            try {
                extractor.fillGradient(Math.round(f), Math.round(f2), Math.round(f + f3), Math.round(f2 + f4), i, i3);
                return;
            } catch (Exception ignored) {
            }
        }
        float[] fArr = {((i >> 16) & 255) / 255.0f, ((i >> 8) & 255) / 255.0f, (i & 255) / 255.0f};
        float[] fArr2 = {((i2 >> 16) & 255) / 255.0f, ((i2 >> 8) & 255) / 255.0f, (i2 & 255) / 255.0f};
        float[] fArr3 = {((i3 >> 16) & 255) / 255.0f, ((i3 >> 8) & 255) / 255.0f, (i3 & 255) / 255.0f};
        float[] fArr4 = {((i4 >> 16) & 255) / 255.0f, ((i4 >> 8) & 255) / 255.0f, (i4 & 255) / 255.0f};
        trajectoriesVertexConsumer.addVertex(f, f2 + f4, 0.0f).setColor(fArr4[0], fArr4[1], fArr4[2], 1.0f);
        trajectoriesVertexConsumer.addVertex(f + f3, f2 + f4, 0.0f).setColor(fArr3[0], fArr3[1], fArr3[2], 1.0f);
        trajectoriesVertexConsumer.addVertex(f + f3, f2, 0.0f).setColor(fArr2[0], fArr2[1], fArr2[2], 1.0f);
        trajectoriesVertexConsumer.addVertex(f, f2, 0.0f).setColor(fArr[0], fArr[1], fArr[2], 1.0f);
    }

    public static void drawMultiGradientRect(Matrix4f matrix4f, float f, float f2, float f3, float f4, int... iArr) {
        if (iArr.length < 2) {
            return;
        }
        if (extractor != null) {
            try {
                float length = f3 / iArr.length;
                for (int i2 = 1; i2 < iArr.length; i2++) {
                    float f5 = i2 == iArr.length - 1 ? f3 : length * i2;
                    extractor.fill(Math.round(f + length * (i2 - 1)), Math.round(f2), Math.round(f + f5), Math.round(f2 + f4), iArr[i2 - 1]);
                }
                return;
            } catch (Exception ignored) {
            }
        }
        float[][] fArr = new float[iArr.length][4];
        for (int i = 0; i < iArr.length; i++) {
            float[] fArr2 = new float[4];
            fArr2[0] = ((iArr[i] >> 24) & 255) / 255.0f;
            fArr2[1] = ((iArr[i] >> 16) & 255) / 255.0f;
            fArr2[2] = ((iArr[i] >> 8) & 255) / 255.0f;
            fArr2[3] = (iArr[i] & 255) / 255.0f;
            fArr[i] = fArr2;
        }
        float length = f3 / iArr.length;
        int i2 = 1;
        while (i2 < iArr.length) {
            float f5 = i2 == iArr.length - 1 ? f3 : length * i2;
            trajectoriesVertexConsumer.addVertex(f + f5, f2, 0.0f).setColor(fArr[i2][1], fArr[i2][2], fArr[i2][3], fArr[i2][0]);
            trajectoriesVertexConsumer.addVertex(f + (length * (i2 - 1)), f2, 0.0f).setColor(fArr[i2 - 1][1], fArr[i2 - 1][2], fArr[i2 - 1][3], fArr[i2 - 1][0]);
            trajectoriesVertexConsumer.addVertex(f + (length * (i2 - 1)), f2 + f4, 0.0f).setColor(fArr[i2 - 1][1], fArr[i2 - 1][2], fArr[i2 - 1][3], fArr[i2 - 1][0]);
            trajectoriesVertexConsumer.addVertex(f + f5, f2 + f4, 0.0f).setColor(fArr[i2][1], fArr[i2][2], fArr[i2][3], fArr[i2][0]);
            i2++;
        }
    }

    public static void drawString(PoseStack matrixStack, String str, int i, int i2, int i3, boolean z) {
        Minecraft.getInstance().font.prepareText(str, i, i2, i3, z, 0);
    }

    public static void drawOrderedText(PoseStack matrixStack, FormattedCharSequence orderedText, int i, int i2, int i3, boolean z) {
        Minecraft.getInstance().font.prepareText(orderedText, i, i2, i3, z, false, 0);
    }

    public static void resetShadowOffset() {
        flush(true);
    }

    public static void flush(boolean z) {
        TrajectoriesVertexConsumer.flush(trajectoriesVertexConsumer);
        if (z) {
            ClientTextRenderer.INSTANCE.resetShadowOffset();
        }
    }

    static {
        trajectoriesVertexConsumer.setDefaultFormat(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }
}
