package me.mioclient.mode.combat;

import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.player.EntityUtil;

import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AuraTargetSortMode.class */
public enum AuraTargetSortMode implements ClientAccessor {
    DISTANCE(livingEntity -> {
        return Double.valueOf(livingEntity.squaredDistanceTo(ClientAccessor.minecraftClient.player));
    }),
    HEALTH(livingEntity2 -> {
        return Double.valueOf(EntityUtil.getEntityHealth((Entity) livingEntity2));
    });

    public final Function<LivingEntity, Double> function;

    AuraTargetSortMode(Function<LivingEntity, Double> function) {
        this.function = function;
    }

    public Function<LivingEntity, Double> getSortFunction() {
        return this.function;
    }
}
