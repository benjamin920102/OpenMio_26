package me.mioclient.render.chams;

import me.mioclient.util.player.ClonedPlayerEntity;

import me.mioclient.render.RenderUtil;

import me.mioclient.render.mesh.TrajectoriesVertexConsumer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.module.render.chams.BoatChamsRenderer;

import me.mioclient.event.EventBus;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.event.Listen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import me.mioclient.render.mesh.TrajectoriesVertexConsumer;
import me.mioclient.util.player.ClonedPlayerEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChamsRenderDispatcher.class */
public class ChamsRenderDispatcher implements ClientAccessor {
    public static final Vector4f vector4f = new Vector4f();
    public static final Vector4f vector4f2 = new Vector4f();
    public static final Vector4f vector4f3 = new Vector4f();
    public static final Vector4f vector4f4 = new Vector4f();
    public static final Map<EntityType<?>, EntityRenderCallback<?>> map = new HashMap();
    public static final LivingEntityChamsRenderer searchHelper4_2 = new LivingEntityChamsRenderer();
    public static final TrajectoriesVertexConsumer trajectoriesVertexConsumer = TrajectoriesVertexConsumer.createDefault();
    public static final TrajectoriesVertexConsumer trajectoriesVertexConsumer2 = TrajectoriesVertexConsumer.createDefault();
    public static Color color;
    public static Color color2;
    public static boolean flag;
    public static double val;

    public ChamsRenderDispatcher() {
        throw new AssertionError();
    }

    @Listen(get219= -9999)
    public static void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        RenderUtil.INSTANCE.setupRenderState();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        trajectoriesVertexConsumer.drawBuffer();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth((float) val);
        trajectoriesVertexConsumer2.drawBuffer();
        RenderSystem.lineWidth(1.0f);
        RenderUtil.INSTANCE.restoreRenderState();
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

    public static void renderEntityChams(MatrixStack matrixStack, Entity entity) {
        Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
        float f = RenderUtil.getPartialTick();
        if (entity instanceof ClonedPlayerEntity) {
            f = 1.0f;
        }
        double lerp = MathHelper.lerp(f, entity.lastRenderX, entity.getX()) - pos.x;
        double lerp2 = MathHelper.lerp(f, entity.lastRenderY, entity.getY()) - pos.y;
        double lerp3 = MathHelper.lerp(f, entity.lastRenderZ, entity.getZ()) - pos.z;
        Vec3d positionOffset = minecraftClient.getEntityRenderDispatcher().getRenderer(entity).getPositionOffset(entity, f);
        double x = lerp + positionOffset.getX();
        double y = lerp2 + positionOffset.getY();
        double z = lerp3 + positionOffset.getZ();
        matrixStack.push();
        matrixStack.translate(x, y, z);
        EntityRenderCallback helper_6616 = getEntityChamsRenderer(entity);
        if (helper_6616 != null) {
            helper_6616.do721(entity, RenderUtil.getPartialTick(), matrixStack);
        }
        matrixStack.pop();
    }

    public static <E extends Entity> EntityRenderCallback<E> getEntityChamsRenderer(E e) {
        return (EntityRenderCallback) map.getOrDefault(e.getType(), e instanceof LivingEntity ? searchHelper4_2 : null);
    }

    public static void renderModelPart(ChamsRenderContext matrixStackData, ModelPart modelPart) {
        if (modelPart.visible) {
            matrixStackData.getMatrixStack().push();
            modelPart.rotate(matrixStackData.getMatrixStack());
            Iterator it = modelPart.cuboids.iterator();
            while (it.hasNext()) {
                renderCuboid(matrixStackData, (ModelPart.Cuboid) it.next());
            }
            Iterator it2 = modelPart.children.values().iterator();
            while (it2.hasNext()) {
                renderModelPart(matrixStackData, (ModelPart) it2.next());
            }
            matrixStackData.getMatrixStack().pop();
        }
    }

