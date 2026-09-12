package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.combat.ScaffoldHelperMode;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import me.mioclient.ducks.DuckClientPlayerEntityMovement;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.PushOutOfBlocksEvent;
import me.mioclient.event.events.player.SprintUpdateEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.player.TickMovementEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.misc.Swing;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.EntityControl;
import me.mioclient.module.movement.NoSlow;
import me.mioclient.module.movement.Sprint;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.mioclient.manager.RotationManager;
import me.mioclient.mode.combat.ScaffoldHelperMode;

/* compiled from: 0.java */
@Mixin({ClientPlayerEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinClientPlayerEntity.class */
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements ClientAccessor, DuckClientPlayerEntityMovement {
    private static ElytraFly elytrafly;

    private static ElytraFly getElytrafly() {

        if (elytrafly == null && ClientServices.moduleInjector != null) {

            try { elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class); } catch (Exception e) {}

        }

        return elytrafly;

    }
    private static Swing swing;

    private static Swing getSwing() {

        if (swing == null && ClientServices.moduleInjector != null) {

            try { swing = (Swing) ClientServices.moduleInjector.getModule(Swing.class); } catch (Exception e) {}

        }

        return swing;

    }
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }
    private static EntityControl entitycontrol;

    private static EntityControl getEntitycontrol() {

        if (entitycontrol == null && ClientServices.moduleInjector != null) {

            try { entitycontrol = (EntityControl) ClientServices.moduleInjector.getModule(EntityControl.class); } catch (Exception e) {}

        }

        return entitycontrol;

    }
    private static Sprint sprint;

    private static Sprint getSprint() {

        if (sprint == null && ClientServices.moduleInjector != null) {

            try { sprint = (Sprint) ClientServices.moduleInjector.getModule(Sprint.class); } catch (Exception e) {}

        }

        return sprint;

    }

    @Shadow
    public Input field_3913;

    @Unique
    private MotionEvent motionEvent;

    public MixinClientPlayerEntity(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
        this.motionEvent = new MotionEvent();
    }

    @Shadow
    protected abstract void method_3148(float f, float f2);

    @Shadow
    public abstract float getPitch(float f);

    @Shadow
    public abstract float getYaw(float f);

    @Shadow
    public abstract void method_33689();

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    private void method_3136() {
    }

    @Inject(method = {"swingHand"}, at = {@At("HEAD")}, cancellable = true)
    public void swingHandHook(Hand hand, CallbackInfo callbackInfo) {
        Swing swing_lcl = getSwing();

        if (swing_lcl == null) return;

        if (!swing_lcl.isToggled() || swing_lcl.hand.getValue() == Swing.ScaffoldHelperMode.VANILLA) {
            return;
        }
        if (swing_lcl.hand.getValue() != Swing.ScaffoldHelperMode.PACKET) {
            super.swingHand(swing_lcl.hand.getValue().getHand());
        }
        minecraftClient.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
        callbackInfo.cancel();
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE)})
    public void tickPost(CallbackInfo callbackInfo) {
        if (minecraftClient.player == null || minecraftClient.world == null) {
            return;
        }
        eventBus.fireEvent(new TickPostEvent());
    }

    @Inject(method = {"tick"}, at = {@At("RETURN")})
    public void tick(CallbackInfo callbackInfo) {
        if (minecraftClient.player == null || minecraftClient.world == null) {
            return;
        }
        eventBus.fireEvent(new TickEvent());
    }

    @Inject(method = {"move"}, at = {@At("HEAD")}, cancellable = true)
    private void move(MovementType movementType, Vec3d vec3d, CallbackInfo callbackInfo) {
        Vec3d vec3d2 = new Vec3d(vec3d.x, vec3d.y, vec3d.z);
        MoveEvent moveEvent = new MoveEvent(vec3d, movementType);
        eventBus.fireEvent(moveEvent);
        if (moveEvent.isCancelled()) {
            callbackInfo.cancel();
            return;
        }
        if (vec3d2.equals(moveEvent.getMovement())) {
            return;
        }
        callbackInfo.cancel();
        double x = getX();
        double z = getZ();
        super.move(moveEvent.getMovementType(), moveEvent.getMovement());
        method_3148((float) (getX() - x), (float) (getZ() - z));
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER)}, cancellable = true)
    private void tickPreHook(CallbackInfo callbackInfo) {
        resetEvent();
        eventBus.fireEvent(this.motionEvent);
        setPosition(this.motionEvent.getX(), this.motionEvent.getY(), this.motionEvent.getZ());
        setYaw(this.motionEvent.getRotationYaw());
        setPitch(this.motionEvent.getRotationPitch());
        setOnGround(this.motionEvent.isOnGround());
        if (this.motionEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", shift = At.Shift.BEFORE)}, cancellable = true)
    private void tickPostHook(CallbackInfo callbackInfo) {
        if (this.motionEvent.getX() != this.motionEvent.getOriginalX() || this.motionEvent.getY() != this.motionEvent.getOriginalY() || this.motionEvent.getZ() != this.motionEvent.getOriginalZ()) {
            setPosition(new Vec3d(this.motionEvent.getOriginalX(), this.motionEvent.getOriginalY(), this.motionEvent.getOriginalZ()));
        }
        resetRotations();
        if (isOnGround() == this.motionEvent.isOnGround()) {
            setOnGround(this.motionEvent.isPreMotion());
        }
    }

    @Inject(method = {"sendMovementPackets"}, at = {@At("RETURN")})
    private void sendMovementPacketsPostHook(CallbackInfo callbackInfo) {
        MotionEvent motionEvent = new MotionEvent(KeyPearlMode.Post, this.motionEvent);
        if (this.motionEvent.isCancelled()) {
            motionEvent.cancel();
        }
        eventBus.fireEvent(motionEvent);
    }

    @ModifyExpressionValue(method = {"sendMovementPackets"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D")})
    private double getXHook(double d) {
        return this.motionEvent.getX();
    }

    @ModifyExpressionValue(method = {"sendMovementPackets"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D")})
    private double getYHook(double d) {
        return this.motionEvent.getY();
    }

    @ModifyExpressionValue(method = {"sendMovementPackets"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D")})
    private double getZHook(double d) {
        return this.motionEvent.getZ();
    }

    @ModifyExpressionValue(method = {"sendMovementPackets"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F")})
    private float getYawHook(float f) {
        if (this.motionEvent.getOriginalYaw() == f && this.motionEvent.getOriginalYaw() != this.motionEvent.getYaw()) {
            return this.motionEvent.getYaw();
        }
        return f;
    }

    @ModifyExpressionValue(method = {"sendMovementPackets"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F")})
    private float getPitchHook(float f) {
        if (this.motionEvent.getOriginalPitch() == f && this.motionEvent.getOriginalPitch() != this.motionEvent.getPitch()) {
            return this.motionEvent.getPitch();
        }
        return f;
    }

    @ModifyExpressionValue(method = {"sendMovementPackets"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z")})
    private boolean isOnGroundHook(boolean z) {
        return this.motionEvent.isOnGround();
    }

    @Redirect(method = {"tickMovement"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V", ordinal = 3), require = 0)
    private void setSprintingHook(ClientPlayerEntity clientPlayerEntity, boolean z) {
        Sprint sprint_lcl = getSprint();

        if (sprint_lcl == null) return;

        clientPlayerEntity.setSprinting(sprint_lcl.isToggled() && sprint_lcl.is2181());
    }

    @Inject(method = {"tickMovement"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canStartSprinting()Z")})
    private void onSprintUpdate(CallbackInfo callbackInfo) {
        eventBus.fireEvent(new SprintUpdateEvent());
    }

    @ModifyExpressionValue(method = {"tickMovement"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z")})
    private boolean tickMovementHook(boolean z) {
        TickMovementEvent tickMovementEvent = new TickMovementEvent();
        eventBus.fireEvent(tickMovementEvent);
        if (tickMovementEvent.isCancelled()) {
            return false;
        }
        return z;
    }

    @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
    private void pushOutOfBlocksHook(double d, double d2, CallbackInfo callbackInfo) {
        PushOutOfBlocksEvent pushOutOfBlocksEvent = new PushOutOfBlocksEvent();
        eventBus.fireEvent(pushOutOfBlocksEvent);
        if (pushOutOfBlocksEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"getMountJumpStrength"}, at = {@At("HEAD")}, cancellable = true)
    private void getJumpMountStrengthHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        EntityControl entitycontrol_lcl = getEntitycontrol();

        if (entitycontrol_lcl == null) return;

        if (entitycontrol_lcl.isToggled() && entitycontrol_lcl.horseJump.getValue().booleanValue()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf(1.0f));
        }
    }

    @Inject(method = {"tickNausea"}, at = {@At("HEAD")})
    private void updateNauseaHook(CallbackInfo callbackInfo) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return;

        if (noslow_lcl.isToggled() && noslow_lcl.portals.getValue().booleanValue() && this.portalManager != null) {
            this.portalManager.setInPortal(false);
        }
    }

    @Inject(method = {"tickMovement"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isFallFlying()Z", shift = At.Shift.BEFORE)})
    private void tickMovementElytra(CallbackInfo callbackInfo) {
        ElytraFly elytrafly_lcl = getElytrafly();

        if (elytrafly_lcl == null) return;

        if (elytrafly_lcl.isToggled() && elytrafly_lcl.hasElytra()) {
            boolean z = minecraftClient.player.input.jumping;
            if ((elytrafly_lcl.mode.getValue() != ElytraFly.ElytraFlyPredicateMode.PACKET || (elytrafly_lcl.vertical2.getValue().booleanValue() && minecraftClient.player.input.jumping)) && z) {
                elytrafly_lcl.sendStartFallFlying();
            } else {
                ClientServices.holeSnapHelper.releaseTickRate(elytrafly);
            }
        }
    }

    @Inject(method = {"shouldSlowDown"}, at = {@At("HEAD")}, cancellable = true)
    private void shouldSlowdown(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return;

        boolean z = minecraftClient.player.isCrawling() && noslow_lcl.crawl.getValue().booleanValue();
        boolean z2 = !z && minecraftClient.player.isSneaking() && noslow_lcl.sneak.getValue().booleanValue();
        if (noslow_lcl.isToggled()) {
            if (z || z2) {
                callbackInfoReturnable.setReturnValue(false);
            }
        }
    }

    @Override // me.mioclient.mixin.ducks.PlayerMovementMixin
    public void sendMovementPacketsWrapper() {
        method_3136();
    }

    @Override // me.mioclient.mixin.ducks.PlayerMovementMixin
    public void superTick() {
        super.tick();
    }

    @Override // me.mioclient.mixin.ducks.PlayerMovementMixin
    public void resetEvent() {
        this.motionEvent = new MotionEvent(KeyPearlMode.Pre, getX(), getBoundingBox().minY, getZ(), getYaw(), getPitch(), isOnGround());
    }

    @Override // me.mioclient.mixin.ducks.PlayerMovementMixin
    public void resetRotations() {
        ClientServices.RotationManager.applyRotationsOnce((!hasVehicle() || this.motionEvent.hasModifiedRotation()) ? this.motionEvent.getYaw() : getVehicle().getYaw(), this.motionEvent.getPitch());
        if (getYaw() == this.motionEvent.getRotationYaw()) {
            setYaw(this.motionEvent.getOriginalYaw());
        }
        if (getPitch() == this.motionEvent.getRotationPitch()) {
            setPitch(this.motionEvent.getOriginalPitch());
        }
    }
}
