package me.mioclient.render.chams;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.ClonedPlayerEntity;

import me.mioclient.mode.render.MatrixStackDataMode;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.mixin.ducks.DuckAnimalModel;
import me.mioclient.mixin.ducks.DuckLivingEntityRenderer;
import me.mioclient.module.render.Animations;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import me.mioclient.mode.render.MatrixStackDataMode;
import me.mioclient.manager.RotationManager;
import me.mioclient.util.player.ClonedPlayerEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/LivingEntityChamsRenderer.class */
public class LivingEntityChamsRenderer implements ClientAccessor, EntityRenderCallback<LivingEntity> {
    public static Animations animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class);
    public boolean flag;
    public boolean flag2 = false;
    public boolean flag3 = false;

    @Override // me.mioclient.render.chams.EntityRenderCallback
    /* renamed from: do1539, reason: merged with bridge method [inline-methods] */
    public void do721(LivingEntity livingEntity, float f, MatrixStack matrixStack) {
        Direction sleepingDirection;
        this.flag = minecraftClient.player == livingEntity;
        this.flag2 = livingEntity instanceof ClonedPlayerEntity;
        if (this.flag2) {
            f = 1.0f;
        }
        if (livingEntity instanceof ClonedPlayerEntity) {
            this.flag3 = ((ClonedPlayerEntity) livingEntity).isChamsVisible();
        } else {
            this.flag3 = false;
        }
        LivingEntityRenderer renderer = (LivingEntityRenderer) minecraftClient.getEntityRenderDispatcher().getRenderer(livingEntity);
        if (renderer instanceof LivingEntityRenderer) {
            DuckLivingEntityRenderer duckLivingEntityRenderer = (DuckLivingEntityRenderer) renderer;
            EntityModel model = (EntityModel)(renderer.getModel());
            matrixStack.push();
            ((EntityModel) model).handSwingProgress = livingEntity.getHandSwingProgress(f);
            ((EntityModel) model).riding = livingEntity.hasVehicle();
            ((EntityModel) model).child = livingEntity.isBaby();
            float lerpAngleDegrees = MathHelper.lerpAngleDegrees(f, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
            float lerpAngleDegrees2 = MathHelper.lerpAngleDegrees(f, livingEntity.prevHeadYaw, livingEntity.headYaw);
            if (this.flag) {
                lerpAngleDegrees = MathHelper.lerpAngleDegrees(f, rotations().getPrevBodyYaw(), rotations().getBodyYaw());
                lerpAngleDegrees2 = MathHelper.lerpAngleDegrees(f, rotations().getPrevRawYaw(), rotations().getRawYaw());
            }
            float f2 = lerpAngleDegrees2 - lerpAngleDegrees;
            if (livingEntity.hasVehicle()) {
                LivingEntity vehicle = (livingEntity.getVehicle()) instanceof LivingEntity ? (LivingEntity) (livingEntity.getVehicle()) : null;
                if (vehicle instanceof LivingEntity) {
                    LivingEntity livingEntity2 = vehicle;
                    float clamp = MathHelper.clamp(MathHelper.wrapDegrees(lerpAngleDegrees2 - (this.flag ? MathHelper.lerpAngleDegrees(f, rotations().getPrevBodyYaw(), rotations().getBodyYaw()) : MathHelper.lerpAngleDegrees(f, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw))), -85.0f, 85.0f);
                    lerpAngleDegrees = lerpAngleDegrees2 - clamp;
                    if (clamp * clamp > 2500.0f) {
                        lerpAngleDegrees += clamp * 0.2f;
                    }
                    f2 = lerpAngleDegrees2 - lerpAngleDegrees;
                }
            }
            float lerp = MathHelper.lerp(f, livingEntity.prevPitch, livingEntity.getPitch());
            if (this.flag) {
                lerp = MathHelper.lerp(f, rotations().getPrevPitch(), rotations().getPitch());
            }
            if (LivingEntityRenderer.shouldFlipUpsideDown(livingEntity)) {
                lerp *= -1.0f;
                f2 *= -1.0f;
            }
            if (livingEntity.isInPose(EntityPose.SLEEPING) && (sleepingDirection = livingEntity.getSleepingDirection()) != null) {
                float eyeHeight = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1f;
                matrixStack.translate((-sleepingDirection.getOffsetX()) * eyeHeight, 0.0f, (-sleepingDirection.getOffsetZ()) * eyeHeight);
            }
            if (model instanceof BipedEntityModel) {
                BipedEntityModel bipedEntityModel = (BipedEntityModel) model;
                if ((livingEntity instanceof PlayerEntity) && ((PlayerEntity) livingEntity) != minecraftClient.player && animations.isSneakAnimationEnabled()) {
                    bipedEntityModel.sneaking = true;
                } else {
                    bipedEntityModel.sneaking = livingEntity.isSneaking();
                }
            }
            float mio$getAnimationProgress = duckLivingEntityRenderer.mio$getAnimationProgress(livingEntity, f);
            duckLivingEntityRenderer.mio$setupTransforms(livingEntity, matrixStack, mio$getAnimationProgress, lerpAngleDegrees, f, livingEntity.getScale());
            matrixStack.scale(-1.0f, -1.0f, 1.0f);
            duckLivingEntityRenderer.mio$scale(livingEntity, matrixStack, f);
            matrixStack.translate(0.0f, -1.501f, 0.0f);
            float f3 = 0.0f;
            float f4 = 0.0f;
            if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
                if (livingEntity instanceof ClonedPlayerEntity) {
                    f3 = Math.min(livingEntity.limbAnimator.getSpeed(), 1.0f);
                    f4 = livingEntity.limbAnimator.getPos();
                } else {
                    f3 = Math.min(livingEntity.limbAnimator.getSpeed(f), 1.0f);
                    f4 = livingEntity.limbAnimator.getPos(f);
                }
                if (livingEntity.isBaby()) {
                    f4 *= 3.0f;
                }
            }
            model.animateModel(livingEntity, f4, f3, f);
            model.setAngles(livingEntity, f4, f3, mio$getAnimationProgress, f2, lerp);
            ChamsRenderContext matrixStackData = new ChamsRenderContext(matrixStack, MatrixStackDataMode.BOTH);
            if (model instanceof VillagerResemblingModel) {
                ((VillagerResemblingModel) model).hatRim.visible = false;
            }
            if (model instanceof AnimalModel) {
                renderAnimalModel(matrixStackData, (AnimalModel) model);
            } else if (model instanceof SinglePartEntityModel) {
                ChamsRenderDispatcher.renderModelPart(matrixStackData, ((SinglePartEntityModel) model).getPart());
            } else if (model instanceof CompositeEntityModel) {
                ((CompositeEntityModel<?>) model).getParts().forEach(modelPart -> {
                    ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart);
                });
            } else {
                renderCustomModel(matrixStackData, model);
            }
            matrixStack.pop();
        }
    }

    public void renderCustomModel(ChamsRenderContext matrixStackData, EntityModel<?> entityModel) {
    }

    public void renderAnimalModel(ChamsRenderContext matrixStackData, AnimalModel<?> animalModel) {
        DuckAnimalModel duckAnimalModel = (DuckAnimalModel) animalModel;
        if (animalModel instanceof BipedEntityModel) {
            configureBipedOverlays((BipedEntityModel) animalModel);
        }
        if (!animalModel.child) {
            duckAnimalModel.mio$getHeadParts().forEach(modelPart -> {
                ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart);
            });
            duckAnimalModel.mio$getBodyParts().forEach(modelPart2 -> {
                ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart2);
            });
            return;
        }
        matrixStackData.getMatrixStack().push();
        if (duckAnimalModel.mio$isHeadScaled()) {
            float mio$getInvertedChildHeadScale = 1.5f / duckAnimalModel.mio$getInvertedChildHeadScale();
            matrixStackData.getMatrixStack().scale(mio$getInvertedChildHeadScale, mio$getInvertedChildHeadScale, mio$getInvertedChildHeadScale);
        }
        matrixStackData.getMatrixStack().translate(0.0f, duckAnimalModel.mio$getChildHeadYOffset() / 16.0f, duckAnimalModel.mio$getChildHeadZOffset() / 16.0f);
        duckAnimalModel.mio$getHeadParts().forEach(modelPart3 -> {
            ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart3);
        });
        matrixStackData.getMatrixStack().pop();
        matrixStackData.getMatrixStack().push();
        float mio$getInvertedChildBodyScale = 1.0f / duckAnimalModel.mio$getInvertedChildBodyScale();
        matrixStackData.getMatrixStack().scale(mio$getInvertedChildBodyScale, mio$getInvertedChildBodyScale, mio$getInvertedChildBodyScale);
        matrixStackData.getMatrixStack().translate(0.0f, duckAnimalModel.mio$getChildBodyYOffset() / 16.0f, 0.0f);
        duckAnimalModel.mio$getBodyParts().forEach(modelPart4 -> {
            ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart4);
        });
        matrixStackData.getMatrixStack().pop();
    }

    public void configureBipedOverlays(BipedEntityModel<?> bipedEntityModel) {
        boolean z = ChamsRenderDispatcher.isExtraLayer() && !this.flag2;
        bipedEntityModel.hat.visible = z;
        if (bipedEntityModel instanceof PlayerEntityModel) {
            PlayerEntityModel playerEntityModel = (PlayerEntityModel) bipedEntityModel;
            playerEntityModel.leftPants.visible = z;
            playerEntityModel.rightPants.visible = z;
            playerEntityModel.leftSleeve.visible = z;
            playerEntityModel.rightSleeve.visible = z;
            playerEntityModel.jacket.visible = z;
            if (this.flag3) {
                playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
                playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
            }
        }
    }

    public <T> T selectForLocalPlayer(T t, T t2) {
        return this.flag ? t2 : t;
    }

    public static RotationManager rotations() {
        return ClientServices.RotationManager;
    }
}
