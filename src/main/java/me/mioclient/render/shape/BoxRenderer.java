package me.mioclient.render.shape;

import me.mioclient.api.ClientAccessor;
import me.mioclient.render.GizmoRenderer;

import java.awt.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public final class BoxRenderer implements ClientAccessor {


    /* 26.2 backend: geometry is emitted inside submitCustomGeometry lambdas so the
       GPU backend records it. The lambda receives the snapshotted pose; verts must be
       camera-relative (world minus camera), matching vanilla's own outline submits. */


    /* Snapshot a live PoseStack pose for deferred emission (multi-pose recorders like
       Skeleton/Chams must not reference the mutable stack top). */

    public static void submitLinePair(PoseStack stackForSnapshot, PoseStack.Pose pose, float ax, float ay, float az, float bx, float by, float bz, int color) {
        submitLinePair(stackForSnapshot, pose, ax, ay, az, bx, by, bz, color, color);
    }

    public static void submitLinePair(PoseStack stackForSnapshot, PoseStack.Pose pose, float ax, float ay, float az, float bx, float by, float bz, int colorA, int colorB) {
        /* 26.2 backend: the model-space segment is transformed to world space by the
           pose snapshot (poses handed down here are camera-relative), then drawn as
           a vanilla gizmo line. */
        if (pose == null) {
            return;
        }
        try {
            Vec3 cam = minecraftClient.gameRenderer.mainCamera().position();
            org.joml.Vector3f a = new org.joml.Vector3f(ax, ay, az);
            org.joml.Vector3f b = new org.joml.Vector3f(bx, by, bz);
            pose.pose().transformPosition(a);
            pose.pose().transformPosition(b);
            GizmoRenderer.line(new Vec3(a.x + cam.x, a.y + cam.y, a.z + cam.z), new Vec3(b.x + cam.x, b.y + cam.y, b.z + cam.z), colorA, 1.0f);
        } catch (Exception ignored) {
        }
    }


    public static void drawLine(PoseStack matrixStack, Vec3 a, Vec3 b, java.awt.Color color) {
        drawLine(matrixStack, a, b, color.hashCode(), color.hashCode());
    }

    public static void drawLine(PoseStack matrixStack, Vec3 a, Vec3 b, int colorA, int colorB) {
        if (a == null || b == null) {
            return;
        }
        GizmoRenderer.line(a, b, colorA, 1.0f);
    }

    /* World-anchored labels through vanilla's nametag pipeline (bg plate, distance
       scaling and depth handled by vanilla). Mirrors EntityRenderer.submitNameDisplay. */
    public static void submitMultilineTag(PoseStack matrixStack, Vec3 worldPos, double yStart, double lineStep, java.util.List<Component> lines, CameraRenderState camera, float scale) {
        if (lines == null || lines.isEmpty()) {
            return;
        }
        double y = yStart;
        for (Component line : lines) {
            if (line == null) {
                continue;
            }
            submitNameTag(matrixStack, worldPos, y, line, camera, scale);
            y += lineStep;
        }
    }
    public static void submitNameTag(PoseStack matrixStack, Vec3 worldPos, double yOffset, Component text, CameraRenderState camera, float scale) {
        /* 26.2 backend: world-anchored labels go through vanilla gizmo billboard
           text (bg plate, distance scaling and depth handled by vanilla). */
        if (worldPos == null || text == null) {
            return;
        }
        String string = text.getString();
        if (string == null || string.isEmpty()) {
            return;
        }
        GizmoRenderer.text(new Vec3(worldPos.x, worldPos.y + yOffset, worldPos.z), string, -1, scale <= 0.0f ? 1.0f : scale);
    }


    public static void drawFilledBox(PoseStack matrixStack, BlockPos blockPos, Color color) {
        drawFilledBox(matrixStack, new AABB(blockPos), color);
    }

    public static void drawFilledBox(PoseStack matrixStack, AABB box, Color color) {
        if (color == null || box == null || color.getAlpha() == 0) {
            return;
        }
        GizmoRenderer.box(box, 0, 0.0f, applyShaderColor(color).hashCode(), false);
    }

    public static void drawFadingBox(PoseStack matrixStack, AABB box, Color color) {
        /* 26.2 backend: real gradient fade rebuilt from stacked gizmo slabs. */
        if (color == null || box == null || color.getAlpha() == 0) {
            return;
        }
        GizmoRenderer.fadingBox(box, color.hashCode(), me.mioclient.util.math.ColorUtils.toIntWithAlpha(color, 0));
    }


    public static void drawBoxOutline(PoseStack matrixStack, BlockPos blockPos, Color color, float f) {
        drawBoxOutline(matrixStack, new AABB(blockPos), color, f);
    }

    public static void drawBoxOutline(PoseStack matrixStack, AABB box, Color color, float f) {
        if (color == null || box == null || color.getAlpha() == 0 || f <= 0.0f) {
            return;
        }
        GizmoRenderer.box(box, color.hashCode(), f, 0, false);
    }


    public static Color applyShaderColor(Color color) {
        return color;
    }

    public static Color applyShaderColor(Color color, float[] fArr) {
        return new Color((int) (color.getRed() * fArr[0]), (int) (color.getGreen() * fArr[1]), (int) (color.getBlue() * fArr[2]), (int) (color.getAlpha() * fArr[3]));
    }


}
