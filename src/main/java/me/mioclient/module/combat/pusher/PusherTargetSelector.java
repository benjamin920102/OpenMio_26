package me.mioclient.module.combat.pusher;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;

import java.util.Comparator;
import java.util.List;
import me.mioclient.module.combat.Pusher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import me.mioclient.manager.RotationManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PusherTargetSelector.class */
public final class PusherTargetSelector implements ClientAccessor {
    public static final List<Block> list = List.of(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.REINFORCED_DEEPSLATE, Blocks.RESPAWN_ANCHOR);
    public final Pusher pusher;

    public PusherTargetSelector(Pusher pusher) {
        this.pusher = pusher;
    }

    public PlayerEntity getTargetPlayer() {
        return (PlayerEntity) minecraftClient.world.getPlayers().stream().filter(abstractClientPlayerEntity -> {
            return abstractClientPlayerEntity.isAlive() && abstractClientPlayerEntity != minecraftClient.player && EntityUtil.isOnGround((LivingEntity) abstractClientPlayerEntity) && minecraftClient.player.distanceTo((Entity) abstractClientPlayerEntity) <= this.pusher.getRange() && !ClientServices.socialManager.isFriendEntity((PlayerEntity) abstractClientPlayerEntity);
        }).filter(abstractClientPlayerEntity2 -> {
            return !this.pusher.ignoreNaked.getValue().booleanValue() || PlayerUtils.hasArmor((LivingEntity) abstractClientPlayerEntity2);
        }).filter((v1) -> {
            return isValidPushTarget(v1);
        }).min(Comparator.comparing(abstractClientPlayerEntity3 -> {
            return Float.valueOf(MathHelper.angleBetween(minecraftClient.player.getYaw(), RotationManager.getRotationsTo((Entity) abstractClientPlayerEntity3)[0]));
        })).orElse(null);
    }

    public boolean isValidPushTarget(PlayerEntity playerEntity) {
        BlockPos blockPos = playerEntity.getBlockPos();
        BlockState blockState = minecraftClient.world.getBlockState(blockPos.up());
        if (list.contains(blockState.getBlock())) {
            return false;
        }
        if (blockState.getHardness(minecraftClient.world, blockPos.up()) == Float.intBitsToFloat(-1082130432)) {
            return false;
        }
        if (ClientServices.stashFinderHelper.isBlockSolid(blockPos.up(2))) {
            return false;
        }
        if (this.pusher.onlyHole.getValue().booleanValue() && !ClientServices.holeSnapOverlayHelper.isHoleAt(blockPos)) {
            return false;
        }
        if (this.pusher.onlySafe.getValue().booleanValue() && !ClientServices.holeSnapOverlayHelper.isHoleAt(blockPos)) {
            if (minecraftClient.world.getBlockState(playerEntity.getBlockPos()).getBlock().getBlastResistance() < Float.intBitsToFloat(1142292480)) {
                return false;
            }
        }
        return PlayerUtils.getSurroundPositions((LivingEntity) playerEntity).size() <= 6;
    }
}
