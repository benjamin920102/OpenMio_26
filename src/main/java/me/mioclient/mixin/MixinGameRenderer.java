package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.ShaderFramebufferSwapper;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.concurrent.LockedTaskRunner;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.ShaderFramebufferSwapper;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.concurrent.LockedTaskRunner;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.Vec3dUtils;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.mioclient.module.exploit.Reach;
import me.mioclient.module.player.Freecam;
import me.mioclient.module.render.FreeLook;
import me.mioclient.module.render.NoBob;
import me.mioclient.module.render.NoRender;
import me.mioclient.module.render.Shader;
import me.mioclient.module.render.ViewModel;
import me.mioclient.module.render.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({GameRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinGameRenderer.class */
public abstract class MixinGameRenderer {
    private static Zoom zoom;

    private static Zoom getZoom() {

        if (zoom == null && ClientServices.moduleInjector != null) {

            try { zoom = (Zoom) ClientServices.moduleInjector.getModule(Zoom.class); } catch (Exception e) {}

        }

        return zoom;

    }
    private static Reach reach;

    private static Reach getReach() {

        if (reach == null && ClientServices.moduleInjector != null) {

            try { reach = (Reach) ClientServices.moduleInjector.getModule(Reach.class); } catch (Exception e) {}

        }

        return reach;

    }
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static Shader shader;

    private static Shader getShader() {

        if (shader == null && ClientServices.moduleInjector != null) {

            try { shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class); } catch (Exception e) {}

        }

        return shader;

    }
    private static Freecam freecam;

    private static Freecam getFreecam() {

        if (freecam == null && ClientServices.moduleInjector != null) {

            try { freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class); } catch (Exception e) {}

        }

        return freecam;

    }
    private static FreeLook freelook;

    private static FreeLook getFreelook() {

        if (freelook == null && ClientServices.moduleInjector != null) {

            try { freelook = (FreeLook) ClientServices.moduleInjector.getModule(FreeLook.class); } catch (Exception e) {}

        }

        return freelook;

    }
    private static ViewModel viewmodel;

    private static ViewModel getViewmodel() {

        if (viewmodel == null && ClientServices.moduleInjector != null) {

            try { viewmodel = (ViewModel) ClientServices.moduleInjector.getModule(ViewModel.class); } catch (Exception e) {}

        }

        return viewmodel;

    }
    private static NoBob nobob;

    private static NoBob getNobob() {

        if (nobob == null && ClientServices.moduleInjector != null) {

            try { nobob = (NoBob) ClientServices.moduleInjector.getModule(NoBob.class); } catch (Exception e) {}

        }

        return nobob;

    }

    @Shadow
    @Final
    MinecraftClient field_4015;

    @Shadow
    @Final
    private Camera field_18765;

    @Shadow
    private boolean field_3992;

    @Shadow
    @Final
    public HeldItemRenderer field_4012;

    @Shadow
    @Final
    private BufferBuilderStorage field_20948;

    @Unique
    private boolean bobbing;

    @Unique
    private boolean prevBobbing;
    private boolean freecamSet = false;

    @Shadow
    private void method_3172(Camera camera, float f, Matrix4f matrix4f) {
    }

    @Shadow
    public abstract void method_3182();

    @Shadow
    public abstract void method_3190(float f);

    @Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = 180, ordinal = 0)}, method = {"renderWorld"})
    private void renderWorldFieldHook(RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        if (this.bobbing) {
            this.field_4015.options.getBobView().setValue(true);
            this.bobbing = false;
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"renderWorld"})
    private void renderWorldPreHook(RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        this.prevBobbing = true;
    }

    @Inject(at = {@At("HEAD")}, method = {"bobView"}, cancellable = true)
    private void bobViewHook(MatrixStack matrixStack, float f, CallbackInfo callbackInfo) {
        if (((Boolean)(Object) this.field_4015.options.getBobView().getValue()).booleanValue() && this.prevBobbing) {
            this.bobbing = true;
            this.field_4015.options.getBobView().setValue(false);
            this.prevBobbing = false;
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tiltViewWhenHurt"}, at = {@At("HEAD")}, cancellable = true)
    private void bobViewWhenHurtHook(MatrixStack matrixStack, float f, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.hurtCam.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"showFloatingItem"}, at = {@At("HEAD")}, cancellable = true)
    private void showFloatingItemHook(ItemStack itemStack, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (itemStack.getItem() == Items.TOTEM_OF_UNDYING && norender_lcl.isToggled() && norender_lcl.totemOverlay.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @WrapOperation(method = {"renderWorld"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F")}, require = 0)
    private float blindnessAdjustedLerp(float f, float f2, float f3, Operation<Float> operation) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return 0.0f;

        if (norender_lcl.isToggled() && norender_lcl.blindness.getValue().booleanValue()) {
            return 0.0f;
        }
        return MathHelper.lerp(f, f2, f3);
    }

    @Inject(method = {"findCrosshairTarget"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;")}, cancellable = true)
    private void onUpdateTargetedEntity(Entity entity, double d, double d2, float f, CallbackInfoReturnable<HitResult> callbackInfoReturnable, @Local HitResult hitResult) {
        Reach reach_lcl = getReach();

        if (reach_lcl == null) return;

        if (reach_lcl.is951() && hitResult.getType() == HitResult.Type.BLOCK) {
            callbackInfoReturnable.setReturnValue(hitResult);
        }
    }

    @WrapWithCondition(method = {"renderHand"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V")})
    private boolean renderHand(HeldItemRenderer heldItemRenderer, float f, MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, ClientPlayerEntity clientPlayerEntity, int i) {
        Shader shader_lcl = getShader();

        // A @WrapWithCondition must return true to let the wrapped call run. Returning false on a
        // null module cancelled renderItem outright, so the held item never drew.
        if (shader_lcl == null || !ShaderPassRegistry.flag2) {
            return true;
        }
        ShaderFramebufferSwapper.swapAndRender(shader_lcl.shader.getValue().getShaderPass(), true, () -> {
            heldItemRenderer.renderItem(f, matrixStack, immediate, clientPlayerEntity, i);
        });
        return false;
    }

    @Inject(method = {"renderWorld"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V", shift = At.Shift.BEFORE)}, cancellable = true)
    private void renderWorldHook(RenderTickCounter renderTickCounter, CallbackInfo callbackInfo, @Local(ordinal = 1) Matrix4f matrix4f, @Local(ordinal = 0) float f, @Local(ordinal = 0) Quaternionf quaternionf) {
        if (this.field_3992 && getShader() != null && getShader().isToggled() && getShader().hands.getValue().booleanValue()) {
            callbackInfo.cancel();
            if (!getNorender().is179()) {
                method_3172(this.field_18765, f, matrix4f);
            }
            ShaderPassRegistry.flag2 = true;
            method_3172(this.field_18765, f, matrix4f);
            ShaderPassRegistry.flag2 = false;
            ClientAccessor.minecraftClient.getProfiler().pop();
        }
    }

    @Redirect(method = {"bobView"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void mio$redirectTranslate(MatrixStack matrixStack, float f, float f2, float f3) {
        float floatValue = getNobob().multiplier.getValue().floatValue();
        if (floatValue <= 0.0f || !getNobob().isToggled()) {
            floatValue = 1.0f;
        }
        if (((Boolean)(Object) this.field_4015.options.getBobView().getValue()).booleanValue()) {
            matrixStack.translate(f * floatValue, f2 * floatValue, f3 * floatValue);
        }
    }

    @Inject(method = {"renderHand"}, at = {@At("HEAD")}, cancellable = true)
    private void renderHandHook(Camera camera, float f, Matrix4f matrix4f, CallbackInfo callbackInfo) {
        Freecam freecam_lcl = getFreecam();
        NoRender norender_lcl = getNorender();
        ViewModel viewmodel_lcl = getViewmodel();

        if (freecam_lcl == null) return;

        if (norender_lcl == null) return;

        if (viewmodel_lcl == null) return;

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, norender_lcl.getHandOpacity());
        if (viewmodel_lcl.isToggled() && !viewmodel_lcl.shadow.getValue().booleanValue()) {
            float pitch = camera.getPitch() * MathConstants.val4;
            float f2 = (-(camera.getYaw() - 45.0f)) * MathConstants.val4;
            float cos = MathHelper.cos(f2);
            float sin = MathHelper.sin(f2);
            float cos2 = MathHelper.cos(pitch);
            Vector3f vector3f = new Vector3f(sin * cos2, -MathHelper.sin(pitch), cos * cos2);
            RenderSystem.setShaderLights(vector3f, new Vector3f(vector3f).mul(-1.0f));
        }
        if (freecam_lcl.is179() || norender_lcl.is179()) {
            callbackInfo.cancel();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @WrapOperation(method = {"renderHand"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getFov(Lnet/minecraft/client/render/Camera;FZ)D")}, require = 0)
    private double renderHandHook(GameRenderer gameRenderer, Camera camera, float f, boolean z, Operation<Double> operation) {
        Zoom zoom_lcl = getZoom();
        ViewModel viewmodel_lcl = getViewmodel();

        if (zoom_lcl == null) return 0.0d;

        if (viewmodel_lcl == null) return 0.0d;

        double base = ((Double) operation.call(new Object[]{gameRenderer, camera, Float.valueOf(f), Boolean.valueOf(z)})).doubleValue();
        return (viewmodel_lcl.isToggled() && viewmodel_lcl.viewModelFov.getValue().booleanValue()) ? viewmodel_lcl.fovAmount.getValue().intValue() * base : base * (zoom_lcl.isToggled() ? 1.0f - zoom_lcl.getSmoothProgress() : 1.0f);
    }

    @Inject(method = {"renderHand"}, at = {@At("RETURN")})
    private void resetShaderColor(Camera camera, float f, Matrix4f matrix4f, CallbackInfo callbackInfo) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Inject(method = {"updateCrosshairTarget"}, at = {@At("HEAD")}, cancellable = true)
    private void updateTargetedEntityInvoke(float f, CallbackInfo callbackInfo) {
        Freecam freecam_lcl = getFreecam();
        FreeLook freelook_lcl = getFreelook();

        if (freecam_lcl == null) return;

        if (freelook_lcl == null) return;

        if (freecam_lcl.isToggled() && this.field_4015.getCameraEntity() != null && !this.freecamSet) {
            callbackInfo.cancel();
            Entity cameraEntity = this.field_4015.getCameraEntity();
            double x = cameraEntity.getX();
            double y = cameraEntity.getY();
            double z = cameraEntity.getZ();
            double d = cameraEntity.prevX;
            double d2 = cameraEntity.prevY;
            double d3 = cameraEntity.prevZ;
            float yaw = cameraEntity.getYaw();
            float pitch = cameraEntity.getPitch();
            float f2 = cameraEntity.prevYaw;
            float f3 = cameraEntity.prevPitch;
            cameraEntity.prevX = freecam_lcl.vec3d2.x;
            cameraEntity.prevY = freecam_lcl.vec3d2.y - cameraEntity.getEyeHeight(cameraEntity.getPose());
            cameraEntity.prevZ = freecam_lcl.vec3d2.z;
            cameraEntity.setYaw(freecam_lcl.val);
            cameraEntity.setHeadYaw(freecam_lcl.val);
            cameraEntity.setPitch(freecam_lcl.val3);
            cameraEntity.prevYaw = freecam_lcl.val2;
            cameraEntity.prevPitch = freecam_lcl.val4;
            this.freecamSet = true;
            LockedTaskRunner.executeLocked(() -> {
                Vec3dUtils.setVectorComponents(cameraEntity.getPos(), freecam_lcl.vec3d.x, freecam_lcl.vec3d.y - cameraEntity.getEyeHeight(cameraEntity.getPose()), freecam_lcl.vec3d.z);
                method_3190(f);
                Vec3dUtils.setVectorComponents(cameraEntity.getPos(), x, y, z);
            });
            this.freecamSet = false;
            cameraEntity.prevX = d;
            cameraEntity.prevY = d2;
            cameraEntity.prevZ = d3;
            cameraEntity.setYaw(yaw);
            cameraEntity.setPitch(pitch);
            cameraEntity.prevYaw = f2;
            cameraEntity.prevPitch = f3;
        }
        if (!freelook_lcl.isToggled() || freecam_lcl.isToggled()) {
            return;
        }
        callbackInfo.cancel();
    }

    @Inject(method = {"render"}, at = {@At("HEAD")})
    private void render(RenderTickCounter renderTickCounter, boolean z, CallbackInfo callbackInfo) {
        ClientServices.hitmarkerHelper.updateFrameCount();
    }
}
