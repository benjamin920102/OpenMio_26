package me.mioclient.mixin;

import me.mioclient.event.events.player.MoveSpeedEvent;
import net.minecraft.world.entity.MoverType;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import me.mioclient.ducks.DuckEntityCamera;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.NoSlow;
import me.mioclient.module.movement.Velocity;
import me.mioclient.module.player.Freecam;
import me.mioclient.module.render.FreeLook;
import me.mioclient.module.render.NoRender;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port (verified against 26.2 merged jar): MovementType is now MoverType;
   adjustMovementForCollisions is now private collide(Vec3); setYaw/setPitch are
   setYRot/setXRot; getCameraPosVec is getEyePosition; getStandingEyeHeight is
   getEyeHeight; pushAwayFrom is push; changeLookDirection is turn;
   getVelocityMultiplier hook moved to getBlockSpeedFactor/Block.getSpeedFactor;
   baseTick profiler is now profiling.ProfilerFiller. Removed: lerpPosAndRotation
   hook (method gone in 26.2), updateMovementInFluid hook (method gone in 26.2). */
@Mixin({Entity.class})
public abstract class MixinEntity implements DuckEntityCamera {

    @Unique
    private float cameraPitch;

    @Unique
    private float cameraYaw;

    @Unique
    private Vec3 mio$prevVelocity;
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
    private Vec3 collide(Vec3 vec3d) {
        return null;
    }

    @Shadow
    public abstract Pose getPose();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @ModifyReceiver(method = {"getBlockSpeedFactor"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getSpeedFactor()F")}, require = 0)
    private Block getVelocityMultiplierHook(Block block) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return null;

        return ((Object) this) != Minecraft.getInstance().player ? block : (noslow_lcl.isToggled() && noslow_lcl.soulSand.getValue().booleanValue()) ? Blocks.STONE : block;
    }

    @Inject(method = {"push"}, at = {@At("HEAD")}, cancellable = true)
    private void pushAwayFromHook(Entity entity, CallbackInfo callbackInfo) {
        Velocity velo_lcl = getVelo();

        if (velo_lcl == null) return;

        if (equals(Minecraft.getInstance().player) && velo_lcl.isToggled() && velo_lcl.push.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method = {"move"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 moveHook(Entity entity, Vec3 vec3d) {
        if (((Object) this) != Minecraft.getInstance().player) {
            return collide(vec3d);
        }
        MoveSpeedEvent moveEvent_2 = new MoveSpeedEvent(KeyPearlMode.Pre, 0.6f);
        ClientAccessor.eventBus.fireEvent(moveEvent_2);
        EntityUtil.setStepHeight((LivingEntity)(Object) this, moveEvent_2.getSpeed());
        Vec3 collided = collide(vec3d);
        if (collided != null) {
            ClientAccessor.eventBus.fireEvent(new MoveSpeedEvent(KeyPearlMode.Post, (float) collided.y));
        }
        return collided;
    }

    @Inject(method = {"getPose"}, at = {@At("HEAD")}, cancellable = true)
    private void getPoseHook(CallbackInfoReturnable<Pose> callbackInfoReturnable) {
        ElytraFly elytrafly_lcl = getElytrafly();

        if (elytrafly_lcl == null) return;

        if (((Object) this) != Minecraft.getInstance().player) {
            return;
        }
        if (elytrafly_lcl.isPacketFlyActive() || elytrafly_lcl.shouldSwapElytra()) {
            callbackInfoReturnable.setReturnValue(Pose.STANDING);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"turn"}, at = {@At("HEAD")}, cancellable = true)
    public void changeCameraLookDirection(double d, double d2, CallbackInfo callbackInfo) {
        Freecam freecam_lcl = getFreecam();
        FreeLook fl_lcl = getFl();

        if (freecam_lcl == null) return;

        if (fl_lcl == null) return;

        if (!freecam_lcl.isToggled() && fl_lcl.isToggled() && equals(Minecraft.getInstance().player)) {
            this.cameraPitch = Mth.clamp(this.cameraPitch + ((float) (d2 * 0.15d)), -MathConstants.num2, MathConstants.num2);
            this.cameraYaw += (float) (d * 0.15d);
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = {"getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getEyeHeight()F")})
    private float getHeightHook(float f) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return f;

        if (norender_lcl.isToggled() && norender_lcl.newSneaking.getValue().booleanValue() && getPose() == Pose.CROUCHING) {
            return 1.54f;
        }
        return f;
    }

    @Inject(method = {"baseTick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER)})
    private void baseTick(CallbackInfo callbackInfo) {
        this.mio$prevVelocity = getDeltaMovement();
    }

    @Inject(method = {"<init>"}, at = {@At("TAIL")})
    private void init(EntityType entityType, Level world, CallbackInfo callbackInfo) {
        this.mio$prevVelocity = Vec3.ZERO;
    }

    @Override
    @Unique
    public float getCameraPitch() {
        return this.cameraPitch;
    }

    @Override
    @Unique
    public void setCameraPitch(float f) {
        this.cameraPitch = f;
    }

    @Override
    @Unique
    public float getCameraYaw() {
        return this.cameraYaw;
    }

    @Override
    @Unique
    public void setCameraYaw(float f) {
        this.cameraYaw = f;
    }

    @Override
    public Vec3 mio$getPrevVelocity() {
        return this.mio$prevVelocity;
    }
}
