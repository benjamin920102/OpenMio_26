package me.mioclient.render.chams;

import me.mioclient.util.player.ClonedPlayerEntity;

import me.mioclient.render.RenderUtil;
import me.mioclient.render.shape.BoxRenderer;


import me.mioclient.api.ClientAccessor;
import me.mioclient.module.render.chams.BoatChamsRenderer;


import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class ChamsRenderDispatcher implements ClientAccessor {
    public static final Vector4f vector4f = new Vector4f();
    public static final Vector4f vector4f2 = new Vector4f();
    public static final Vector4f vector4f3 = new Vector4f();
    public static final Vector4f vector4f4 = new Vector4f();
    public static final Map<EntityType<?>, EntityRenderCallback<?>> map = new HashMap<>();
    public static final LivingEntityChamsRenderer searchHelper4_2 = new LivingEntityChamsRenderer();
    public static Color color;
    public static Color color2;
    public static boolean flag;
    public static double val;

    public ChamsRenderDispatcher() {
        throw new AssertionError();
    }

    public static void setChamsColors(Color color3, Color color4) {
        color = color3;
        color2 = color4;
    }

    public static void setExtraLayer(boolean z) {
        flag = z;
    }

    public static boolean isExtraLayer() {
        return flag;
    }

    public static void renderEntityChams(PoseStack matrixStack, Entity entity) {
        Vec3 pos = minecraftClient.gameRenderer.mainCamera().position();
        float f = RenderUtil.getPartialTick();
        if (ClonedPlayerEntity.class.isInstance(entity)) {
            f = 1.0f;
        }
        double lerp = Mth.lerp(f, (float) entity.xOld, (float) entity.getX()) - pos.x;
        double lerp2 = Mth.lerp(f, (float) entity.yOld, (float) entity.getY()) - pos.y;
        double lerp3 = Mth.lerp(f, (float) entity.zOld, (float) entity.getZ()) - pos.z;
        matrixStack.pushPose();
        matrixStack.translate(lerp, lerp2, lerp3);
        EntityRenderCallback helper_6616 = getEntityChamsRenderer(entity);
        if (helper_6616 != null) {
            helper_6616.do721(entity, RenderUtil.getPartialTick(), matrixStack);
        }
        matrixStack.popPose();
    }

    public static <E extends Entity> EntityRenderCallback<E> getEntityChamsRenderer(E e) {
        return (EntityRenderCallback) map.getOrDefault(e.getType(), e instanceof LivingEntity ? searchHelper4_2 : null);
    }

    public static void renderModelPart(ChamsRenderContext matrixStackData, ModelPart modelPart) {
        if (modelPart.visible) {
            matrixStackData.getMatrixStack().pushPose();
            modelPart.translateAndRotate(matrixStackData.getMatrixStack());
            modelPart.visit(matrixStackData.getMatrixStack(), (pose, partPath, cubeIndex, cube) -> renderCube(matrixStackData, pose, cube));
            matrixStackData.getMatrixStack().popPose();
        }
    }

    public static void renderCube(ChamsRenderContext matrixStackData, PoseStack.Pose pose, ModelPart.Cube cube) {
        /* 26.2 backend: model cubes are drawn as vanilla gizmo cuboids. The cube
           corners are transformed to world space by the pose snapshot (poses handed
           down here are camera-relative). */
        if (pose == null || cube == null) {
            return;
        }
        boolean fill = color2 != null && color2.getAlpha() != 0;
        boolean stroke = color != null && color.getAlpha() != 0;
        if (!fill && !stroke) {
            return;
        }
        try {
            net.minecraft.world.phys.Vec3 cam = minecraftClient.gameRenderer.mainCamera().position();
            org.joml.Matrix4f matrix = pose.pose();
            org.joml.Vector3f v = new org.joml.Vector3f();
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;
            float[] xs = new float[]{cube.minX, cube.maxX};
            float[] ys = new float[]{cube.minY, cube.maxY};
            float[] zs = new float[]{cube.minZ, cube.maxZ};
            for (float x : xs) {
                for (float y : ys) {
                    for (float z : zs) {
                        v.set(x, y, z);
                        matrix.transformPosition(v);
                        if (v.x < minX) {
                            minX = v.x;
                        }
                        if (v.y < minY) {
                            minY = v.y;
                        }
                        if (v.z < minZ) {
                            minZ = v.z;
                        }
                        if (v.x > maxX) {
                            maxX = v.x;
                        }
                        if (v.y > maxY) {
                            maxY = v.y;
                        }
                        if (v.z > maxZ) {
                            maxZ = v.z;
                        }
                    }
                }
            }
            me.mioclient.render.GizmoRenderer.box(new net.minecraft.world.phys.AABB(minX + cam.x, minY + cam.y, minZ + cam.z, maxX + cam.x, maxY + cam.y, maxZ + cam.z),
                    stroke ? color.hashCode() : 0, (float) val, fill ? color2.hashCode() : 0, false);
        } catch (Exception ignored) {
        }
    }

    public static void renderCuboid(ChamsRenderContext matrixStackData, ModelPart.Cube cube) {
        renderCube(matrixStackData, matrixStackData.getMatrixStack().last(), cube);
    }

    public static void addOutlineEdge(ChamsRenderContext matrixStackData, float f, float f2, float f3, float f4, float f5, float f6) {
        if (matrixStackData == null || matrixStackData.getMatrixStack() == null || color == null || color.getAlpha() == 0) {
            return;
        }
        PoseStack.Pose pose = matrixStackData.getMatrixStack().last();
        BoxRenderer.submitLinePair(matrixStackData.getMatrixStack(), pose, f, f2, f3, f4, f5, f6, color.hashCode());
    }

    public static void setOutlineWidth(double d) {
        val = d;
    }

    static {
        map.put(EntityTypes.END_CRYSTAL, new EndCrystalRenderer());
        color = Color.white;
        color2 = Color.white;
    }
}
