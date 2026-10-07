package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import net.minecraft.world.entity.MoverType;

import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.network.PacketSender;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mioclient.ducks.DuckLivingEntityServerDead;
import me.mioclient.ducks.DuckLivingEntity;
import me.mioclient.event.events.player.GetStatusEffectEvent;
import me.mioclient.event.events.player.TickPreEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.module.misc.Swing;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.Fireworks;
import me.mioclient.module.movement.Jesus;
import me.mioclient.module.movement.NoJumpDelay;
import me.mioclient.module.movement.Speed;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})

public abstract class MixinLivingEntity implements DuckLivingEntityServerDead, DuckLivingEntity {

    @Accessor("useItemRemaining")
    public abstract void setItemUseTimeLeft(int i);

    @Override
    public double mio$getServerX() {
        return ((LivingEntity)(Object) this).getPositionCodec().getBase().x;
    }

    @Override
    public double mio$getServerY() {
        return ((LivingEntity)(Object) this).getPositionCodec().getBase().y;
    }

    @Override
    public double mio$getServerZ() {
        return ((LivingEntity)(Object) this).getPositionCodec().getBase().z;
    }

    @Override
    public Vec3 mio$getServerPos() {
        return ((LivingEntity)(Object) this).getPositionCodec().getBase();
    }

    @Invoker("getCurrentSwingDuration")
    public abstract int getSwingDuration();

    @Shadow
    private int noJumpDelay;

    @Unique
    private TickPreEvent mio$moveEvent;

    @Unique
    private RotationEvent event;

    @Unique
    private boolean serverDead;

    @Unique
    private boolean prevFlying;

    @Unique
    private float prevLookYaw;

    @Unique
    private float prevLookPitch;

    @Unique
    private long prevTime;
    private static ElytraFly efly;

    private static ElytraFly getEfly() {

        if (efly == null && ClientServices.moduleInjector != null) {

            try { efly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class); } catch (Exception e) {}

        }

