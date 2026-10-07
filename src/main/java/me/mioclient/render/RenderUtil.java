package me.mioclient.render;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shader.Framebuffer;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;

import java.awt.Color;
import me.mioclient.module.client.UI;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderUtil implements ClientAccessor {
    public static final RenderUtil INSTANCE = new RenderUtil();
    public static float partialTick;
    public static PoseStack matrixStack;

    public static float getPartialTick() {
        return partialTick;
    }

    public static void setPartialTick(float f) {
        partialTick = f;
    }

    public static PoseStack getMatrixStack() {
        return matrixStack;
    }

    public static void setMatrixStack(PoseStack matrixStack2) {
        matrixStack = matrixStack2;
    }

    public void drawRectOutline(PoseStack matrixStack2, float f, float f2, float f3, float f4, Color color) {
        if (f3 < f) {
            float tmp = f;
            f = f3;
            f3 = tmp;
        }
        if (f4 < f2) {
            float tmp = f2;
            f2 = f4;
            f4 = tmp;
        }
        drawHorizontalLine(matrixStack2, f, f3, f2, color);
        drawVerticalLine(matrixStack2, f3, f2, f4, color);
        drawHorizontalLine(matrixStack2, f, f3, f4, color);
        drawVerticalLine(matrixStack2, f, f2, f4, color);
    }

    public void drawRectOutlineThick(PoseStack matrixStack2, float f, float f2, float f3, float f4, Color color, float f5) {
        if (f3 < f) {
            float tmp = f;
            f = f3;
            f3 = tmp;
        }
        if (f4 < f2) {
            float tmp = f2;
            f2 = f4;
            f4 = tmp;
        }
        drawHorizontalLineThick(matrixStack2, f, f3, f2, color, f5);
        drawVerticalLineThick(matrixStack2, f3, f2, f4, color, f5);
        drawHorizontalLineThick(matrixStack2, f, f3, f4, color, f5);
        drawVerticalLineThick(matrixStack2, f, f2, f4, color, f5);
    }

    public void drawHorizontalLine(PoseStack matrixStack2, float f, float f2, float f3, Color color) {
        if (f2 < f) {
            float tmp = f;
            f = f2;
            f2 = tmp;
        }
        drawFilledRectColor(matrixStack2, f, f3, f2 + 1.0f, f3 + 1.0f, color);
    }

    public void drawVerticalLine(PoseStack matrixStack2, float f, float f2, float f3, Color color) {
        if (f3 < f2) {
            float tmp = f2;
            f2 = f3;
            f3 = tmp;
        }
        drawFilledRectColor(matrixStack2, f, f2 + 1.0f, f + 1.0f, f3, color);
    }

    public void drawHorizontalLineThick(PoseStack matrixStack2, float f, float f2, float f3, Color color, float f4) {
        if (f2 < f) {
            float tmp = f;
            f = f2;
            f2 = tmp;
        }
        drawFilledRectColor(matrixStack2, f, f3, f2 + f4, f3 + f4, color);
    }

    public void drawVerticalLineThick(PoseStack matrixStack2, float f, float f2, float f3, Color color, float f4) {
        if (f3 < f2) {
            float tmp = f2;
            f2 = f3;
            f3 = tmp;
        }
        drawFilledRectColor(matrixStack2, f, f2 + f4, f + f4, f3, color);
    }

    public void drawFilledRect(PoseStack matrixStack2, float f, float f2, float f3, float f4, int i) {
        if (f3 < f) {
            float tmp = f;
            f = f3;
            f3 = tmp;
        }
        if (f4 < f2) {
            float tmp = f2;
            f2 = f4;
            f4 = tmp;
        }
        Shape2DRenderer.drawRect(matrixStack2, f, f2, f3, f4, i);
    }

    public void drawFilledRectColor(PoseStack matrixStack2, float f, float f2, float f3, float f4, Color color) {
        drawFilledRect(matrixStack2, f, f2, f3, f4, color.hashCode());
    }

    public void drawGradientRect(PoseStack matrixStack2, int i, int i2, int i3, int i4, Color color, Color color2) {
        Shape2DRenderer.drawGradientRect(matrixStack2.last().pose(), i, i2, i3, i4, color.hashCode(), color.hashCode(), color2.hashCode(), color2.hashCode());
    }

    public void drawGradientQuad(Matrix4f matrix4f, VertexConsumer vertexConsumer, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        float alpha = ((i6 >> 24) & 255) / 255.0f;
        float red = ((i6 >> 16) & 255) / 255.0f;
        float green = ((i6 >> 8) & 255) / 255.0f;
        float blue = (i6 & 255) / 255.0f;
        float alpha2 = ((i7 >> 24) & 255) / 255.0f;
        float red2 = ((i7 >> 16) & 255) / 255.0f;
        float green2 = ((i7 >> 8) & 255) / 255.0f;
        float blue2 = (i7 & 255) / 255.0f;
        vertexConsumer.addVertex(i, i2, i5).setColor(red, green, blue, alpha);
        vertexConsumer.addVertex(i, i4, i5).setColor(red2, green2, blue2, alpha2);
        vertexConsumer.addVertex(i3, i4, i5).setColor(red2, green2, blue2, alpha2);
        vertexConsumer.addVertex(i3, i2, i5).setColor(red, green, blue, alpha);
    }

    public int getTextWidth(String str) {
        return minecraftClient.font.width(str);
    }

    public int getTextWidthText(Component text) {
        return minecraftClient.font.width(text);
    }

    public double getScaledWidth() {
        return minecraftClient.getWindow().getGuiScaledWidth();
    }

    public double getScaledHeight() {
        return minecraftClient.getWindow().getGuiScaledHeight();
    }

    public void drawBoxFilled(PoseStack matrixStack2, AABB box, Color color) {
        BoxRenderer.drawFilledBox(matrixStack2, box, color);
    }

    public void drawBoxFilledTransparent(PoseStack matrixStack2, AABB box, Color color) {
        BoxRenderer.drawFadingBox(matrixStack2, box, color);
    }

    public void drawBoxFilledVec(PoseStack matrixStack2, Vec3 vec3d, Color color) {
        drawBoxFilled(matrixStack2, AABB.unitCubeFromLowerCorner(vec3d), color);
    }

    public void drawBoxFilledBlock(PoseStack matrixStack2, BlockPos blockPos, Color color) {
        drawBoxFilled(matrixStack2, new AABB(blockPos), color);
    }

    public void drawBoxOutline(PoseStack matrixStack2, AABB box, Color color, double d) {
        BoxRenderer.drawBoxOutline(matrixStack2, box, color, (float) d);
    }

    public void drawBoxOutlineVec(PoseStack matrixStack2, Vec3 vec3d, Color color, double d) {
        drawBoxOutline(matrixStack2, AABB.unitCubeFromLowerCorner(vec3d), color, d);
    }

    public void drawBoxOutlineBlock(PoseStack matrixStack2, BlockPos blockPos, Color color, double d) {
        drawBoxOutline(matrixStack2, new AABB(blockPos), color, d);
    }

    public void drawThickLine(PoseStack matrixStack2, Vec3 vec3d, Vec3 vec3d2, Color color, float f) {
        GizmoRenderer.line(vec3d, vec3d2, color.hashCode(), f);
    }

    public void drawDebugLine(PoseStack matrixStack2, Vec3 vec3d, Vec3 vec3d2, Color color) {
        BoxRenderer.drawLine(matrixStack2, vec3d, vec3d2, color.hashCode(), color.hashCode());
    }

    public void enableBlend() {
    }

    public void setupRenderState() {
    }

    public void disableBlend() {
    }

    public void restoreRenderState() {
    }

    public void runWithMSAA(java.lang.Runnable runnable) {
        if (UI.uI.msaa.getValue() == UI.Mode.NONE) {
            runnable.run();
        } else {
            int i = UI.uI.msaa.getValue().getMsaaMultiplier();
            Framebuffer.renderTempAndBlit((i & (i - 1)) == 0 ? i : 2, runnable);
        }
    }

    public void drawBillboard(PoseStack matrixStack2, Vec3 vec3d, float f, float f2, float f3, float f4, double d, Color color) {
        matrixStack2.pushPose();
        translateToPos(matrixStack2, vec3d);
        matrixStack2.translate(f, f2, 0.0f);
        matrixStack2.mulPose(minecraftClient.gameRenderer.mainCamera().rotation());
        matrixStack2.scale(0.025f * ((float) d), (-0.025f) * ((float) d), (float) (0.02500000037252903d * d));
        int i = (int) (f3 / 2.0f);
        drawFilledRectColor(matrixStack2, -i, 0.0f, (int) (f3 - i), (int) f4, color);
        matrixStack2.popPose();
    }

    public void drawTextureBillboard(GuiGraphicsExtractor drawContext, Vec3 vec3d, float f, float f2, double d, Color color, Identifier identifier) {
        if (drawContext == null) {
            return;
        }
        drawContext.pose().pushMatrix();
        drawContext.pose().translate((float) (vec3d.x - minecraftClient.gameRenderer.mainCamera().position().x), (float) (vec3d.y - minecraftClient.gameRenderer.mainCamera().position().y));
        drawContext.pose().scale(0.025f * ((float) d), (-0.025f) * ((float) d));
        int i = (int) (f / 2.0f);
        drawContext.blit(RenderPipelines.GUI_TEXTURED, identifier, -i, -i, 0.0f, 0.0f, (int) f, (int) f2, (int) f, (int) f2);
        drawContext.pose().popMatrix();
    }

    public void drawBillboardFacing(PoseStack matrixStack2, Vec3 vec3d, float f, float f2, float f3, float f4, double d, Color color) {
        matrixStack2.pushPose();
        translateToPos(matrixStack2, vec3d);
        Camera camera = minecraftClient.gameRenderer.mainCamera();
        matrixStack2.mulPose(Axis.YP.rotationDegrees(-camera.yRot()));
        matrixStack2.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
        matrixStack2.translate(f, f2, 0.0f);
        matrixStack2.scale(0.025f * ((float) d), (-0.025f) * ((float) d), 1.0f);
        int i = (int) (f3 / 2.0f);
        drawRectOutline(matrixStack2, -i, 0.0f, (int) (f3 - i), (int) f4, color);
        matrixStack2.popPose();
    }

    public void drawTextBillboard(GuiGraphicsExtractor drawContext, String str, Vec3 vec3d, float f, float f2, double d, Color color, boolean z) {
        drawTextBillboardOffset(drawContext, str, vec3d, f, f2, (-ClientTextRenderer.INSTANCE.getStringWidth(str)) / 2.0f, 0.0f, d, color, z);
    }

    public void drawTextBillboardOffset(GuiGraphicsExtractor drawContext, String str, Vec3 vec3d, float f, float f2, float f3, float f4, double d, Color color, boolean z) {
        drawContext.pose().pushMatrix();
        drawContext.pose().translate((float) (vec3d.x - minecraftClient.gameRenderer.mainCamera().position().x + f), (float) (vec3d.y - minecraftClient.gameRenderer.mainCamera().position().y + f2));
        drawContext.pose().scale(0.025f * ((float) d), (-0.025f) * ((float) d));
        Font font = minecraftClient.font;
        if (z) {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str, f3, f4, color);
        } else {
            ClientTextRenderer.INSTANCE.drawText(drawContext, str, f3, f4, color);
        }
        drawContext.pose().popMatrix();
    }

    public void addLineVertex(PoseStack matrixStack2, VertexConsumer bufferBuilder, Vec3 vec3d, Vec3 vec3d2, int i, int i2) {
        BoxRenderer.drawLine(matrixStack2, vec3d, vec3d2, i, i2);
    }

    public void addFloorQuad(PoseStack matrixStack2, VertexConsumer bufferBuilder, AABB box, int i) {
        Vec3 cam = minecraftClient.gameRenderer.mainCamera().position();
        float x = (float) (box.minX - cam.x);
        float y = (float) (box.minY - cam.y);
        float z = (float) (box.minZ - cam.z);
        float x2 = (float) (box.maxX - cam.x);
        float z2 = (float) (box.maxZ - cam.z);
        PoseStack.Pose pose = matrixStack2.last();
        bufferBuilder.addVertex(pose, x, y, z).setColor(i);
        bufferBuilder.addVertex(pose, x2, y, z).setColor(i);
        bufferBuilder.addVertex(pose, x2, y, z2).setColor(i);
        bufferBuilder.addVertex(pose, x, y, z2).setColor(i);
        bufferBuilder.addVertex(pose, x, y, z).setColor(i);
    }

    public void renderEntity(Entity entity, float f, PoseStack matrixStack2, net.minecraft.client.renderer.SubmitNodeCollector collector) {
        Vec3 pos = minecraftClient.gameRenderer.mainCamera().position();
        double x = Mth.lerp(f, (float) entity.xo, (float) entity.getX()) - pos.x;
        double y = Mth.lerp(f, (float) entity.yo, (float) entity.getY()) - pos.y;
        double z = Mth.lerp(f, (float) entity.zo, (float) entity.getZ()) - pos.z;
        matrixStack2.pushPose();
        matrixStack2.translate(x, y, z);
        matrixStack2.popPose();
    }

    public void drawGradientQuadMatrix(Matrix4f matrix4f, float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
        Shape2DRenderer.drawGradientRect(matrix4f, f, f2, f3, f4, i, i2, i3, i4);
    }

    public void drawGradientBar(Matrix4f matrix4f, float f, float f2, float f3, float f4, int... iArr) {
        Shape2DRenderer.drawMultiGradientRect(matrix4f, f, f2, f3, f4, iArr);
    }

    public static void translateToPos(PoseStack matrixStack2, Vec3 vec3d) {
        Vec3 pos = minecraftClient.gameRenderer.mainCamera().position();
        matrixStack2.translate(vec3d.x - pos.x, vec3d.y - pos.y, vec3d.z - pos.z);
    }

    public static void applyCameraRotation(PoseStack matrixStack2) {
        Camera camera = minecraftClient.gameRenderer.mainCamera();
        matrixStack2.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
        matrixStack2.mulPose(Axis.YP.rotationDegrees(camera.yRot() + 180.0f));
    }
}
