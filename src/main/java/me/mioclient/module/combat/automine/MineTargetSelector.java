package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.PlayerUtils;

import java.util.Comparator;
import me.mioclient.module.combat.AutoMine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import me.mioclient.manager.RotationManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MineTargetSelector.class */
public final class MineTargetSelector implements ClientAccessor {
    public final AutoMine autoMine;

    public MineTargetSelector(AutoMine autoMine) {
        this.autoMine = autoMine;
    }

    public PlayerEntity getTargetPlayer() {
        return (PlayerEntity) minecraftClient.world.getPlayers().stream().filter(abstractClientPlayerEntity -> {
            return abstractClientPlayerEntity.isAlive() && abstractClientPlayerEntity != minecraftClient.player && minecraftClient.player.distanceTo((Entity) abstractClientPlayerEntity) <= this.autoMine.getRange() && !ClientServices.socialManager.isFriendEntity((PlayerEntity) abstractClientPlayerEntity);
        }).filter(abstractClientPlayerEntity2 -> {
            return !this.autoMine.ignoreNaked.getValue().booleanValue() || PlayerUtils.hasArmor((LivingEntity) abstractClientPlayerEntity2);
        }).min(Comparator.comparing(abstractClientPlayerEntity3 -> {
            return Float.valueOf(MathHelper.angleBetween(minecraftClient.player.getYaw(), RotationManager.getRotationsTo((Entity) abstractClientPlayerEntity3)[0]));
        })).orElse(null);
    }

    public Box getFeetBox(PlayerEntity playerEntity) {
        return playerEntity.getBoundingBox().expand(-BoxMath.val, 0.0d, -BoxMath.val).withMaxY(playerEntity.getY());
    }
}
