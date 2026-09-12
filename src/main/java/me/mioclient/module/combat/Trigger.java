package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.PlayerUtils;

import java.util.concurrent.TimeUnit;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.mixin.ducks.DuckMinecraftClient;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/Trigger.class */
public class Trigger extends Module {
    public Setting<Float> setting;
    public Setting<Boolean> setting2;
    public Setting<Double> setting3;
    public Setting<Integer> setting4;
    public Setting<Boolean> setting5;
    public Setting<Boolean> setting6;
    public Setting<Boolean> setting7;
    public Setting<Boolean> setting8;
    public Setting<Boolean> setting9;
    public Setting<Boolean> setting10;
    public final Stopwatch stopwatch;

    public Trigger() {
        super("Trigger", "Attacks entities under your crosshair.", Category.COMBAT, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x013e  */
    /* JADX WARN: Removed duplicated region for block: B:60:? A[RETURN, SYNTHETIC] */
    @Listen
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handleMotion(MotionEvent motionEvent) {
        boolean z;
        boolean z2;
        if (this.setting == null || this.setting2 == null || this.setting3 == null || this.setting4 == null || this.setting5 == null || this.setting6 == null || this.setting7 == null || this.setting8 == null || this.setting9 == null || this.setting10 == null) {
            return;
        }
        if (motionEvent.getPhase() == KeyPearlMode.Post || isNotInGame()) {
            return;
        }
        if ((!(minecraftClient.player.getMainHandStack().getItem() instanceof AxeItem) && !(minecraftClient.player.getMainHandStack().getItem() instanceof SwordItem) && this.setting2.getValue().booleanValue()) || !(minecraftClient.crosshairTarget instanceof EntityHitResult)) {
            return;
        }
        Entity entity = ((EntityHitResult) minecraftClient.crosshairTarget).getEntity();
        if (entity instanceof PlayerEntity) {
            if (!ClientServices.socialManager.isFriend(entity.getName().getString()) && this.setting6.getValue().booleanValue()) {
                z = true;
                z2 = z;
                if (z2 && this.setting7.getValue().booleanValue() && !PlayerUtils.hasArmor((PlayerEntity) entity)) {
                    z2 = false;
                }
                if (entity.isAlive()) {
                    return;
                }
                if (z2 || (((entity instanceof PassiveEntity) && this.setting8.getValue().booleanValue()) || (((entity instanceof Monster) && this.setting9.getValue().booleanValue()) || ((entity instanceof EndCrystalEntity) && this.setting10.getValue().booleanValue())))) {
                    if (minecraftClient.player.getAttackCooldownProgress(Float.intBitsToFloat(1056964608)) >= Float.intBitsToFloat(1065353216)) {
                        if (!this.stopwatch.hasElapsed(this.setting3.getValue().doubleValue() * Math.random(), TimeUnit.SECONDS) || minecraftClient.player.distanceTo(entity) > this.setting.getValue().floatValue()) {
                            return;
                        }
                        performAttack(MathUtils.isChanceHit(this.setting4.getValue().intValue()));
                        this.stopwatch.reset();
                        return;
                    }
                    return;
                }
                return;
            }
        }
        z = false;
        z2 = z;
        if (z2) {
            z2 = false;
        }
        if (entity.isAlive()) {
        }
    }

    public void performAttack(boolean z) {
        if (!z) {
            ((DuckMinecraftClient)(Object) minecraftClient).attack();
            return;
        }
        minecraftClient.player.swingHand(Hand.MAIN_HAND);
        minecraftClient.player.resetLastAttackedTicks();
    }
}
