package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientAccessor;

import java.util.Comparator;
import me.mioclient.module.combat.AutoMine;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import me.mioclient.manager.RotationManager;

public final class MineTargetSelector implements ClientAccessor {
    public final AutoMine autoMine;

    public MineTargetSelector(AutoMine autoMine) {
        this.autoMine = autoMine;
    }

    public Player getTargetPlayer() {
        return (Player) minecraftClient.level.players().stream().filter(abstractClientPlayerEntity -> {
            return abstractClientPlayerEntity.isAlive() && abstractClientPlayerEntity != minecraftClient.player && minecraftClient.player.distanceTo((Entity) abstractClientPlayerEntity) <= this.autoMine.getRange() && !ClientServices.socialManager.isFriendEntity((Player) abstractClientPlayerEntity);
        }).filter(abstractClientPlayerEntity2 -> {
            return !this.autoMine.ignoreNaked.getValue().booleanValue() || PlayerUtils.hasArmor((LivingEntity) abstractClientPlayerEntity2);
        }).min(Comparator.comparing(abstractClientPlayerEntity3 -> {
            return Float.valueOf(Mth.degreesDifferenceAbs(minecraftClient.player.getYRot(), RotationManager.getRotationsTo((Entity) abstractClientPlayerEntity3)[0]));
        })).orElse(null);
    }

    public AABB getFeetBox(Player playerEntity) {
        return playerEntity.getBoundingBox().inflate(-BoxMath.val, 0.0d, -BoxMath.val).setMaxY(playerEntity.getY());
    }
}