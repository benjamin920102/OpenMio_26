package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.module.combat.AutoCrystal;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoCrystalMode_3.class */
public enum AutoCrystalMode_3 implements ClientAccessor, NamedValue {
    PLACE("Place"),
    HIT("Hit");

    public final String name;
    public static AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);

    AutoCrystalMode_3(String str) {
        this.name = str;
    }

    public double getMinDamageThreshold(PlayerEntity playerEntity) {
        if (autoCrystal.flag3) {
            return 0.1d;
        }
        if (autoCrystal.facePlace.getValue().booleanValue() && (EntityUtil.getEntityHealth((Entity) playerEntity) <= autoCrystal.health2.getValue().floatValue() || InventoryUtils.getArmorDurability(playerEntity) <= autoCrystal.armorThreshold.getValue().intValue())) {
            return 0.6d;
        }
        if (minecraftClient.world.getBlockState(minecraftClient.player.getBlockPos()).isOf(Blocks.NETHER_PORTAL)) {
            return 0.6d;
        }
        double floatValue = autoCrystal.minDamage2.getValue().floatValue();
        double floatValue2 = autoCrystal.minDamage.getValue().floatValue();
        if (autoCrystal.minDamage2.isHoleSnapConfigured()) {
            floatValue = EntityUtil.getEntityHealth((Entity) playerEntity);
        }
        if (autoCrystal.isMineIgnoreActive()) {
            floatValue = Math.min(floatValue, 5.0d);
            floatValue2 = 3.0d;
        }
        return Math.min(this == HIT ? floatValue2 : floatValue, EntityUtil.getEntityHealth((Entity) playerEntity));
    }

    public double getMaxSelfDamage() {
        if (autoCrystal.isSelfInSpawnGracePeriod()) {
            return 999.0d;
        }
        return (this == HIT ? autoCrystal.maxSelfDamage2.getValue() : autoCrystal.maxSelfDamage.getValue()).floatValue();
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x007f A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean shouldSkipAttack(PlayerEntity playerEntity, double d, double d2, DamageOutcome autoCrystalMode_7) {
        boolean z2 = minecraftClient.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)
                || minecraftClient.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
        boolean equals = autoCrystalMode_7.equals(DamageOutcome.KILL);
        if (autoCrystal.forceSuicide.getValue().booleanValue()) {
            return false;
        }
        if (autoCrystal.isSelfInSpawnGracePeriod()) {
            d = 0.0d;
        }
        if (d >= EntityUtil.getPlayerHealth() && autoCrystal.antiSuicide.getValue().booleanValue() && (!equals || !z2)) {
            return true;
        }
        if (equals) {
            return false;
        }
        if (this != PLACE || d <= 0.0d || d2 / d >= autoCrystal.damageRatio.getValue().floatValue()) {
            return d2 < ((double) EntityUtil.getEntityHealth((Entity) playerEntity)) && (d2 < getMinDamageThreshold(playerEntity) || d > getMaxSelfDamage());
        }
        return true;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
