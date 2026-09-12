package me.mioclient.mixin;

import me.mioclient.event.events.entity.EntityModelRenderEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import java.util.Collections;
import java.util.List;
import me.mioclient.event.events.entity.EntityModelRenderEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.module.render.Animations;
import me.mioclient.module.render.Chams;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin(value = {LivingEntityRenderer.class}, priority = 9999)
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinLivingEntityRenderer.class */
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> implements ClientAccessor {
    private static Chams chams;

    private static Chams getChams() {

        if (chams == null && ClientServices.moduleInjector != null) {

            try { chams = (Chams) ClientServices.moduleInjector.getModule(Chams.class); } catch (Exception e) {}

        }

        return chams;

    }
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

    @Unique
    private T lastEntity;

    @WrapWithCondition(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V")}, require = 0)
    private boolean onRenderModel(EntityModel entityModel, MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int i2, int i3) {
        Chams chams_lcl = getChams();

        if (chams_lcl == null) return true;

        if (ShaderPassRegistry.flag) {
            return true;
        }
        EntityModelRenderEvent entityEvent180 = EntityModelRenderEvent.create(this.lastEntity, i, ColorHelper.Argb.getRed(i3) / 255.0f, ColorHelper.Argb.getGreen(i3) / 255.0f, ColorHelper.Argb.getBlue(i3) / 255.0f, ColorHelper.Argb.getAlpha(i3) / 255.0f);
        eventBus.fireEvent(entityEvent180);
        int i4 = ColorUtils.argbFloatsToInt(entityEvent180.getRed(), entityEvent180.getGreen(), entityEvent180.getBlue(), entityEvent180.getAlpha());
        if (Chams.flag) {
            entityModel.render(matrixStack, chams_lcl.getShineVertexConsumer().getOrCreateBuffer(), entityEvent180.getLight(), i2);
            return false;
        }
        if (entityEvent180.isCancelled()) {
            return false;
        }
        entityModel.render(matrixStack, vertexConsumer, entityEvent180.getLight(), i2, i4);
        return false;
    }

    @ModifyExpressionValue(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;features:Ljava/util/List;")})
    private List<FeatureRenderer<T, M>> getFilteredFeatures(List<FeatureRenderer<T, M>> list) {
        return Chams.flag ? Collections.emptyList() : list;
    }

    @Redirect(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasVehicle()Z", ordinal = 2))
    public boolean renderHook(LivingEntity livingEntity) {
        Animations animations_lcl = getAnimations();

        if (animations_lcl == null) return false;

        if (animations_lcl.isStaticAnimationEnabled() && (livingEntity instanceof PlayerEntity)) {
            return true;
        }
        return livingEntity.hasVehicle();
    }

    @Redirect(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;prevBodyYaw:F"))
    private float interceptPrevBodyYaw(LivingEntity livingEntity) {
        return (livingEntity != minecraftClient.player || RotationManager.flag) ? livingEntity.prevBodyYaw : rotations().getPrevBodyYaw();
    }

    @Redirect(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;prevHeadYaw:F"))
    private float interceptPrevHeadYaw(LivingEntity livingEntity) {
        return (livingEntity != minecraftClient.player || RotationManager.flag) ? livingEntity.prevHeadYaw : rotations().getPrevRawYaw();
    }

    @Redirect(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;bodyYaw:F"))
    private float interceptBodyYaw(LivingEntity livingEntity) {
        return (livingEntity != minecraftClient.player || RotationManager.flag) ? livingEntity.bodyYaw : rotations().getBodyYaw();
    }

    @Redirect(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headYaw:F"))
    private float interceptHeadYaw(LivingEntity livingEntity) {
        return (livingEntity != minecraftClient.player || RotationManager.flag) ? livingEntity.headYaw : rotations().getRawYaw();
    }

    @Redirect(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;prevPitch:F"))
    private float redirectPrevPitch(LivingEntity livingEntity) {
        return (livingEntity != minecraftClient.player || RotationManager.flag) ? livingEntity.prevPitch : rotations().getPrevPitch();
    }

    @Redirect(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float redirectGetPitch(LivingEntity livingEntity) {
        return (livingEntity != minecraftClient.player || RotationManager.flag) ? livingEntity.getPitch() : rotations().getPitch();
    }

    @Inject(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = {@At("HEAD")})
    private void captureRenderedEntity(T t, float f, float f2, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo) {
        this.lastEntity = t;
    }

    @Inject(method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at = {@At("HEAD")}, cancellable = true)
    private void render(T t, float f, float f2, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        int intValue = norender_lcl.wardenDistance.getValue().intValue();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if ((t instanceof WardenEntity) && norender_lcl.isToggled() && norender_lcl.wardens.getValue().booleanValue() && t.getBlockPos().getSquaredDistance(minecraftClient.gameRenderer.getCamera().getBlockPos()) > intValue * intValue) {
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = {"getOverlay"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/OverlayTexture;getV(Z)I")})
    private static int getV(int i) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return i;

        if (norender_lcl.isToggled() && norender_lcl.hurt.getValue().booleanValue()) {
            return 10;
        }
        return i;
    }

    private RotationManager rotations() {
        return ClientServices.RotationManager;
    }
}
