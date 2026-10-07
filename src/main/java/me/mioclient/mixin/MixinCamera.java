package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.render.RenderUtil;

import me.mioclient.ducks.DuckEntityCamera;
import me.mioclient.event.events.player.UpdateSetPosEvent;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.player.Freecam;
import me.mioclient.module.render.FreeLook;
import me.mioclient.module.render.NoRender;
import me.mioclient.module.render.ViewClip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
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

@Mixin({Camera.class})

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

    /* 26.2 port (verified via JDK25 javap): horizontalPlane/verticalPlane/
       diagonalPlane gone (now forwards/up/left); pos -> position;
       cameraY/lastCameraY -> eyeHeight/eyeHeightOld; focusedEntity -> entity;
       setPos(Vec3) -> setPosition(Vec3). Hooks kept lenient (require = 0). */
    @Shadow
    @Final
    private Vector3f forwards;

    @Shadow
    @Final
    private Vector3f up;

    @Shadow
    @Final
    private Vector3f left;

    @Shadow
    private Vec3 position;

    @Shadow
    private float eyeHeight;

    @Shadow
    private float eyeHeightOld;

    @Shadow
    private Entity entity;

    @Unique
    private boolean hold = true;

    @Shadow
    protected void setPosition(Vec3 vec3d) {
    }

    @Shadow
    protected abstract void setRotation(float f, float f2);

    @Inject(method = {"clipToSpace"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void clipToSpaceHook(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
        ViewClip viewclip_lcl = getViewclip();

        if (viewclip_lcl == null) return;

        if (viewclip_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf(viewclip_lcl.range.getValue().floatValue()));
        }
    }

    @Inject(method = {"updateEyeHeight"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void updateEyeHeightHook(CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.newSneaking.getValue().booleanValue() && this.entity != null) {
            callbackInfo.cancel();
            this.eyeHeightOld = this.eyeHeight;
            Player playerEntity = (this.entity) instanceof Player ? (Player) (this.entity) : null;
            if (playerEntity instanceof Player) {
                Player playerEntity2 = playerEntity;
                if (playerEntity2.getPose() == Pose.CROUCHING) {
                    this.eyeHeight = 1.54f;
                } else if (this.eyeHeight >= 1.62f || playerEntity2.getPose() != Pose.STANDING) {
                    this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5f;
                } else {
                    this.eyeHeight = 1.62f - ((float) ((1.62f - this.eyeHeight) * 0.4d));
                }
            }
        }
    }

    @Inject(method = {"moveBy"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void moveByHook(float f, float f2, float f3, CallbackInfo callbackInfo) {
        ViewClip viewclip_lcl = getViewclip();

        if (viewclip_lcl == null) return;

        if (viewclip_lcl.isToggled() && viewclip_lcl.smooth.getValue().booleanValue()) {
            float f4 = viewclip_lcl.progress.getValue();
            setPosition(new Vec3(this.position.x + (((this.forwards.x() * f) + (this.up.x() * f2) + (this.left.x() * f3)) * f4), this.position.y + (((this.forwards.y() * f) + (this.up.y() * f2) + (this.left.y() * f3)) * f4), this.position.z + (((this.forwards.z() * f) + (this.up.z() * f2) + (this.left.z() * f3)) * f4)));
            callbackInfo.cancel();
        }
    }

    @ModifyArgs(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"), require = 0)
    private void onUpdateSetPosArgs(Args args) {
        UpdateSetPosEvent updateSetPosEvent = new UpdateSetPosEvent(((Double) args.get(0)).doubleValue(), ((Double) args.get(1)).doubleValue(), ((Double) args.get(2)).doubleValue());
        ClientAccessor.eventBus.fireEvent(updateSetPosEvent);
        if (updateSetPosEvent.isCancelled()) {
            args.set(0, Double.valueOf(updateSetPosEvent.toVec3d().x));
            args.set(1, Double.valueOf(updateSetPosEvent.toVec3d().y));
            args.set(2, Double.valueOf(updateSetPosEvent.toVec3d().z));
        }
    }

    @ModifyArgs(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"), require = 0)
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

    @Inject(method = {"update"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0, shift = At.Shift.AFTER)}, require = 0)
    public void lockRotation(BlockGetter blockView, Entity entity, boolean z, boolean z2, float f, CallbackInfo callbackInfo) {
        Freecam freecam_lcl = getFreecam();
        FreeLook fl_lcl = getFl();

        if (freecam_lcl == null) return;

        if (fl_lcl == null) return;

        if (freecam_lcl.isToggled()) {
            return;
        }
        if (fl_lcl.isToggled() && (entity instanceof LocalPlayer)) {
            Minecraft minecraftClient = Minecraft.getInstance();
            DuckEntityCamera cameraYawHelper = (DuckEntityCamera) entity;
            if (minecraftClient.player != null && this.hold) {
                cameraYawHelper.setCameraYaw(minecraftClient.player.getYRot());
                cameraYawHelper.setCameraPitch(minecraftClient.player.getXRot());
                this.hold = false;
            }
            setRotation(cameraYawHelper.getCameraYaw(), cameraYawHelper.getCameraPitch());
        }
        if (fl_lcl.isToggled() || !(entity instanceof LocalPlayer)) {
            return;
        }
        this.hold = true;
    }
}
