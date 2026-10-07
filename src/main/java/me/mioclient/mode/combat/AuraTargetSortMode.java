package me.mioclient.mode.combat;

import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;

import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public enum AuraTargetSortMode implements ClientAccessor {
    DISTANCE(livingEntity -> {
        return Double.valueOf(livingEntity.distanceToSqr(ClientAccessor.minecraftClient.player));
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
