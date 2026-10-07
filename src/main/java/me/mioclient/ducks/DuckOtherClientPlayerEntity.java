package me.mioclient.ducks;

import net.minecraft.world.damagesource.DamageSource;

public interface DuckOtherClientPlayerEntity {
    default boolean damageSuper(DamageSource damageSource, float f) {
        return true;
    }
}