    public static void renderCuboid(ChamsRenderContext matrixStackData, ModelPart.Cuboid cuboid) {
        for (ModelPart.Quad quad : cuboid.sides) {
            vector4f.set(quad.vertices[0].pos.x / 16.0f, quad.vertices[0].pos.y / 16.0f, quad.vertices[0].pos.z / 16.0f, 1.0f);
            vector4f2.set(quad.vertices[1].pos.x / 16.0f, quad.vertices[1].pos.y / 16.0f, quad.vertices[1].pos.z / 16.0f, 1.0f);
            vector4f3.set(quad.vertices[2].pos.x / 16.0f, quad.vertices[2].pos.y / 16.0f, quad.vertices[2].pos.z / 16.0f, 1.0f);
            vector4f4.set(quad.vertices[3].pos.x / 16.0f, quad.vertices[3].pos.y / 16.0f, quad.vertices[3].pos.z / 16.0f, 1.0f);
            if (color2.getAlpha() != 0) {
                if (!trajectoriesVertexConsumer.isBuilding()) {
                    trajectoriesVertexConsumer.beginBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                }
                trajectoriesVertexConsumer.vertex(matrixStackData.getMatrixStack().peek().getPositionMatrix(), vector4f.x, vector4f.y, vector4f.z).color(color2.hashCode());
                trajectoriesVertexConsumer.vertex(matrixStackData.getMatrixStack().peek().getPositionMatrix(), vector4f2.x, vector4f2.y, vector4f2.z).color(color2.hashCode());
                trajectoriesVertexConsumer.vertex(matrixStackData.getMatrixStack().peek().getPositionMatrix(), vector4f3.x, vector4f3.y, vector4f3.z).color(color2.hashCode());
                trajectoriesVertexConsumer.vertex(matrixStackData.getMatrixStack().peek().getPositionMatrix(), vector4f4.x, vector4f4.y, vector4f4.z).color(color2.hashCode());
            }
            if (color.getAlpha() != 0) {
                if (!trajectoriesVertexConsumer2.isBuilding()) {
                    trajectoriesVertexConsumer2.beginBuffer(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
                }
                addOutlineEdge(matrixStackData, vector4f.x, vector4f.y, vector4f.z, vector4f2.x, vector4f2.y, vector4f2.z);
                addOutlineEdge(matrixStackData, vector4f2.x, vector4f2.y, vector4f2.z, vector4f3.x, vector4f3.y, vector4f3.z);
                addOutlineEdge(matrixStackData, vector4f3.x, vector4f3.y, vector4f3.z, vector4f4.x, vector4f4.y, vector4f4.z);
                addOutlineEdge(matrixStackData, vector4f4.x, vector4f4.y, vector4f4.z, vector4f.x, vector4f.y, vector4f.z);
            }
        }
    }

    public static void addOutlineEdge(ChamsRenderContext matrixStackData, float f, float f2, float f3, float f4, float f5, float f6) {
        trajectoriesVertexConsumer2.vertex(matrixStackData.getMatrixStack().peek().getPositionMatrix(), f, f2, f3).color(color.hashCode()).normal(matrixStackData.getMatrixStack().peek(), f4 - f, f5 - f2, f6 - f3);
        trajectoriesVertexConsumer2.vertex(matrixStackData.getMatrixStack().peek().getPositionMatrix(), f4, f5, f6).color(color.hashCode()).normal(matrixStackData.getMatrixStack().peek(), f4 - f, f5 - f2, f6 - f3);
    }

    public static void setOutlineWidth(double d) {
        val = d;
    }

    static {
        eventBus.registerStatic(ChamsRenderDispatcher.class);
        map.put(EntityType.END_CRYSTAL, new EndCrystalRenderer());
        map.put(EntityType.BOAT, new BoatChamsRenderer());
        map.put(EntityType.CHEST_BOAT, new BoatChamsRenderer());
        map.put(EntityType.LLAMA, new LlamaChamsRenderer());
        map.put(EntityType.RABBIT, new RabbitChamsRenderer());
        map.put(EntityType.ENDER_DRAGON, new DragonChamsRenderer());
        color = Color.white;
        color2 = Color.white;
    }
}