        return efly;

    }
    private static NoJumpDelay njd;

    private static NoJumpDelay getNjd() {

        if (njd == null && ClientServices.moduleInjector != null) {

            try { njd = (NoJumpDelay) ClientServices.moduleInjector.getModule(NoJumpDelay.class); } catch (Exception e) {}

        }

        return njd;

    }
    private static Jesus jesus;

    private static Jesus getJesus() {

        if (jesus == null && ClientServices.moduleInjector != null) {

            try { jesus = (Jesus) ClientServices.moduleInjector.getModule(Jesus.class); } catch (Exception e) {}

        }

        return jesus;

    }
    private static Swing swing;

    private static Swing getSwing() {

        if (swing == null && ClientServices.moduleInjector != null) {

            try { swing = (Swing) ClientServices.moduleInjector.getModule(Swing.class); } catch (Exception e) {}

        }

        return swing;

    }
    private static Speed speed;

    private static Speed getSpeedModule() {

        if (speed == null && ClientServices.moduleInjector != null) {

            try { speed = (Speed) ClientServices.moduleInjector.getModule(Speed.class); } catch (Exception e) {}

        }

        return speed;

    }
    private static Fireworks fireworks;

    private static Fireworks getFireworks() {

        if (fireworks == null && ClientServices.moduleInjector != null) {

            try { fireworks = (Fireworks) ClientServices.moduleInjector.getModule(Fireworks.class); } catch (Exception e) {}

        }

        return fireworks;

    }

    @Inject(method = {"getCurrentSwingDuration"}, at = {@At("HEAD")}, cancellable = true)
    private void getHandSwingDurationHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        Swing swing_lcl = getSwing();
        Speed speed_lcl = getSpeedModule();

        if (swing_lcl == null) return;

        if (speed_lcl == null) return;

        if (swing_lcl.isToggled()) {
            LivingEntity livingEntity = (LivingEntity)(Object) this;
            callbackInfoReturnable.cancel();
            int doubleValue = (int) ((swing_lcl.type.getValue() == Swing.MixinLivingEntityMode.ONE_EIGHT ? 7 : 6) / swing_lcl.speed.getValue().doubleValue());
            if (MobEffectUtil.hasDigSpeed(livingEntity)) {
                callbackInfoReturnable.setReturnValue(Integer.valueOf(doubleValue - (1 + MobEffectUtil.getDigSpeedAmplification(livingEntity))));
            } else {
                callbackInfoReturnable.setReturnValue(Integer.valueOf(livingEntity.hasEffect(MobEffects.MINING_FATIGUE) ? doubleValue + ((1 + livingEntity.getEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2) : doubleValue));
            }
        }
    }

    @Inject(method = {"isFallFlying"}, at = {@At("HEAD")}, cancellable = true)
    private void isFallFlyingHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Fireworks fireworks_lcl = getFireworks();
        ElytraFly efly_lcl = getEfly();

        if (fireworks_lcl == null) return;

        if (efly_lcl == null) return;

        if (ClientAccessor.isLocalPlayer(this)) {
            if (fireworks_lcl.isToggled() && fireworks_lcl.fastLaunch.getValue().booleanValue() && fireworks_lcl.getRemainingTicks() > 0) {
                callbackInfoReturnable.cancel();
                callbackInfoReturnable.setReturnValue(true);
            }
            if (efly_lcl.isPacketFlyActive() || efly_lcl.shouldSwapElytra()) {
                callbackInfoReturnable.setReturnValue(false);
                callbackInfoReturnable.cancel();
            }
        }
    }

    @Inject(method = {"canStandOnFluid"}, at = {@At("HEAD")}, cancellable = true)
    private void canWalkOnFluidHook(net.minecraft.world.level.material.FluidState fluidState, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Jesus jesus_lcl = getJesus();

        if (jesus_lcl == null) return;

        if (((Object) this) == Minecraft.getInstance().player && jesus_lcl.isToggled() && jesus_lcl.canJesus() && !Minecraft.getInstance().player.isInWater()) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = {"travel"}, at = {@At("HEAD")})
    private void travelPreHook(Vec3 vec3d, CallbackInfo callbackInfo) {
        if (((Object) this) != Minecraft.getInstance().player) {
            return;
        }
        ElytraFly.flag = true;
        this.prevLookYaw = ((LivingEntity)(Object) this).getYRot();
        this.prevLookPitch = ((LivingEntity)(Object) this).getXRot();
        this.event = new RotationEvent(this.prevLookYaw, this.prevLookPitch, false);
        ClientAccessor.eventBus.fireEvent(this.event);
        if (this.event.isCancelled()) {
            ((LivingEntity)(Object) this).setYRot(this.event.getYaw());
            ((LivingEntity)(Object) this).setXRot(this.event.getPitch());
        }
    }

    @Inject(method = {"travel"}, at = {@At("TAIL")})
    private void travelPostHook(Vec3 vec3d, CallbackInfo callbackInfo) {
        if (((Object) this) != Minecraft.getInstance().player) {
            return;
        }
        ElytraFly.flag = false;
        if (this.event.isCancelled()) {
            ((LivingEntity)(Object) this).setYRot(this.prevLookYaw);
            ((LivingEntity)(Object) this).setXRot(this.prevLookPitch);
        }
    }

    @ModifyExpressionValue(method = {"jumpFromGround"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F")})
    private float jumpFix(float f) {
        this.event = new RotationEvent(((LivingEntity)(Object) this).getYRot(), ((LivingEntity)(Object) this).getXRot(), true);
        ClientAccessor.eventBus.fireEvent(this.event);
        return this.event.isCancelled() ? this.event.getYaw() : f;
    }

    @Override // me.mioclient.ducks.DuckLivingEntityServerDead
    public void setServerSideDead(boolean z) {
        this.serverDead = z;
    }

    @Override // me.mioclient.ducks.DuckLivingEntityServerDead
    public boolean isServerSideDead() {
        return this.serverDead;
    }

    @Inject(method = {"aiStep"}, at = {@At("HEAD")})
    public void reduceCooldown(CallbackInfo callbackInfo) {
        NoJumpDelay njd_lcl = getNjd();
        ElytraFly efly_lcl = getEfly();

        if (njd_lcl == null) return;

        if (efly_lcl == null) return;

        Minecraft minecraftClient = Minecraft.getInstance();
        if (efly_lcl.isToggled() && efly_lcl.mode.getValue() == ElytraFly.ElytraFlyPredicateMode.BOUNCE && ((Object) this) == minecraftClient.player && this.noJumpDelay > 2) {
            this.noJumpDelay = 2;
        } else if (njd_lcl.isToggled() && ((Object) this) == minecraftClient.player && !minecraftClient.player.isFallFlying()) {
            this.noJumpDelay = 0;
        }
    }

    @Inject(method = {"isFallFlying"}, at = {@At("TAIL")}, cancellable = true)
    public void onRecast(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        ElytraFly efly_lcl = getEfly();

        if (efly_lcl == null) return;

        if (((Object) this) != Minecraft.getInstance().player) {
            return;
        }
        if (efly_lcl.shouldSwapElytra()) {
            callbackInfoReturnable.setReturnValue(false);
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.prevTime >= 12) {
            if (efly_lcl.isToggled() && efly_lcl.mode.getValue() == ElytraFly.ElytraFlyPredicateMode.BOUNCE) {
                boolean booleanValue = ((Boolean) callbackInfoReturnable.getReturnValue()).booleanValue();
                if (this.prevFlying && !booleanValue) {
                    if (efly_lcl.tryStartFallFlying()) {
                        PacketSender.sendStartFallFlying();
                    }
                    callbackInfoReturnable.setReturnValue(Boolean.valueOf(efly_lcl.tryStartFallFlying()));
                }
                this.prevFlying = booleanValue;
            }
            this.prevTime = currentTimeMillis;
        }
    }

    @Inject(method = {"hasEffect"}, at = {@At("HEAD")}, cancellable = true)
    private void hasStatusEffectHook(Holder<MobEffect> registryEntry, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (((Object) this) == Minecraft.getInstance().player) {
            GetStatusEffectEvent getStatusEffectEvent = new GetStatusEffectEvent(registryEntry);
            ClientAccessor.eventBus.fireEvent(getStatusEffectEvent);
            if (getStatusEffectEvent.isCancelled()) {
                callbackInfoReturnable.cancel();
                callbackInfoReturnable.setReturnValue(false);
            }
        }
    }

    @Inject(method = {"getEffect"}, at = {@At("HEAD")}, cancellable = true)
    private void getStatusEffectHook(Holder<MobEffect> registryEntry, CallbackInfoReturnable<MobEffectInstance> callbackInfoReturnable) {
        if (((Object) this) == Minecraft.getInstance().player) {
            GetStatusEffectEvent getStatusEffectEvent = new GetStatusEffectEvent(registryEntry);
            ClientAccessor.eventBus.fireEvent(getStatusEffectEvent);
            if (getStatusEffectEvent.isCancelled()) {
                callbackInfoReturnable.cancel();
                callbackInfoReturnable.setReturnValue(null);
            }
        }
    }

    @Inject(method = {"jumpFromGround"}, at = {@At("HEAD")}, cancellable = true)
    private void jumpHook(CallbackInfo callbackInfo) {
        Speed speed_lcl = getSpeedModule();

        if (speed_lcl == null) return;

        if (!speed_lcl.shouldPause() && speed_lcl.isToggled() && speed_lcl.mode.getValue() == Speed.SpeedPredicateMode.speedPredicateMode2 && MovementUtils.is2181()) {
            callbackInfo.cancel();
        }
    }

    /* 26.2 port: travel no longer invokes move(MoverType, Vec3) — it dispatches
       to travelInFluid/travelFallFlying/travelInAir. Hook HEAD instead; same
       pre-movement timing for the TickPreEvent. */
    @Inject(method = {"travel"}, at = {@At("HEAD")})
    private void tickPre(Vec3 vec3d, CallbackInfo callbackInfo) {
        if (TickPreEvent.isPlayerVehicle(this)) {
            this.mio$moveEvent = new TickPreEvent(((LivingEntity)(Object) this).getDeltaMovement());
            ClientAccessor.eventBus.fireEvent(this.mio$moveEvent);
            ((LivingEntity)(Object) this).setDeltaMovement(new Vec3(this.mio$moveEvent.getX(), this.mio$moveEvent.getY(), this.mio$moveEvent.getZ()));
        }
    }
}
