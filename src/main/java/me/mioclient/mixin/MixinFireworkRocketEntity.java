package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import net.minecraft.world.phys.Vec3;

import me.mioclient.util.math.MathUtils;


import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.Fireworks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified): entity is FireworkRocketEntity; shooter field is now
   attachedToEntity; age is tickCount; getRotationVec is getLookAngle;
   addVelocity is push; boost applies via LivingEntity.setDeltaMovement. */
@Mixin({FireworkRocketEntity.class})
public abstract class MixinFireworkRocketEntity extends Entity {
    private static Fireworks fireworks;

    private static Fireworks getFireworks() {

        if (fireworks == null && ClientServices.moduleInjector != null) {

            try { fireworks = (Fireworks) ClientServices.moduleInjector.getModule(Fireworks.class); } catch (Exception e) {}

        }

        return fireworks;

    }

    @Shadow
    @Nullable
    private LivingEntity attachedToEntity;

    public MixinFireworkRocketEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void tickHook(CallbackInfo callbackInfo) {
        ElytraFly.flag = true;
    }

    @Inject(method = {"tick"}, at = {@At("TAIL")})
    private void tickHook2(CallbackInfo callbackInfo) {
        ElytraFly.flag = false;
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.AFTER)})
    private void tick(CallbackInfo callbackInfo) {
        Fireworks fireworks_lcl = getFireworks();

        if (fireworks_lcl == null) return;

        if (fireworks_lcl.flag3) {
            this.tickCount = 1;
            fireworks_lcl.flag3 = false;
        }
        float f = fireworks_lcl.getBoostFactor(true);
        if (this.attachedToEntity == Minecraft.getInstance().player && f != 0.0f) {
            float f2 = MathUtils.getRadians(this.attachedToEntity.getYRot());
            if (f > 0.5d) {
                f = Mth.clamp(0.2f + (0.3f * this.tickCount), 0.0f, f);
            }
            if (fireworks_lcl.getRemainingTicks() > 0) {
                this.attachedToEntity.push(this.attachedToEntity.getLookAngle().scale(f));
            } else {
                this.attachedToEntity.push(Mth.sin(f2) * (-f), 0.0d, Mth.cos(f2) * f);
            }
        }
    }
}
