package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.combat.ScaffoldHelperMode;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import me.mioclient.ducks.DuckClientPlayerEntityMovement;
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
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.mioclient.manager.RotationManager;

/* 26.2 port: Yarn LocalPlayer -> MojMap LocalPlayer. sendMovementPackets is now
   private sendPosition(); Yarn tickMovement is now aiStep(); setYaw/setPitch/getYaw/getPitch
   and isOnGround are setYRot/setXRot/getYRot/getXRot and onGround(); input is ClientInput
   (jump state via input.keyPresses.jump()); connection.sendPacket is connection.send. */
@Mixin({LocalPlayer.class})
public abstract class MixinClientPlayerEntity extends AbstractClientPlayer implements ClientAccessor, DuckClientPlayerEntityMovement {
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
    public ClientInput input;

    @Unique
    private MotionEvent motionEvent;

    public MixinClientPlayerEntity(ClientLevel clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
        this.motionEvent = new MotionEvent();
    }

    @Shadow
    protected abstract void updateAutoJump(float f, float f2);

    @Shadow
    private void sendPosition() {
    }

    @Inject(method = {"swing"}, at = {@At("HEAD")}, cancellable = true)
    public void swingHandHook(InteractionHand hand, CallbackInfo callbackInfo) {
        Swing swing_lcl = getSwing();

        if (swing_lcl == null) return;

        if (!swing_lcl.isToggled() || swing_lcl.hand.getValue() == Swing.ScaffoldHelperMode.VANILLA) {
            return;
        }
        if (swing_lcl.hand.getValue() != Swing.ScaffoldHelperMode.PACKET) {
            super.swing(swing_lcl.hand.getValue().getHand());
        }
        minecraftClient.player.connection.send(new ServerboundSwingPacket(hand));
        callbackInfo.cancel();
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V", shift = At.Shift.BEFORE)})
    public void tickPost(CallbackInfo callbackInfo) {
        if (minecraftClient.player == null || minecraftClient.level == null) {
            return;
        }
        eventBus.fireEvent(new TickPostEvent());
    }

    @Inject(method = {"tick"}, at = {@At("RETURN")})
    public void tick(CallbackInfo callbackInfo) {
        if (minecraftClient.player == null || minecraftClient.level == null) {
            return;
        }
        eventBus.fireEvent(new TickEvent());
    }

    @Inject(method = {"move"}, at = {@At("HEAD")}, cancellable = true)
    private void move(MoverType movementType, Vec3 vec3d, CallbackInfo callbackInfo) {
        Vec3 vec3d2 = new Vec3(vec3d.x, vec3d.y, vec3d.z);
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
        updateAutoJump((float) (getX() - x), (float) (getZ() - z));
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V", shift = At.Shift.AFTER)}, cancellable = true)
    private void tickPreHook(CallbackInfo callbackInfo) {
        resetEvent();
        eventBus.fireEvent(this.motionEvent);
        setPos(this.motionEvent.getX(), this.motionEvent.getY(), this.motionEvent.getZ());
        setYRot(this.motionEvent.getRotationYaw());
        setXRot(this.motionEvent.getRotationPitch());
        setOnGround(this.motionEvent.isOnGround());
        if (this.motionEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", shift = At.Shift.BEFORE)}, cancellable = true)
    private void tickPostHook(CallbackInfo callbackInfo) {
        if (this.motionEvent.getX() != this.motionEvent.getOriginalX() || this.motionEvent.getY() != this.motionEvent.getOriginalY() || this.motionEvent.getZ() != this.motionEvent.getOriginalZ()) {
            setPos(new Vec3(this.motionEvent.getOriginalX(), this.motionEvent.getOriginalY(), this.motionEvent.getOriginalZ()));
        }
        resetRotations();
        if (onGround() == this.motionEvent.isOnGround()) {
            setOnGround(this.motionEvent.isPreMotion());
        }
    }

    @Inject(method = {"sendPosition"}, at = {@At("RETURN")})
    private void sendMovementPacketsPostHook(CallbackInfo callbackInfo) {
        MotionEvent motionEvent = new MotionEvent(KeyPearlMode.Post, this.motionEvent);
        if (this.motionEvent.isCancelled()) {
            motionEvent.cancel();
        }
        eventBus.fireEvent(motionEvent);
    }

    @ModifyExpressionValue(method = {"sendPosition"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D")})
    private double getXHook(double d) {
        return this.motionEvent.getX();
    }

    @ModifyExpressionValue(method = {"sendPosition"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D")})
    private double getYHook(double d) {
        return this.motionEvent.getY();
    }

    @ModifyExpressionValue(method = {"sendPosition"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D")})
    private double getZHook(double d) {
        return this.motionEvent.getZ();
    }

    @ModifyExpressionValue(method = {"sendPosition"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F")})
    private float getYawHook(float f) {
        if (this.motionEvent.getOriginalYaw() == f && this.motionEvent.getOriginalYaw() != this.motionEvent.getYaw()) {
            return this.motionEvent.getYaw();
        }
        return f;
    }

    @ModifyExpressionValue(method = {"sendPosition"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F")})
    private float getPitchHook(float f) {
        if (this.motionEvent.getOriginalPitch() == f && this.motionEvent.getOriginalPitch() != this.motionEvent.getPitch()) {
            return this.motionEvent.getPitch();
        }
        return f;
    }

    @ModifyExpressionValue(method = {"sendPosition"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;onGround()Z")})
    private boolean isOnGroundHook(boolean z) {
        return this.motionEvent.isOnGround();
    }

    @Redirect(method = {"aiStep"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setSprinting(Z)V", ordinal = 3), require = 0)
    private void setSprintingHook(LocalPlayer clientPlayerEntity, boolean z) {
        Sprint sprint_lcl = getSprint();

        if (sprint_lcl == null) return;

        clientPlayerEntity.setSprinting(sprint_lcl.isToggled() && sprint_lcl.is2181());
    }

    @Inject(method = {"aiStep"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;canStartSprinting()Z")})
    private void onSprintUpdate(CallbackInfo callbackInfo) {
        eventBus.fireEvent(new SprintUpdateEvent());
    }

    /* 26.2 port: aiStep no longer calls isUsingItem — item slowdown now flows
       through LocalPlayer.isSlowDueToUsing (bytecode-verified owner). Same
       cancel-to-false semantics for NoSlow's TickMovementEvent. */
    @ModifyExpressionValue(method = {"aiStep"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSlowDueToUsingItem()Z")})
    private boolean tickMovementHook(boolean z) {
        TickMovementEvent tickMovementEvent = new TickMovementEvent();
        eventBus.fireEvent(tickMovementEvent);
        if (tickMovementEvent.isCancelled()) {
            return false;
        }
        return z;
    }

    @Inject(method = {"moveTowardsClosestSpace"}, at = {@At("HEAD")}, cancellable = true)
    private void pushOutOfBlocksHook(double d, double d2, CallbackInfo callbackInfo) {
        PushOutOfBlocksEvent pushOutOfBlocksEvent = new PushOutOfBlocksEvent();
        eventBus.fireEvent(pushOutOfBlocksEvent);
        if (pushOutOfBlocksEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"aiStep"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isFallFlying()Z", shift = At.Shift.BEFORE)})
    private void tickMovementElytra(CallbackInfo callbackInfo) {
        ElytraFly elytrafly_lcl = getElytrafly();

        if (elytrafly_lcl == null) return;

        if (elytrafly_lcl.isToggled() && elytrafly_lcl.hasElytra()) {
            boolean z = minecraftClient.player.input.keyPresses.jump();
            if ((elytrafly_lcl.mode.getValue() != ElytraFly.ElytraFlyPredicateMode.PACKET || (elytrafly_lcl.vertical2.getValue().booleanValue() && minecraftClient.player.input.keyPresses.jump())) && z) {
                elytrafly_lcl.sendStartFallFlying();
            } else {
                ClientServices.holeSnapHelper.releaseTickRate(elytrafly);
            }
        }
    }

    @Override
    public void sendMovementPacketsWrapper() {
        sendPosition();
    }

    @Override
    public void superTick() {
        super.tick();
    }

    @Override
    public void resetEvent() {
        this.motionEvent = new MotionEvent(KeyPearlMode.Pre, getX(), getBoundingBox().minY, getZ(), getYRot(), getXRot(), onGround());
    }

    @Override
    public void resetRotations() {
        ClientServices.RotationManager.applyRotationsOnce((!(getVehicle() != null) || this.motionEvent.hasModifiedRotation()) ? this.motionEvent.getYaw() : getVehicle().getYRot(), this.motionEvent.getPitch());
        if (getYRot() == this.motionEvent.getRotationYaw()) {
            setYRot(this.motionEvent.getOriginalYaw());
        }
        if (getXRot() == this.motionEvent.getRotationPitch()) {
            setXRot(this.motionEvent.getOriginalPitch());
        }
    }
}
