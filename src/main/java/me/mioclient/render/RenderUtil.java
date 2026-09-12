package me.mioclient.render;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shader.Framebuffer;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.math.ColorUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.mioclient.module.client.UI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import me.mioclient.render.shader.Framebuffer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/RenderUtil.class */
public class RenderUtil implements ClientAccessor {
    public static final RenderUtil INSTANCE = new RenderUtil();
    public static float partialTick;
    public static MatrixStack matrixStack;

    public static float getPartialTick() {
        return partialTick;
    }

    public static void setPartialTick(float f) {
        partialTick = f;
    }

    public static MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public static void setMatrixStack(MatrixStack matrixStack2) {
        matrixStack = matrixStack2;
    }

    public void drawRectOutline(MatrixStack matrixStack2, float f, float f2, float f3, float f4, Color color) {
        if (f3 < f) {
            f = f3;
            f3 = f;
        }
        if (f4 < f2) {
            f2 = f4;
            f4 = f2;
        }
        drawHorizontalLine(matrixStack2, f, f3, f2, color);
        drawVerticalLine(matrixStack2, f3, f2, f4, color);
        drawHorizontalLine(matrixStack2, f, f3, f4, color);
        drawVerticalLine(matrixStack2, f, f2, f4, color);
    }

    public void drawRectOutlineThick(MatrixStack matrixStack2, float f, float f2, float f3, float f4, Color color, float f5) {
        drawHorizontalLineThick(matrixStack2, f, f3, f2, color, f5);
        drawVerticalLineThick(matrixStack2, f3, f2, f4, color, f5);
        drawHorizontalLineThick(matrixStack2, f, f3, f4, color, f5);
        drawVerticalLineThick(matrixStack2, f, f2, f4, color, f5);
    }

    public void drawHorizontalLine(MatrixStack matrixStack2, float f, float f2, float f3, Color color) {
        if (f2 < f) {
            f = f2;
            f2 = f;
        }
        drawFilledRectColor(matrixStack2, f, f3, f2 + 1.0f, f3 + 1.0f, color);
    }

    public void drawVerticalLine(MatrixStack matrixStack2, float f, float f2, float f3, Color color) {
        if (f3 < f2) {
            f2 = f3;
            f3 = f2;
        }
        drawFilledRectColor(matrixStack2, f, f2 + 1.0f, f + 1.0f, f3, color);
    }

    public void drawHorizontalLineThick(MatrixStack matrixStack2, float f, float f2, float f3, Color color, float f4) {
        if (f2 < f) {
            f = f2;
            f2 = f;
        }
        drawFilledRectColor(matrixStack2, f, f3, f2 + f4, f3 + f4, color);
    }

    public void drawVerticalLineThick(MatrixStack matrixStack2, float f, float f2, float f3, Color color, float f4) {
        if (f3 < f2) {
            f2 = f3;
            f3 = f2;
        }
        drawFilledRectColor(matrixStack2, f, f2 + f4, f + f4, f3, color);
    }

