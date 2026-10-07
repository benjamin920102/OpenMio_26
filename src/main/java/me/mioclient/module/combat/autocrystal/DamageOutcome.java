package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;

import me.mioclient.module.combat.AutoCrystal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;

public enum DamageOutcome implements ClientAccessor {
    NONE(false),
    POP(true),
    KILL(true);

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
    public final boolean flag;

    DamageOutcome(boolean z) {
        this.flag = z;
    }

    
    
    public static DamageOutcome evaluate(LivingEntity livingEntity, double d) {
        boolean z2 = d >= ((double) EntityUtil.getEntityHealth((Entity) livingEntity));
        boolean z = livingEntity.getOffhandItem().is(Items.TOTEM_OF_UNDYING)
                || livingEntity.getMainHandItem().is(Items.TOTEM_OF_UNDYING);
        return !z2 ? (z ? POP : KILL) : NONE;
    }
}