package me.mioclient.mixin;

import me.mioclient.event.events.player.MoveSpeedEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.math.MathConstants;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import me.mioclient.ducks.DuckEntityCamera;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.event.events.player.MoveSpeedEvent;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.NoSlow;
import me.mioclient.module.movement.Velocity;
import me.mioclient.module.player.Freecam;
import me.mioclient.module.render.FreeLook;
import me.mioclient.module.render.NoRender;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({Entity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinEntity.class */
public abstract class MixinEntity implements DuckEntityCamera {

    @Shadow
    private float field_6031;

    @Shadow
    private float field_5965;

    @Unique
    private float cameraPitch;

    @Unique
    private float cameraYaw;

    @Unique
    private Vec3d mio$prevVelocity;
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

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
    private static Velocity velo;

    private static Velocity getVelo() {

        if (velo == null && ClientServices.moduleInjector != null) {

            try { velo = (Velocity) ClientServices.moduleInjector.getModule(Velocity.class); } catch (Exception e) {}

        }

        return velo;

    }
    private static FreeLook fl;

    private static FreeLook getFl() {

        if (fl == null && ClientServices.moduleInjector != null) {

            try { fl = (FreeLook) ClientServices.moduleInjector.getModule(FreeLook.class); } catch (Exception e) {}

        }

        return fl;

    }
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Shadow
    private Vec3d method_17835(Vec3d vec3d) {
        return null;
    }

    @Shadow
    public abstract void method_5784(MovementType movementType, Vec3d vec3d);

    @Shadow
    public abstract boolean equals(Object obj);

    @Shadow
    public abstract void method_33574(Vec3d vec3d);

    @Shadow
    public abstract void method_5814(double d, double d2, double d3);

    @Shadow
    protected abstract void method_5710(float f, float f2);

    @Shadow
    public abstract Vec3d method_18798();

    @Shadow
    public abstract boolean method_41328(EntityPose entityPose);

    @ModifyReceiver(method = {"getVelocityMultiplier"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getVelocityMultiplier()F")}, require = 0)
    private Block getVelocityMultiplierHook(Block block) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return null;

        return ((Object) this) != MinecraftClient.getInstance().player ? block : (noslow_lcl.isToggled() && noslow_lcl.soulSand.getValue().booleanValue()) ? Blocks.STONE : block;
    }

    @Inject(method = {"pushAwayFrom"}, at = {@At("HEAD")}, cancellable = true)
    private void pushAwayFromHook(Entity entity, CallbackInfo callbackInfo) {
        Velocity velo_lcl = getVelo();

        if (velo_lcl == null) return;

        if (equals(MinecraftClient.getInstance().player) && velo_lcl.isToggled() && velo_lcl.push.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method = {"updateMovementInFluid"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;getVelocity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d updateMovementInFluidHook(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
        Velocity velo_lcl = getVelo();

        if (velo_lcl == null) return fluidState.getVelocity(blockView, blockPos);

        return (velo_lcl.isToggled() && velo_lcl.liquids.getValue().booleanValue()) ? Vec3d.ZERO : fluidState.getVelocity(blockView, blockPos);
    }

    @Redirect(method = {"move"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d moveHook(Entity entity, Vec3d vec3d) {
        if (((Object) this) != MinecraftClient.getInstance().player) {
            return method_17835(vec3d);
        }
        MoveSpeedEvent moveEvent_2 = new MoveSpeedEvent(KeyPearlMode.Pre, 0.6f);
        ClientAccessor.eventBus.fireEvent(moveEvent_2);
        EntityUtil.setStepHeight((LivingEntity)(Object) this, moveEvent_2.getSpeed());
        Vec3d method_17835 = method_17835(vec3d);
        if (method_17835 != null) {
            ClientAccessor.eventBus.fireEvent(new MoveSpeedEvent(KeyPearlMode.Post, (float) method_17835.y));
        }
        return method_17835;
    }

    @Inject(method = {"getPose"}, at = {@At("HEAD")}, cancellable = true)
    private void getPoseHook(CallbackInfoReturnable<EntityPose> callbackInfoReturnable) {
        ElytraFly elytrafly_lcl = getElytrafly();

        if (elytrafly_lcl == null) return;

        if (((Object) this) != MinecraftClient.getInstance().player) {
            return;
        }
        if (elytrafly_lcl.isPacketFlyActive() || elytrafly_lcl.shouldSwapElytra()) {
            callbackInfoReturnable.setReturnValue(EntityPose.STANDING);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"changeLookDirection"}, at = {@At("HEAD")}, cancellable = true)
    public void changeCameraLookDirection(double d, double d2, CallbackInfo callbackInfo) {
        Freecam freecam_lcl = getFreecam();
        FreeLook fl_lcl = getFl();

        if (freecam_lcl == null) return;

        if (fl_lcl == null) return;

        if (!freecam_lcl.isToggled() && fl_lcl.isToggled() && equals(MinecraftClient.getInstance().player)) {
            this.cameraPitch = MathHelper.clamp(this.cameraPitch + ((float) (d2 * 0.15d)), -MathConstants.num2, MathConstants.num2);
            this.cameraYaw += (float) (d * 0.15d);
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"lerpPosAndRotation"}, at = {@At("HEAD")}, cancellable = true)
    private void lerpPosAndRotationHook(int i, double d, double d2, double d3, double d4, double d5, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.interpolation.getValue().booleanValue()) {
            method_5814(d, d2, d3);
            method_5710((float) d4, (float) d5);
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = {"getCameraPosVec"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStandingEyeHeight()F")})
    private float getHeightHook(float f) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return f;

        if (norender_lcl.isToggled() && norender_lcl.newSneaking.getValue().booleanValue() && method_41328(EntityPose.CROUCHING)) {
            return 1.54f;
        }
        return f;
    }

    @Inject(method = {"baseTick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER)})
    private void baseTick(CallbackInfo callbackInfo) {
        this.mio$prevVelocity = method_18798();
    }

    @Inject(method = {"<init>"}, at = {@At("TAIL")})
    private void init(EntityType entityType, World world, CallbackInfo callbackInfo) {
        this.mio$prevVelocity = Vec3d.ZERO;
    }

    @Override // me.mioclient.ducks.DuckEntityCamera
    @Unique
    public float getCameraPitch() {
        return this.cameraPitch;
    }

    @Override // me.mioclient.ducks.DuckEntityCamera
    @Unique
    public float getCameraYaw() {
        return this.cameraYaw;
    }

    @Override // me.mioclient.ducks.DuckEntityCamera
    @Unique
    public void setCameraPitch(float f) {
        this.cameraPitch = f;
    }

    @Override // me.mioclient.ducks.DuckEntityCamera
    @Unique
    public void setCameraYaw(float f) {
        this.cameraYaw = f;
    }

    @Override // me.mioclient.ducks.DuckEntityCamera
    public Vec3d mio$getPrevVelocity() {
        return this.mio$prevVelocity;
    }
}
