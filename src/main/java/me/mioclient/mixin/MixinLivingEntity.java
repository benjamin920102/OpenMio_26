package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.Vec3dUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerTickDriver;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerTickDriver;
import me.mioclient.util.math.Vec3dUtils;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mioclient.ducks.DuckLivingEntityServerDead;
import me.mioclient.command.FakePlayerCommand;
import me.mioclient.event.events.player.GetStatusEffectEvent;
import me.mioclient.event.events.player.TickPreEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.module.misc.Swing;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.FastLadder;
import me.mioclient.module.movement.Fireworks;
import me.mioclient.module.movement.Jesus;
import me.mioclient.module.movement.NoJumpDelay;
import me.mioclient.module.movement.Speed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({LivingEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinLivingEntity.class */
public abstract class MixinLivingEntity extends Entity implements DuckLivingEntityServerDead {

    @Shadow
    private int field_6228;

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

    private static Speed getSpeed() {

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
    private static FastLadder fastladder;

    private static FastLadder getFastladder() {

        if (fastladder == null && ClientServices.moduleInjector != null) {

            try { fastladder = (FastLadder) ClientServices.moduleInjector.getModule(FastLadder.class); } catch (Exception e) {}

        }

        return fastladder;

    }

    public MixinLivingEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.prevTime = 0L;
    }

    @Shadow
    public abstract float getYaw(float f);

    @Shadow
    public abstract void setBodyYaw(float f);

    @Shadow
    public abstract boolean damage(DamageSource damageSource, float f);

    @Inject(method = {"getHandSwingDuration"}, at = {@At("HEAD")}, cancellable = true)
    private void getHandSwingDurationHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        Swing swing_lcl = getSwing();
        Speed speed_lcl = getSpeed();

        if (swing_lcl == null) return;

        if (speed_lcl == null) return;

        if (swing_lcl.isToggled()) {
            LivingEntity livingEntity = (LivingEntity)(Object) this;
            callbackInfoReturnable.cancel();
            int doubleValue = (int) ((swing_lcl.type.getValue() == Swing.MixinLivingEntityMode.ONE_EIGHT ? 7 : 6) / swing_lcl.speed.getValue().doubleValue());
            if (StatusEffectUtil.hasHaste(livingEntity)) {
                callbackInfoReturnable.setReturnValue(Integer.valueOf(doubleValue - (1 + StatusEffectUtil.getHasteAmplifier(livingEntity))));
            } else {
                callbackInfoReturnable.setReturnValue(Integer.valueOf(livingEntity.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? doubleValue + ((1 + livingEntity.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2) : doubleValue));
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

    @Inject(method = {"canWalkOnFluid"}, at = {@At("HEAD")}, cancellable = true)
    private void canWalkOnFluidHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Jesus jesus_lcl = getJesus();

        if (jesus_lcl == null) return;

        if (((Object) this) == MinecraftClient.getInstance().player && jesus_lcl.isToggled() && jesus_lcl.canJesus() && !MinecraftClient.getInstance().player.isTouchingWater()) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = {"travel"}, at = {@At("HEAD")})
    private void travelPreHook(Vec3d vec3d, CallbackInfo callbackInfo) {
        if (((Object) this) != MinecraftClient.getInstance().player) {
            return;
        }
        ElytraFly.flag = true;
        this.prevLookYaw = getYaw(1.0f);
        this.prevLookPitch = getPitch();
        this.event = new RotationEvent(this.prevLookYaw, this.prevLookPitch, false);
        ClientAccessor.eventBus.fireEvent(this.event);
        if (this.event.isCancelled()) {
            setYaw(this.event.getYaw());
            setPitch(this.event.getPitch());
        }
    }

    @Inject(method = {"travel"}, at = {@At("TAIL")})
    private void travelPostHook(Vec3d vec3d, CallbackInfo callbackInfo) {
        if (((Object) this) != MinecraftClient.getInstance().player) {
            return;
        }
        ElytraFly.flag = false;
        if (this.event.isCancelled()) {
            setYaw(this.prevLookYaw);
            setPitch(this.prevLookPitch);
        }
    }

    @ModifyExpressionValue(method = {"jump"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F")})
    private float jumpFix(float f) {
        this.event = new RotationEvent(getYaw(), getPitch(), true);
        ClientAccessor.eventBus.fireEvent(this.event);
        return this.event.isCancelled() ? this.event.getYaw() : f;
    }

    @Inject(method = {"shouldSpawnConsumptionEffects"}, at = {@At("HEAD")}, cancellable = true)
    private void shouldSpawnConsumptionEffects(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (PlayerTickDriver.is870()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(method = {"damage"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z")})
    private boolean damageHook(boolean z) {
        if (((Object) this) instanceof FakePlayerCommand.OtherClientPlayerEntity) {
            return false;
        }
        return z;
    }

    @Override // me.mioclient.ducks.DuckLivingEntityServerDead
    public void setServerSideDead(boolean z) {
        this.serverDead = z;
    }

    @Override // me.mioclient.ducks.DuckLivingEntityServerDead
    public boolean isServerSideDead() {
        return this.serverDead;
    }

    @Inject(method = {"tickMovement"}, at = {@At("HEAD")})
    public void reduceCooldown(CallbackInfo callbackInfo) {
        NoJumpDelay njd_lcl = getNjd();
        ElytraFly efly_lcl = getEfly();

        if (njd_lcl == null) return;

        if (efly_lcl == null) return;

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (efly_lcl.isToggled() && efly_lcl.mode.getValue() == ElytraFly.ElytraFlyPredicateMode.BOUNCE && equals(minecraftClient.player) && this.field_6228 > 2) {
            this.field_6228 = 2;
        } else if (njd_lcl.isToggled() && equals(minecraftClient.player) && !minecraftClient.player.isFallFlying()) {
            this.field_6228 = 0;
        }
    }

    @Inject(method = {"isFallFlying"}, at = {@At("TAIL")}, cancellable = true)
    public void onRecast(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        ElytraFly efly_lcl = getEfly();

        if (efly_lcl == null) return;

        if (((Object) this) != MinecraftClient.getInstance().player) {
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

    @Inject(method = {"hasStatusEffect"}, at = {@At("HEAD")}, cancellable = true)
    private void hasStatusEffectHook(RegistryEntry<StatusEffect> registryEntry, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (equals(MinecraftClient.getInstance().player)) {
            GetStatusEffectEvent getStatusEffectEvent = new GetStatusEffectEvent(registryEntry);
            ClientAccessor.eventBus.fireEvent(getStatusEffectEvent);
            if (getStatusEffectEvent.isCancelled()) {
                callbackInfoReturnable.cancel();
                callbackInfoReturnable.setReturnValue(false);
            }
        }
    }

    @Inject(method = {"getStatusEffect"}, at = {@At("HEAD")}, cancellable = true)
    private void getStatusEffectHook(RegistryEntry<StatusEffect> registryEntry, CallbackInfoReturnable<StatusEffectInstance> callbackInfoReturnable) {
        if (equals(MinecraftClient.getInstance().player)) {
            GetStatusEffectEvent getStatusEffectEvent = new GetStatusEffectEvent(registryEntry);
            ClientAccessor.eventBus.fireEvent(getStatusEffectEvent);
            if (getStatusEffectEvent.isCancelled()) {
                callbackInfoReturnable.cancel();
                callbackInfoReturnable.setReturnValue(null);
            }
        }
    }

    @Inject(method = {"jump"}, at = {@At("HEAD")}, cancellable = true)
    private void jumpHook(CallbackInfo callbackInfo) {
        Speed speed_lcl = getSpeed();

        if (speed_lcl == null) return;

        if (!speed_lcl.shouldPause() && speed_lcl.isToggled() && speed_lcl.mode.getValue() == Speed.SpeedPredicateMode.speedPredicateMode2 && MovementUtils.is2181()) {
            callbackInfo.cancel();
        }
    }

    @ModifyConstant(method = {"applyMovementInput"}, constant = {@Constant(doubleValue = 0.2d)})
    private double applyMovementInputHook(double d) {
        FastLadder fastladder_lcl = getFastladder();
        Speed speed_lcl = getSpeed();

        if (fastladder_lcl == null) return d;

        if (speed_lcl == null) return d;

        return fastladder_lcl.isToggled() ? fastladder_lcl.speed.getValue().floatValue() : d;
    }

    @Inject(method = {"applyMovementInput"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE)})
    private void tickPre(Vec3d vec3d, float f, CallbackInfoReturnable<Vec3d> callbackInfoReturnable) {
        if (TickPreEvent.isPlayerVehicle(this)) {
            this.mio$moveEvent = new TickPreEvent(getVelocity());
            ClientAccessor.eventBus.fireEvent(this.mio$moveEvent);
            Vec3dUtils.setVectorComponents(getVelocity(), this.mio$moveEvent.getX(), this.mio$moveEvent.getY(), this.mio$moveEvent.getZ());
        }
    }

    @Inject(method = {"travel"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE)})
    private void tickPre(Vec3d vec3d, CallbackInfo callbackInfo) {
        if (TickPreEvent.isPlayerVehicle(this)) {
            this.mio$moveEvent = new TickPreEvent(getVelocity());
            ClientAccessor.eventBus.fireEvent(this.mio$moveEvent);
            Vec3dUtils.setVectorComponents(getVelocity(), this.mio$moveEvent.getX(), this.mio$moveEvent.getY(), this.mio$moveEvent.getZ());
        }
    }
}
