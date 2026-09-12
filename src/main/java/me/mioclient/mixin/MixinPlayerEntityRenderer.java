package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.mesh.CustomRenderLayers;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.MathConstants;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.mioclient.render.mesh.CustomRenderLayers;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.render.Animations;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin({PlayerEntityRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinPlayerEntityRenderer.class */
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static Animations animations;

    private static Animations getAnimations() {

        if (animations == null && ClientServices.moduleInjector != null) {

            try { animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class); } catch (Exception e) {}

        }

        return animations;

    }
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static ElytraFly elytrafly;

    private static ElytraFly getElytrafly() {

        if (elytrafly == null && ClientServices.moduleInjector != null) {

            try { elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class); } catch (Exception e) {}

        }

        return elytrafly;

    }

    @Unique
    AbstractClientPlayerEntity rendered;

    public MixinPlayerEntityRenderer(EntityRendererFactory.Context context, PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel, float f) {
        super(context, playerEntityModel, f);
    }

    @Inject(method = {"getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;"}, at = {@At("HEAD")}, cancellable = true)
    private void mio$getPositionOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, CallbackInfoReturnable<Vec3d> callbackInfoReturnable) {
        if (!getAnimations().isSneakAnimationEnabled() || abstractClientPlayerEntity == MinecraftClient.getInstance().player) {
            return;
        }
        callbackInfoReturnable.setReturnValue(new Vec3d(0.0d, (abstractClientPlayerEntity.getScale() * (-2.0f)) / 16.0d, 0.0d));
    }

    @ModifyExpressionValue(method = {"setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInSneakingPose()Z")})
    private boolean mio$isInSneakingPose(boolean z, @Local(argsOnly = true) AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (!getAnimations().isSneakAnimationEnabled() || abstractClientPlayerEntity == MinecraftClient.getInstance().player) {
            return z;
        }
        return true;
    }

    @Inject(method = {"scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V"}, at = {@At("HEAD")})
    private void scalePre(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, CallbackInfo callbackInfo) {
        this.rendered = abstractClientPlayerEntity;
    }

    @ModifyArgs(method = {"scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    private void scaleHook(Args args) {
        Animations animations_lcl = getAnimations();

        if (animations_lcl == null) return;

        if (animations_lcl.isToggled() && animations_lcl.isPlayerScaleModified() && this.rendered != MinecraftClient.getInstance().player) {
            args.set(0, Float.valueOf(((Float) args.get(0)).floatValue() * animations_lcl.playerScale.getValue().floatValue()));
            args.set(1, Float.valueOf(((Float) args.get(1)).floatValue() * animations_lcl.playerScale.getValue().floatValue()));
            args.set(2, Float.valueOf(((Float) args.get(2)).floatValue() * animations_lcl.playerScale.getValue().floatValue()));
        }
    }

    @ModifyArgs(method = {"setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
    private void setupTransformsHook(Args args) {
        ElytraFly elytrafly_lcl = getElytrafly();

        if (elytrafly_lcl == null) return;

        if (elytrafly_lcl.isToggled() && elytrafly_lcl.mode.getValue() == ElytraFly.ElytraFlyPredicateMode.BOUNCE && elytrafly_lcl.is951() && elytrafly_lcl.pitchLock.getValue().booleanValue() && elytrafly_lcl.silent.getValue().booleanValue()) {
            args.set(0, RotationAxis.POSITIVE_X.rotationDegrees(-MathConstants.num));
        }
    }

    @ModifyExpressionValue(method = {"renderArm"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityTranslucent(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;")})
    private RenderLayer renderArmHook(RenderLayer renderLayer, @Local Identifier identifier) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return renderLayer;

        return (norender_lcl.isToggled() && norender_lcl.hands.getValue().booleanValue() && ShaderPassRegistry.flag) ? RenderLayer.getEntitySolid(identifier) : renderLayer;
    }

    @ModifyExpressionValue(method = {"renderArm"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;")})
    private RenderLayer renderArm(RenderLayer renderLayer, @Local Identifier identifier) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return renderLayer;

        return norender_lcl.getHandOpacity() != 1.0f ? CustomRenderLayers.getRenderLayer2032(identifier) : renderLayer;
    }

    protected /* synthetic */ float getShadowRadius(AbstractClientPlayerEntity p0) {
        return super.getShadowRadius(p0);
    }
}
