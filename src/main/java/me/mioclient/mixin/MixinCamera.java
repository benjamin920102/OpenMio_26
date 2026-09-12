package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.RenderUtil;

import me.mioclient.ducks.DuckEntityCamera;
import me.mioclient.event.events.player.UpdateSetPosEvent;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.player.Freecam;
import me.mioclient.module.render.FreeLook;
import me.mioclient.module.render.NoRender;
import me.mioclient.module.render.ViewClip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin({Camera.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinCamera.class */
public abstract class MixinCamera {
    private static ViewClip viewclip;

    private static ViewClip getViewclip() {

        if (viewclip == null && ClientServices.moduleInjector != null) {

            try { viewclip = (ViewClip) ClientServices.moduleInjector.getModule(ViewClip.class); } catch (Exception e) {}

        }

        return viewclip;

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
    private static Freecam freecam;

    private static Freecam getFreecam() {

        if (freecam == null && ClientServices.moduleInjector != null) {

            try { freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class); } catch (Exception e) {}

        }

        return freecam;

    }
    private static FreeLook fl;

    private static FreeLook getFl() {

        if (fl == null && ClientServices.moduleInjector != null) {

            try { fl = (FreeLook) ClientServices.moduleInjector.getModule(FreeLook.class); } catch (Exception e) {}

        }

        return fl;

    }

    @Shadow
    @Final
    private Vector3f field_18714;

    @Shadow
    @Final
    private Vector3f field_18715;

    @Shadow
    @Final
    private Vector3f field_18716;

    @Shadow
    private Vec3d field_18712;

    @Shadow
    private float field_18721;

    @Shadow
    private float field_18722;

    @Shadow
    private Entity field_18711;

    @Unique
    private boolean hold = true;

    @Shadow
    protected void method_19322(Vec3d vec3d) {
    }

    @Shadow
    protected abstract void method_19325(float f, float f2);

    @Inject(method = {"clipToSpace"}, at = {@At("HEAD")}, cancellable = true)
    private void clipToSpaceHook(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
        ViewClip viewclip_lcl = getViewclip();

        if (viewclip_lcl == null) return;

        if (viewclip_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf(viewclip_lcl.range.getValue().floatValue()));
        }
    }

    @Inject(method = {"updateEyeHeight"}, at = {@At("HEAD")}, cancellable = true)
    private void updateEyeHeightHook(CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.newSneaking.getValue().booleanValue() && this.field_18711 != null) {
            callbackInfo.cancel();
            this.field_18722 = this.field_18721;
            PlayerEntity playerEntity = (this.field_18711) instanceof PlayerEntity ? (PlayerEntity) (this.field_18711) : null;
            if (playerEntity instanceof PlayerEntity) {
                PlayerEntity playerEntity2 = playerEntity;
                if (playerEntity2.isInPose(EntityPose.CROUCHING)) {
                    this.field_18721 = 1.54f;
                } else if (this.field_18721 >= 1.62f || !playerEntity2.isInPose(EntityPose.STANDING)) {
                    this.field_18721 += (this.field_18711.getStandingEyeHeight() - this.field_18721) * 0.5f;
                } else {
                    this.field_18721 = 1.62f - ((float) ((1.62f - this.field_18721) * 0.4d));
                }
            }
        }
    }

    @Inject(method = {"moveBy"}, at = {@At("HEAD")}, cancellable = true)
    private void moveByHook(float f, float f2, float f3, CallbackInfo callbackInfo) {
        ViewClip viewclip_lcl = getViewclip();

        if (viewclip_lcl == null) return;

        if (viewclip_lcl.isToggled() && viewclip_lcl.smooth.getValue().booleanValue()) {
            float f4 = viewclip_lcl.progress.getValue();
            method_19322(new Vec3d(this.field_18712.x + (((this.field_18714.x() * f) + (this.field_18715.x() * f2) + (this.field_18716.x() * f3)) * f4), this.field_18712.y + (((this.field_18714.y() * f) + (this.field_18715.y() * f2) + (this.field_18716.y() * f3)) * f4), this.field_18712.z + (((this.field_18714.z() * f) + (this.field_18715.z() * f2) + (this.field_18716.z() * f3)) * f4)));
            callbackInfo.cancel();
        }
    }

    @ModifyArgs(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void onUpdateSetPosArgs(Args args) {
        UpdateSetPosEvent updateSetPosEvent = new UpdateSetPosEvent(((Double) args.get(0)).doubleValue(), ((Double) args.get(1)).doubleValue(), ((Double) args.get(2)).doubleValue());
        ClientAccessor.eventBus.fireEvent(updateSetPosEvent);
        if (updateSetPosEvent.isCancelled()) {
            args.set(0, Double.valueOf(updateSetPosEvent.toVec3d().x));
            args.set(1, Double.valueOf(updateSetPosEvent.toVec3d().y));
            args.set(2, Double.valueOf(updateSetPosEvent.toVec3d().z));
        }
    }

    @ModifyArgs(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(Args args) {
        Freecam freecam_lcl = getFreecam();
        ElytraFly elytrafly_lcl = getElytrafly();

        if (freecam_lcl == null) return;

        if (elytrafly_lcl == null) return;

        if (elytrafly_lcl.isToggled() && elytrafly_lcl.mode.getValue() == ElytraFly.ElytraFlyPredicateMode.BOUNCE && elytrafly_lcl.pitchLock.getValue().booleanValue() && !elytrafly_lcl.silent.getValue().booleanValue()) {
            args.set(1, elytrafly_lcl.pitch2.getValue());
        }
        float f = RenderUtil.getPartialTick();
        if (freecam_lcl.isToggled()) {
            args.set(0, Float.valueOf((float) freecam_lcl.get177(f)));
            args.set(1, Float.valueOf((float) freecam_lcl.get178(f)));
        }
    }

    @Inject(method = {"update"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0, shift = At.Shift.AFTER)})
    public void lockRotation(BlockView blockView, Entity entity, boolean z, boolean z2, float f, CallbackInfo callbackInfo) {
        Freecam freecam_lcl = getFreecam();
        FreeLook fl_lcl = getFl();

        if (freecam_lcl == null) return;

        if (fl_lcl == null) return;

        if (freecam_lcl.isToggled()) {
            return;
        }
        if (fl_lcl.isToggled() && (entity instanceof ClientPlayerEntity)) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            DuckEntityCamera cameraYawHelper = (DuckEntityCamera) entity;
            if (minecraftClient.player != null && this.hold) {
                cameraYawHelper.setCameraYaw(minecraftClient.player.getYaw());
                cameraYawHelper.setCameraPitch(minecraftClient.player.getPitch());
                this.hold = false;
            }
            method_19325(cameraYawHelper.getCameraYaw(), cameraYawHelper.getCameraPitch());
        }
        if (fl_lcl.isToggled() || !(entity instanceof ClientPlayerEntity)) {
            return;
        }
        this.hold = true;
    }
}
