package me.mioclient.util.player;

import me.mioclient.mode.combat.BlockerOffsetMode;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientAccessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.mioclient.module.movement.ElytraFly;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import me.mioclient.mode.combat.BlockerOffsetMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PlayerUtils.class */
public class PlayerUtils implements ClientAccessor {
    public static boolean isInCobweb(PlayerEntity playerEntity) {
        return minecraftClient.world.getStatesInBox(playerEntity.getBoundingBox()).anyMatch(blockState -> {
            return blockState.isOf(Blocks.COBWEB);
        });
    }

    public static boolean isInWater(PlayerEntity playerEntity) {
        return BlockPlacer.getBlockAt(BlockPos.ofFloored(playerEntity.getX(), playerEntity.getBoundingBox().minY, playerEntity.getZ())) == Blocks.WATER || playerEntity.isTouchingWater();
    }

    public static boolean isInLiquid(PlayerEntity playerEntity) {
        return isInWater(playerEntity) || playerEntity.isInLava();
    }

    public static BlockPos getRoundedBlockPos() {
        return getRoundedBlockPos(minecraftClient.player);
    }

    public static BlockPos getRoundedBlockPos(LivingEntity livingEntity) {
        return BlockPos.ofFloored(livingEntity.getX(), Math.round(livingEntity.getY()), livingEntity.getZ());
    }

    public static Direction getMovementDirection() {
        double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(), minecraftClient.player.input, Double.longBitsToDouble(4607182418800017408L));
        return Direction.fromRotation(Math.toDegrees(Math.atan2(doubleArray2508[1], doubleArray2508[0])) - MathConstants.num2);
    }

    public static List<BlockPos> getSurroundPositions(LivingEntity livingEntity) {
        ArrayList arrayList = new ArrayList();
        Set<BlockPos> set2011 = getFeetPositions(livingEntity);
        for (BlockPos blockPos : set2011) {
            for (Vec3i vec3i : BlockerOffsetMode.SURROUND.getOffsets()) {
                BlockPos add = blockPos.add(vec3i);
                if (!set2011.contains(add)) {
                    arrayList.add(add);
                }
            }
        }
        return arrayList;
    }

    public static Set<BlockPos> getFeetPositions(LivingEntity livingEntity) {
        BlockPos blockPos2008 = getRoundedBlockPos(livingEntity);
        HashSet hashSet = new HashSet();
        Box expand = livingEntity.getBoundingBox().expand(-BoxMath.val);
        hashSet.add(BlockPos.ofFloored(expand.minX, blockPos2008.getY(), expand.minZ));
        hashSet.add(BlockPos.ofFloored(expand.minX, blockPos2008.getY(), expand.maxZ));
        hashSet.add(BlockPos.ofFloored(expand.maxX, blockPos2008.getY(), expand.minZ));
        hashSet.add(BlockPos.ofFloored(expand.maxX, blockPos2008.getY(), expand.maxZ));
        return hashSet;
    }

    public static boolean isHungry() {
        return minecraftClient.player.getHungerManager().getFoodLevel() < 6;
    }

    public static boolean hasArmor(LivingEntity livingEntity) {
        Iterator it = livingEntity.getArmorItems().iterator();
        while (it.hasNext()) {
            if (!((ItemStack) it.next()).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasElytraEquipped(PlayerEntity playerEntity) {
        return playerEntity.getInventory().getArmorStack(EquipmentSlot.CHEST.getEntitySlotId()).isOf(Items.ELYTRA);
    }

    public static boolean isReallyFallFlying() {
        if (ElytraFly.flag) {
            return minecraftClient.player.isFallFlying();
        }
        ElytraFly.flag = true;
        boolean isFallFlying = minecraftClient.player.isFallFlying();
        ElytraFly.flag = false;
        return isFallFlying;
    }

    public static Hand getOppositeHand(Hand hand) {
        return hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }
}
