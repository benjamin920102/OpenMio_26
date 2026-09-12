package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.MathUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.MathUtils;

import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.Fireworks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({FireworkRocketEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinFireworkRocketEntity.class */
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
    private LivingEntity field_7616;

    public MixinFireworkRocketEntity(EntityType<?> entityType, World world) {
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

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER)})
    private void tick(CallbackInfo callbackInfo) {
        Fireworks fireworks_lcl = getFireworks();

        if (fireworks_lcl == null) return;

        if (fireworks_lcl.flag3) {
            this.age = 1;
            fireworks_lcl.flag3 = false;
        }
        float f = fireworks_lcl.getBoostFactor(true);
        if (this.field_7616 == MinecraftClient.getInstance().player && f != 0.0f) {
            float f2 = MathUtils.getRadians(this.field_7616.getYaw());
            if (f > 0.5d) {
                f = MathHelper.clamp(0.2f + (0.3f * this.age), 0.0f, f);
            }
            if (fireworks_lcl.getRemainingTicks() > 0) {
                this.field_7616.addVelocity(this.field_7616.getRotationVec(1.0f).multiply(f));
            } else {
                this.field_7616.addVelocity(MathHelper.sin(f2) * (-f), 0.0d, MathHelper.cos(f2) * f);
            }
        }
    }
}
