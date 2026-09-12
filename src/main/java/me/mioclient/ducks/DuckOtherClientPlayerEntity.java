package me.mioclient.ducks;

import net.minecraft.entity.damage.DamageSource;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/DuckOtherClientPlayerEntity.class */
public interface DuckOtherClientPlayerEntity {
    default boolean damageSuper(DamageSource damageSource, float f) {
        return true;
    }
}