    public void drawFilledRect(MatrixStack matrixStack2, float f, float f2, float f3, float f4, int i) {
        if (f < f3) {
            f = f3;
            f3 = f;
        }
        if (f2 < f4) {
            f2 = f4;
            f4 = f2;
        }
        float f5 = ((i >> 24) & 255) / 255.0f;
        float f6 = ((i >> 16) & 255) / 255.0f;
        float f7 = ((i >> 8) & 255) / 255.0f;
        float f8 = (i & 255) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), f, f4, 0.0f).color(f6, f7, f8, f5);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), f3, f4, 0.0f).color(f6, f7, f8, f5);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), f3, f2, 0.0f).color(f6, f7, f8, f5);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), f, f2, 0.0f).color(f6, f7, f8, f5);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderSystem.disableBlend();
    }

    public void drawFilledRectColor(MatrixStack matrixStack2, float f, float f2, float f3, float f4, Color color) {
        drawFilledRect(matrixStack2, f, f2, f3, f4, color.hashCode());
    }

    public void drawGradientRect(MatrixStack matrixStack2, int i, int i2, int i3, int i4, Color color, Color color2) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator.getInstance();
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        drawGradientQuad(matrixStack2.peek().getPositionMatrix(), begin, i, i2, i3, i4, 0, color.hashCode(), color2.hashCode());
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderSystem.disableBlend();
    }

    public void drawGradientQuad(Matrix4f matrix4f, VertexConsumer vertexConsumer, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        float alpha = ColorHelper.Argb.getAlpha(i6) / 255.0f;
        float red = ColorHelper.Argb.getRed(i6) / 255.0f;
        float green = ColorHelper.Argb.getGreen(i6) / 255.0f;
        float blue = ColorHelper.Argb.getBlue(i6) / 255.0f;
        float alpha2 = ColorHelper.Argb.getAlpha(i7) / 255.0f;
        float red2 = ColorHelper.Argb.getRed(i7) / 255.0f;
        float green2 = ColorHelper.Argb.getGreen(i7) / 255.0f;
        float blue2 = ColorHelper.Argb.getBlue(i7) / 255.0f;
        vertexConsumer.vertex(matrix4f, i, i2, i5).color(red, green, blue, alpha);
        vertexConsumer.vertex(matrix4f, i, i4, i5).color(red2, green2, blue2, alpha2);
        vertexConsumer.vertex(matrix4f, i3, i4, i5).color(red2, green2, blue2, alpha2);
        vertexConsumer.vertex(matrix4f, i3, i2, i5).color(red, green, blue, alpha);
    }

    public int getTextWidth(String str) {
        return minecraftClient.textRenderer.getWidth(str);
    }

    public int getTextWidthText(Text text) {
        return minecraftClient.textRenderer.getWidth(text);
    }

    public double getScaledWidth() {
        return minecraftClient.getWindow().getScaledWidth();
    }

    public double getScaledHeight() {
        return minecraftClient.getWindow().getScaledHeight();
    }

    public void drawBoxFilled(MatrixStack matrixStack2, Box box, Color color) {
        float x = (float) (box.minX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (box.minY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (box.minZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (box.maxX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (box.maxY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (box.maxZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        setupRenderState();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z2).color(color.getRGB());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z).color(color.getRGB());
        BufferRenderer.drawWithGlobalProgram(begin.end());
        restoreRenderState();
    }

    public void drawBoxFilledTransparent(MatrixStack matrixStack2, Box box, Color color) {
        float x = (float) (box.minX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (box.minY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (box.minZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (box.maxX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (box.maxY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (box.maxZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        int hashCode = color.hashCode();
        int i = ColorUtils.toIntWithAlpha(color, 0);
        setupRenderState();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z).color(i);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z).color(i);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z).color(i);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(i);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y, z2).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(i);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z2).color(i);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z2).color(hashCode);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z2).color(i);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y2, z).color(i);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        restoreRenderState();
    }

    public void drawBoxFilledVec(MatrixStack matrixStack2, Vec3d vec3d, Color color) {
        drawBoxFilled(matrixStack2, Box.from(vec3d), color);
    }

    public void drawBoxFilledBlock(MatrixStack matrixStack2, BlockPos blockPos, Color color) {
        drawBoxFilled(matrixStack2, new Box(blockPos), color);
    }

    public void drawBoxOutline(MatrixStack matrixStack2, Box box, Color color, double d) {
        float x = (float) (box.minX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (box.minY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (box.minZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (box.maxX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (box.maxY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (box.maxZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        Tessellator.getInstance();
        setupRenderState();
        RenderSystem.lineWidth((float) d);
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        GL20.glEnable(2848);
        GL20.glHint(3154, 4354);
        RenderSystem.defaultBlendFunc();
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        WorldRenderer.drawBox(matrixStack2, begin, x, y, z, x2, y2, z2, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        GL20.glDisable(2848);
        restoreRenderState();
    }

    public void drawBoxOutlineVec(MatrixStack matrixStack2, Vec3d vec3d, Color color, double d) {
        drawBoxOutline(matrixStack2, Box.from(vec3d), color, d);
    }

    public void drawBoxOutlineBlock(MatrixStack matrixStack2, BlockPos blockPos, Color color, double d) {
        drawBoxOutline(matrixStack2, new Box(blockPos), color, d);
    }

    public void drawThickLine(MatrixStack matrixStack2, Vec3d vec3d, Vec3d vec3d2, Color color, float f) {
        float x = (float) (vec3d.x - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (vec3d.y - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (vec3d.z - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (vec3d2.x - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (vec3d2.y - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (vec3d2.z - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        MatrixStack.Entry peek = matrixStack2.peek();
        setupRenderState();
        RenderSystem.lineWidth(f);
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        GL20.glEnable(2848);
        GL20.glHint(3154, 4354);
        RenderSystem.defaultBlendFunc();
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(color.getRGB()).normal(peek, f, 0.0f, 0.0f);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(color.getRGB()).normal(peek, f, 0.0f, 0.0f);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(color.getRGB()).normal(peek, 0.0f, f, 0.0f);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(color.getRGB()).normal(peek, 0.0f, f, 0.0f);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(color.getRGB()).normal(peek, 0.0f, 0.0f, f);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(color.getRGB()).normal(peek, 0.0f, 0.0f, f);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        GL20.glDisable(2848);
        restoreRenderState();
    }

    public void drawDebugLine(MatrixStack matrixStack2, Vec3d vec3d, Vec3d vec3d2, Color color) {
        float x = (float) (vec3d.x - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (vec3d.y - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (vec3d.z - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (vec3d2.x - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (vec3d2.y - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (vec3d2.z - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        INSTANCE.setupRenderState();
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(color.hashCode());
        begin.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(color.hashCode());
        BufferRenderer.drawWithGlobalProgram(begin.end());
        INSTANCE.restoreRenderState();
    }

    public void enableBlend() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void setupRenderState() {
        enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
    }

    public void disableBlend() {
        RenderSystem.disableBlend();
    }

    public void restoreRenderState() {
        disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    public void runWithMSAA(java.lang.Runnable runnable) {
        if (UI.uI.msaa.getValue() == UI.Mode.NONE) {
            runnable.run();
        } else {
            int i = UI.uI.msaa.getValue().getMsaaMultiplier();
            Framebuffer.renderTempAndBlit((i & (i - 1)) == 0 ? i : 2, runnable);
        }
    }

    public void drawBillboard(MatrixStack matrixStack2, Vec3d vec3d, float f, float f2, float f3, float f4, double d, Color color) {
        matrixStack2.push();
        translateToPos(matrixStack2, vec3d);
        RenderSystem.defaultBlendFunc();
        matrixStack2.translate(f, f2, 0.0f);
        matrixStack2.multiply(minecraftClient.getEntityRenderDispatcher().getRotation());
        matrixStack2.scale(0.025f * ((float) d), (-0.025f) * ((float) d), (float) (0.02500000037252903d * d));
        int i = (int) (f3 / 2.0f);
        RenderSystem.disableDepthTest();
        drawFilledRectColor(matrixStack2, -i, 0.0f, (int) (f3 - i), (int) f4, color);
        RenderSystem.enableDepthTest();
        matrixStack2.pop();
    }

    public void drawTextureBillboard(DrawContext drawContext, Vec3d vec3d, float f, float f2, double d, Color color, Identifier identifier) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        translateToPos(matrices, vec3d);
        Camera camera = minecraftClient.gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        RenderSystem.defaultBlendFunc();
        matrices.scale(0.025f * ((float) d), (-0.025f) * ((float) d), 1.0f);
        int i = (int) (f / 2.0f);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
        drawContext.drawTexture(identifier, -i, -i, 0, 0, (int) f, (int) f2);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableDepthTest();
        matrices.pop();
    }

    public void drawBillboardFacing(MatrixStack matrixStack2, Vec3d vec3d, float f, float f2, float f3, float f4, double d, Color color) {
        matrixStack2.push();
        translateToPos(matrixStack2, vec3d);
        Camera camera = minecraftClient.gameRenderer.getCamera();
        matrixStack2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrixStack2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        RenderSystem.defaultBlendFunc();
        matrixStack2.translate(f, f2, 0.0f);
        matrixStack2.scale(0.025f * ((float) d), (-0.025f) * ((float) d), 1.0f);
        int i = (int) (f3 / 2.0f);
        RenderSystem.disableDepthTest();
        drawRectOutline(matrixStack2, -i, 0.0f, (int) (f3 - i), (int) f4, color);
        RenderSystem.enableDepthTest();
        matrixStack2.pop();
    }

    public void drawTextBillboard(DrawContext drawContext, String str, Vec3d vec3d, float f, float f2, double d, Color color, boolean z) {
        drawTextBillboardOffset(drawContext, str, vec3d, f, f2, (-ClientTextRenderer.INSTANCE.getStringWidth(str)) / 2.0f, 0.0f, d, color, z);
    }

    public void drawTextBillboardOffset(DrawContext drawContext, String str, Vec3d vec3d, float f, float f2, float f3, float f4, double d, Color color, boolean z) {
        drawContext.getMatrices().push();
        translateToPos(drawContext.getMatrices(), vec3d);
        RenderSystem.defaultBlendFunc();
        drawContext.getMatrices().translate(f, f2, 0.0f);
        drawContext.getMatrices().multiply(minecraftClient.getEntityRenderDispatcher().getRotation());
        drawContext.getMatrices().scale(0.025f * ((float) d), (-0.025f) * ((float) d), (float) (0.02500000037252903d * d));
        if (z) {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str, f3, f4, color);
        } else {
            ClientTextRenderer.INSTANCE.drawText(drawContext, str, f3, f4, color);
        }
        drawContext.getMatrices().pop();
    }

    public void addLineVertex(MatrixStack matrixStack2, BufferBuilder bufferBuilder, Vec3d vec3d, Vec3d vec3d2, int i, int i2) {
        float x = (float) (vec3d.getX() - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (vec3d.getY() - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (vec3d.getZ() - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (vec3d2.getX() - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y2 = (float) (vec3d2.getY() - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z2 = (float) (vec3d2.getZ() - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        bufferBuilder.vertex(matrixStack2.peek().getPositionMatrix(), x, y, z).color(i).normal(matrixStack2.peek(), x2 - x, y2 - y, z2 - z);
        bufferBuilder.vertex(matrixStack2.peek().getPositionMatrix(), x2, y2, z2).color(i2).normal(matrixStack2.peek(), x2 - x, y2 - y, z2 - z);
    }

    public void addFloorQuad(MatrixStack matrixStack2, BufferBuilder bufferBuilder, Box box, int i) {
        float x = (float) (box.minX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float y = (float) (box.minY - minecraftClient.getEntityRenderDispatcher().camera.getPos().getY());
        float z = (float) (box.minZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        float x2 = (float) (box.maxX - minecraftClient.getEntityRenderDispatcher().camera.getPos().getX());
        float z2 = (float) (box.maxZ - minecraftClient.getEntityRenderDispatcher().camera.getPos().getZ());
        Matrix4f positionMatrix = matrixStack2.peek().getPositionMatrix();
        bufferBuilder.vertex(positionMatrix, x, y, z).color(i);
        bufferBuilder.vertex(positionMatrix, x2, y, z).color(i);
        bufferBuilder.vertex(positionMatrix, x2, y, z2).color(i);
        bufferBuilder.vertex(positionMatrix, x, y, z2).color(i);
        bufferBuilder.vertex(positionMatrix, x, y, z).color(i);
    }

    public void renderEntity(Entity entity, float f, MatrixStack matrixStack2, VertexConsumerProvider vertexConsumerProvider) {
        EntityRenderDispatcher entityRenderDispatcher = minecraftClient.getEntityRenderDispatcher();
        Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
        entityRenderDispatcher.render(entity, MathHelper.lerp(f, entity.lastRenderX, entity.getX()) - pos.x, MathHelper.lerp(f, entity.lastRenderY, entity.getY()) - pos.y, MathHelper.lerp(f, entity.lastRenderZ, entity.getZ()) - pos.z, MathHelper.lerp(f, entity.prevYaw, entity.getYaw()), f, matrixStack2, vertexConsumerProvider, entityRenderDispatcher.getLight(entity, f));
    }

    public void drawGradientQuadMatrix(Matrix4f matrix4f, float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
        float[] fArr = {((i >> 16) & 255) / 255.0f, ((i >> 8) & 255) / 255.0f, (i & 255) / 255.0f};
        float[] fArr2 = {((i2 >> 16) & 255) / 255.0f, ((i2 >> 8) & 255) / 255.0f, (i2 & 255) / 255.0f};
        float[] fArr3 = {((i3 >> 16) & 255) / 255.0f, ((i3 >> 8) & 255) / 255.0f, (i3 & 255) / 255.0f};
        float[] fArr4 = {((i4 >> 16) & 255) / 255.0f, ((i4 >> 8) & 255) / 255.0f, (i4 & 255) / 255.0f};
        enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        begin.vertex(matrix4f, f, f2 + f4, 0.0f).color(fArr4[0], fArr4[1], fArr4[2], 1.0f);
        begin.vertex(matrix4f, f + f3, f2 + f4, 0.0f).color(fArr3[0], fArr3[1], fArr3[2], 1.0f);
        begin.vertex(matrix4f, f + f3, f2, 0.0f).color(fArr2[0], fArr2[1], fArr2[2], 1.0f);
        begin.vertex(matrix4f, f, f2, 0.0f).color(fArr[0], fArr[1], fArr[2], 1.0f);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        disableBlend();
    }

    public void drawGradientBar(Matrix4f matrix4f, float f, float f2, float f3, float f4, int... iArr) {
        if (iArr.length < 2) {
            return;
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
        enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        float length = f3 / iArr.length;
        int i2 = 1;
        while (i2 < iArr.length) {
            Tessellator.getInstance();
            BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            begin.vertex(matrix4f, f + (i2 == iArr.length - 1 ? f3 : length * i2), f2, 0.0f).color(fArr[i2][1], fArr[i2][2], fArr[i2][3], fArr[i2][0]);
            begin.vertex(matrix4f, f + (length * (i2 - 1)), f2, 0.0f).color(fArr[i2 - 1][1], fArr[i2 - 1][2], fArr[i2 - 1][3], fArr[i2 - 1][0]);
            begin.vertex(matrix4f, f + (length * (i2 - 1)), f2 + f4, 0.0f).color(fArr[i2 - 1][1], fArr[i2 - 1][2], fArr[i2 - 1][3], fArr[i2 - 1][0]);
            begin.vertex(matrix4f, f + (i2 == iArr.length - 1 ? f3 : length * i2), f2 + f4, 0.0f).color(fArr[i2][1], fArr[i2][2], fArr[i2][3], fArr[i2][0]);
            BufferRenderer.drawWithGlobalProgram(begin.end());
            i2++;
        }
        disableBlend();
    }

    public static void translateToPos(MatrixStack matrixStack2, Vec3d vec3d) {
        Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
        matrixStack2.translate(vec3d.x - pos.x, vec3d.y - pos.y, vec3d.z - pos.z);
    }

    public static void applyCameraRotation(MatrixStack matrixStack2) {
        Camera camera = minecraftClient.gameRenderer.getCamera();
        matrixStack2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
    }
}
