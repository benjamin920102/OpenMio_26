package me.mioclient.module.render;

import me.mioclient.api.ClientServices;

import me.mioclient.render.mesh.TrajectoriesVertexConsumer;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.MathConstants;

import java.awt.Color;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.model.player.PlayerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import me.mioclient.setting.ColorSetting;

public class Skeleton extends Module {
    public static Animations animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class);
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-4351489, true)).setThemeSync(true));

    public Skeleton() {
        super("Skeleton", "Draws skeletons for players.", Category.RENDER, new String[0]);
        setDrawn(false);
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        PoseStack matrixStack472 = inner_3.getMatrixStack();
        float f = inner_3.getTickDelta();
        matrixStack472.pushPose();
        for (AbstractClientPlayer abstractClientPlayerEntity : minecraftClient.level.players()) {
            if (!abstractClientPlayerEntity.isRemoved() && abstractClientPlayerEntity != minecraftClient.player) {
                Color value = this.color.getValue();
                Vec3 vec3d2641 = getCameraRelativePos((Entity) abstractClientPlayerEntity, f);
                PlayerModel model = (PlayerModel) ((AvatarRenderer) minecraftClient.getEntityRenderDispatcher().getRenderer(abstractClientPlayerEntity)).getModel();
                float lerpAngleDegrees = Mth.rotLerp(f, abstractClientPlayerEntity.yBodyRotO, abstractClientPlayerEntity.yBodyRot);
                float lerp = Mth.lerp(f, abstractClientPlayerEntity.xRotO, abstractClientPlayerEntity.getXRot());
                if (!animations.isStaticAnimationEnabled()) {
                    float lerpAngleDegrees2 = Mth.rotLerp(f, abstractClientPlayerEntity.yHeadRotO, abstractClientPlayerEntity.yHeadRot) - Mth.rotLerp(f, abstractClientPlayerEntity.yBodyRotO, abstractClientPlayerEntity.yBodyRot);
                    float mio$getAnimationProgress = (float) abstractClientPlayerEntity.tickCount + f;
                    float f2 = 0.0f;
                    float f3 = 0.0f;
                    if (!abstractClientPlayerEntity.isPassenger() && abstractClientPlayerEntity.isAlive()) {
                        f2 = Math.min(abstractClientPlayerEntity.walkAnimation.speed(f), Float.intBitsToFloat(1065353216));
                        f3 = abstractClientPlayerEntity.walkAnimation.position(f);
                        if (abstractClientPlayerEntity.isBaby()) {
                            f3 *= Float.intBitsToFloat(1077936128);
                        }
                    }
                    model.head.yRot = lerpAngleDegrees2 * 0.017453292f;
                    model.head.xRot = lerp * 0.017453292f;
                    model.leftLeg.xRot = Mth.cos(f3 * 0.6662f) * 1.4f * f2;
                    model.rightLeg.xRot = Mth.cos(f3 * 0.6662f + (float) Math.PI) * 1.4f * f2;
                    model.leftArm.xRot = Mth.cos(f3 * 0.6662f) * 2.0f * f2 * 0.5f;
                    model.rightArm.xRot = Mth.cos(f3 * 0.6662f + (float) Math.PI) * 2.0f * f2 * 0.5f;
                }
                boolean isInSwimmingPose = abstractClientPlayerEntity.isSwimming();
                boolean z = abstractClientPlayerEntity.isShiftKeyDown() || animations.isSneakAnimationEnabled();
                boolean isFallFlying = abstractClientPlayerEntity.isFallFlying();
                ModelPart modelPart = model.head;
                ModelPart modelPart2 = model.leftArm;
                ModelPart modelPart3 = model.rightArm;
                ModelPart modelPart4 = model.leftLeg;
                ModelPart modelPart5 = model.rightLeg;
                matrixStack472.translate(vec3d2641.x, vec3d2641.y, vec3d2641.z);
                if (isInSwimmingPose) {
                    matrixStack472.translate(0.0f, Float.intBitsToFloat(1051931443), 0.0f);
                }
                matrixStack472.mulPose(new Quaternionf().setAngleAxis(((lerpAngleDegrees + Float.intBitsToFloat(1127481344)) * MathConstants.val) / Double.longBitsToDouble(4640537203540230144L), 0.0d, Double.longBitsToDouble(-4616189618054758400L), 0.0d));
                if (isInSwimmingPose || isFallFlying) {
                    matrixStack472.mulPose(new Quaternionf().setAngleAxis(((MathConstants.num2 + lerp) * MathConstants.val) / Double.longBitsToDouble(4640537203540230144L), Double.longBitsToDouble(-4616189618054758400L), 0.0d, 0.0d));
                }
                if (isInSwimmingPose) {
                    matrixStack472.translate(0.0f, Float.intBitsToFloat(-1082969293), 0.0f);
                }
                if (animations.isPlayerScaleModified()) {
                    matrixStack472.scale(animations.playerScale.getValue().floatValue(), animations.playerScale.getValue().floatValue(), animations.playerScale.getValue().floatValue());
                }
                PoseStack.Pose pose = matrixStack472.last();
                int rgb = value.getRGB();
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose, 0.0f, z ? Float.intBitsToFloat(1058642330) : Float.intBitsToFloat(1060320051), z ? Float.intBitsToFloat(1047233823) : 0.0f, 0.0f, z ? Float.intBitsToFloat(1065772646) : Float.intBitsToFloat(1068708659), 0.0f, rgb);
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose, Float.intBitsToFloat(-1094881116), z ? Float.intBitsToFloat(1065772646) : Float.intBitsToFloat(1068289229), 0.0f, Float.intBitsToFloat(1052602532), z ? Float.intBitsToFloat(1065772646) : Float.intBitsToFloat(1068289229), 0.0f, rgb);
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose, Float.intBitsToFloat(-1105618534), z ? Float.intBitsToFloat(1058642330) : Float.intBitsToFloat(1060320051), z ? Float.intBitsToFloat(1047233823) : 0.0f, Float.intBitsToFloat(1041865114), z ? Float.intBitsToFloat(1058642330) : Float.intBitsToFloat(1060320051), z ? Float.intBitsToFloat(1047233823) : 0.0f, rgb);
                matrixStack472.pushPose();
                matrixStack472.translate(0.0f, z ? Float.intBitsToFloat(1065772646) : Float.intBitsToFloat(1068708659), 0.0f);
                applyModelPartRotation(matrixStack472, modelPart);
                PoseStack.Pose pose2 = matrixStack472.last();
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose2, 0.0f, 0.0f, 0.0f, 0.0f, Float.intBitsToFloat(1041865114), 0.0f, rgb);
                matrixStack472.popPose();
                matrixStack472.pushPose();
                matrixStack472.translate(Float.intBitsToFloat(1041865114), z ? Float.intBitsToFloat(1058642330) : Float.intBitsToFloat(1060320051), z ? Float.intBitsToFloat(1047233823) : 0.0f);
                applyModelPartRotation(matrixStack472, modelPart5);
                PoseStack.Pose pose3 = matrixStack472.last();
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose3, 0.0f, 0.0f, 0.0f, 0.0f, Float.intBitsToFloat(-1088841318), 0.0f, rgb);
                matrixStack472.popPose();
                matrixStack472.pushPose();
                matrixStack472.translate(Float.intBitsToFloat(-1105618534), z ? Float.intBitsToFloat(1058642330) : Float.intBitsToFloat(1060320051), z ? Float.intBitsToFloat(1047233823) : 0.0f);
                applyModelPartRotation(matrixStack472, modelPart4);
                PoseStack.Pose pose4 = matrixStack472.last();
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose4, 0.0f, 0.0f, 0.0f, 0.0f, Float.intBitsToFloat(-1088841318), 0.0f, rgb);
                matrixStack472.popPose();
                matrixStack472.pushPose();
                matrixStack472.translate(Float.intBitsToFloat(1052602532), z ? Float.intBitsToFloat(1065772646) : Float.intBitsToFloat(1068289229), 0.0f);
                applyModelPartRotation(matrixStack472, modelPart3);
                PoseStack.Pose pose5 = matrixStack472.last();
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose5, 0.0f, 0.0f, 0.0f, 0.0f, Float.intBitsToFloat(-1089680179), 0.0f, rgb);
                matrixStack472.popPose();
                matrixStack472.pushPose();
                matrixStack472.translate(Float.intBitsToFloat(-1094881116), z ? Float.intBitsToFloat(1065772646) : Float.intBitsToFloat(1068289229), 0.0f);
                applyModelPartRotation(matrixStack472, modelPart2);
                PoseStack.Pose pose6 = matrixStack472.last();
                me.mioclient.render.shape.BoxRenderer.submitLinePair(matrixStack472, pose6, 0.0f, 0.0f, 0.0f, 0.0f, Float.intBitsToFloat(-1089680179), 0.0f, rgb);
                matrixStack472.popPose();
                if (isInSwimmingPose) {
                    matrixStack472.translate(0.0f, Float.intBitsToFloat(1064514355), 0.0f);
                }
                if (isInSwimmingPose || isFallFlying) {
                    matrixStack472.mulPose(new Quaternionf().setAngleAxis(((MathConstants.num2 + lerp) * MathConstants.val) / Double.longBitsToDouble(4640537203540230144L), Double.longBitsToDouble(4607182418800017408L), 0.0d, 0.0d));
                }
                if (isInSwimmingPose) {
                    matrixStack472.translate(0.0f, Float.intBitsToFloat(-1095552205), 0.0f);
                }
                matrixStack472.mulPose(new Quaternionf().setAngleAxis(((lerpAngleDegrees + Float.intBitsToFloat(1127481344)) * MathConstants.val) / Double.longBitsToDouble(4640537203540230144L), 0.0d, Double.longBitsToDouble(4607182418800017408L), 0.0d));
                matrixStack472.translate(-vec3d2641.x, -vec3d2641.y, -vec3d2641.z);
            }
        }
        matrixStack472.popPose();
    }

    public void applyModelPartRotation(PoseStack matrixStack, ModelPart modelPart) {
        if (modelPart.zRot != 0.0f) {
            matrixStack.mulPose(Axis.ZP.rotation(modelPart.zRot));
        }
        if (modelPart.yRot != 0.0f) {
            matrixStack.mulPose(Axis.YN.rotation(modelPart.yRot));
        }
        if (modelPart.xRot != 0.0f) {
            matrixStack.mulPose(Axis.XN.rotation(modelPart.xRot));
        }
    }

    public Vec3 getCameraRelativePos(Entity entity, double d) {
        return new Vec3((entity.xo + ((entity.getX() - entity.xo) * d)) - minecraftClient.getEntityRenderDispatcher().camera.position().x, (entity.yo + ((entity.getY() - entity.yo) * d)) - minecraftClient.getEntityRenderDispatcher().camera.position().y, (entity.zo + ((entity.getZ() - entity.zo) * d)) - minecraftClient.getEntityRenderDispatcher().camera.position().z);
    }
}