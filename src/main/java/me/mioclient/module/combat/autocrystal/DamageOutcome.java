package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.module.combat.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoCrystalMode_7.class */
public enum DamageOutcome implements ClientAccessor {
    NONE(false),
    POP(true),
    KILL(true);

    public static AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public final boolean flag;

    DamageOutcome(boolean z) {
        this.flag = z;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0073  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static DamageOutcome evaluate(LivingEntity livingEntity, double d) {
        boolean z2 = d >= ((double) EntityUtil.getEntityHealth((Entity) livingEntity));
        boolean z = livingEntity.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)
                || livingEntity.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
        return !z2 ? (z ? POP : KILL) : NONE;
    }
}
