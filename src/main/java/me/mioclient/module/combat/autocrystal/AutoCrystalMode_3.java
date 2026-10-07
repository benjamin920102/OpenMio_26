package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;

import me.mioclient.module.combat.AutoCrystal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public enum AutoCrystalMode_3 implements ClientAccessor, NamedValue {
    PLACE("Place"),
    HIT("Hit");

    public final String name;
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving autoCrystal null. */
    private static AutoCrystal autoCrystal;

    public static AutoCrystal getAutoCrystal() {
        if (autoCrystal == null && ClientServices.moduleInjector != null) {
            try {
                autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
            } catch (Exception e) {
            }
        }
        return autoCrystal;
    }

    AutoCrystalMode_3(String str) {
        this.name = str;
    }

    public double getMinDamageThreshold(Player playerEntity) {
        AutoCrystal autoCrystal_lcl = getAutoCrystal();
        if (autoCrystal_lcl == null) {
            return Math.min(6.0d, EntityUtil.getEntityHealth((Entity) playerEntity));
        }
        if (autoCrystal_lcl.flag3) {
            return 0.1d;
        }
        if (autoCrystal_lcl.facePlace.getValue().booleanValue() && (EntityUtil.getEntityHealth((Entity) playerEntity) <= autoCrystal_lcl.health2.getValue().floatValue() || InventoryUtils.getArmorDurability(playerEntity) <= autoCrystal_lcl.armorThreshold.getValue().intValue())) {
            return 0.6d;
        }
        if (minecraftClient.level.getBlockState(minecraftClient.player.blockPosition()).is(Blocks.NETHER_PORTAL)) {
            return 0.6d;
        }
        double floatValue = autoCrystal_lcl.minDamage2.getValue().floatValue();
        double floatValue2 = autoCrystal_lcl.minDamage.getValue().floatValue();
        if (autoCrystal_lcl.minDamage2.isHoleSnapConfigured()) {
            floatValue = EntityUtil.getEntityHealth((Entity) playerEntity);
        }
        if (autoCrystal_lcl.isMineIgnoreActive()) {
            floatValue = Math.min(floatValue, 5.0d);
            floatValue2 = 3.0d;
        }
        return Math.min(this == HIT ? floatValue2 : floatValue, EntityUtil.getEntityHealth((Entity) playerEntity));
    }

    public double getMaxSelfDamage() {
        AutoCrystal autoCrystal_lcl = getAutoCrystal();
        if (autoCrystal_lcl == null) {
            return 8.0d;
        }
        if (autoCrystal_lcl.isSelfInSpawnGracePeriod()) {
            return 999.0d;
        }
        return (this == HIT ? autoCrystal_lcl.maxSelfDamage2.getValue() : autoCrystal_lcl.maxSelfDamage.getValue()).floatValue();
    }

    
    
    public boolean shouldSkipAttack(Player playerEntity, double d, double d2, DamageOutcome autoCrystalMode_7) {
        AutoCrystal autoCrystal_lcl = getAutoCrystal();
        boolean z2 = minecraftClient.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)
                || minecraftClient.player.getMainHandItem().is(Items.TOTEM_OF_UNDYING);
        boolean equals = autoCrystalMode_7.equals(DamageOutcome.KILL);
        if (autoCrystal_lcl == null) {
            return false;
        }
        if (autoCrystal_lcl.forceSuicide.getValue().booleanValue()) {
            return false;
        }
        if (autoCrystal_lcl.isSelfInSpawnGracePeriod()) {
            d = 0.0d;
        }
        if (d >= EntityUtil.getPlayerHealth() && autoCrystal_lcl.antiSuicide.getValue().booleanValue() && (!equals || !z2)) {
            return true;
        }
        if (equals) {
            return false;
        }
        if (this != PLACE || d <= 0.0d || d2 / d >= autoCrystal_lcl.damageRatio.getValue().floatValue()) {
            return d2 < ((double) EntityUtil.getEntityHealth((Entity) playerEntity)) && (d2 < getMinDamageThreshold(playerEntity) || d > getMaxSelfDamage());
        }
        return true;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}