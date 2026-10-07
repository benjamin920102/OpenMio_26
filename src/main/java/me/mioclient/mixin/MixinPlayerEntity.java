package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.manager.SocialEntry;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.render.NameTagsHelperMode;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientAccessor;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import me.mioclient.ducks.DuckPlayerEntity;
import me.mioclient.event.events.player.AttackHookPostEvent;
import me.mioclient.module.exploit.Reach;
import me.mioclient.module.movement.Sprint;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})

public abstract class MixinPlayerEntity extends LivingEntity implements DuckPlayerEntity {
    private static Sprint sprint;

    private static Sprint getSprint() {

        if (sprint == null && ClientServices.moduleInjector != null) {

            try { sprint = (Sprint) ClientServices.moduleInjector.getModule(Sprint.class); } catch (Exception e) {}

        }

        return sprint;

    }
    private static Reach reach;

    private static Reach getReach() {

        if (reach == null && ClientServices.moduleInjector != null) {

            try { reach = (Reach) ClientServices.moduleInjector.getModule(Reach.class); } catch (Exception e) {}

        }

        return reach;

    }

    @Unique
    private long mio$lastEatingTime;

    @Unique
    private NameTagsHelperMode mio$role;

    @Unique
    private boolean mio$isNextToWall;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = {"<init>"}, at = {@At("TAIL")})
    private void initHook(Level world, GameProfile gameProfile, CallbackInfo callbackInfo) {
        for (SocialEntry mixinPlayerEntityData : ClientServices.socialManager.getRegistry()) {
            if (gameProfile.name().equalsIgnoreCase(mixinPlayerEntityData.getRawName())) {
                mio$setRole(mixinPlayerEntityData.getHelperMode());
                return;
            }
        }
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void tick(CallbackInfo callbackInfo) {
        this.mio$isNextToWall = !level().noCollision(getBoundingBox().inflate(1.0E-4d, 0.0d, 1.0E-4d));
        if (!isUsingItem() || get70(getUseItem(), this) > 1.0f) {
            this.mio$lastEatingTime = System.currentTimeMillis();
        }
    }

    @Inject(method = {"attack"}, at = {@At("HEAD")}, cancellable = true)
    public void attackHookPre(Entity entity, CallbackInfo callbackInfo) {
        AttackHookPostEvent attackHookPostEvent = new AttackHookPostEvent(KeyPearlMode.Pre, entity);
        ClientAccessor.eventBus.fireEvent(attackHookPostEvent);
        if (attackHookPostEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"attack"}, at = {@At("TAIL")})
    private void attackHookPost(Entity entity, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new AttackHookPostEvent(KeyPearlMode.Post, entity));
    }

    /* 26.2 port: Player.attack no longer calls Entity.setDeltaMovement — sprint
       knockback now flows through Player.causeExtraKnockback (verified via
       javap on the 26.2 jar: invokevirtual Player.causeExtraKnockback). */
    @WrapWithCondition(method = {"attack"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeExtraKnockback(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;FZ)V"))
    private boolean attackHook(Player playerEntity, Entity entity, float f, Vec3 vec3d, DamageSource damageSource, float f2, boolean z) {
        Sprint sprint_lcl = getSprint();

        if (sprint_lcl == null) return true;

        return (sprint_lcl.isToggled() && sprint_lcl.keep.getValue().booleanValue()) ? false : true;
    }

    @Inject(method = {"entityInteractionRange"}, at = {@At("HEAD")}, cancellable = true)
    private void getEntityInteractionRangeHook(CallbackInfoReturnable<Double> callbackInfoReturnable) {
        Reach reach_lcl = getReach();

        if (reach_lcl == null) return;

        if (reach_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(Double.valueOf(reach_lcl.modifier.getValue().doubleValue()));
        }
    }

    @Inject(method = {"blockInteractionRange"}, at = {@At("HEAD")}, cancellable = true)
    private void getBlockInteractionRangeHook(CallbackInfoReturnable<Double> callbackInfoReturnable) {
        Reach reach_lcl = getReach();

        if (reach_lcl == null) return;

        if (reach_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(Double.valueOf(reach_lcl.modifier.getValue().doubleValue()));
        }
    }

    @Inject(method = {"isSecondaryUseActive"}, at = {@At("HEAD")}, cancellable = true)
    private void shouldCancelInteractionHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (BlockPlacer.flag) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public long mio$getLastEatingTime() {
        return this.mio$lastEatingTime;
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public NameTagsHelperMode mio$getRole() {
        return this.mio$role;
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public void mio$setRole(NameTagsHelperMode nameTagsHelperMode) {
        this.mio$role = nameTagsHelperMode;
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public boolean mio$isNextToWall() {
        return this.mio$isNextToWall;
    }
}
