package me.mioclient.module.combat.automine;

import me.mioclient.util.math.BoxMath;

import me.mioclient.module.combat.automine.MineStrategyBase;
import me.mioclient.module.combat.automine.MineTargetState;
import me.mioclient.util.math.BoxMath;

import java.util.Comparator;
import java.util.List;
import me.mioclient.module.combat.AutoMine;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoMineSearchHelper42_6.class */
public final class BurrowTargetStrategy extends MineStrategyBase {
    public BurrowTargetStrategy(AutoMine autoMine) {
        super(autoMine);
    }

    @Override // me.mioclient.module.combat.automine.MineTargetStrategy
    public void findTargets(MineTargetState autoMineHelper) {
        boolean isExtraBreakEnabled = this.autoMine.isExtraBreakEnabled();
        PlayerEntity playerEntity886 = this.autoMine.autoMineSearchHelper4.getTargetPlayer();
        if (playerEntity886 == null) {
            return;
        }
        List list = BlockPos.stream(playerEntity886.getBoundingBox().expand(-BoxMath.val, 0.0d, -BoxMath.val).withMaxY(playerEntity886.getY())).map((v0) -> {
            return v0.toImmutable();
        }).filter(blockPos -> {
            return this.isValidMineTarget(blockPos);
        }).filter(blockPos2 -> {
            Block block = minecraftClient.world.getBlockState(blockPos2).getBlock();
            return (block.getBlastResistance() < Float.intBitsToFloat(1142292480) || block == Blocks.COBWEB || block == Blocks.ANVIL) ? false : true;
        }).sorted(Comparator.comparing(blockPos3 -> {
            return isSpeedMineTarget(blockPos3) ? Double.valueOf(Double.longBitsToDouble(-4616189618054758400L)) : Double.valueOf(playerEntity886.squaredDistanceTo(blockPos3.toCenterPos()));
        })).toList();
        if (list.isEmpty()) {
            return;
        }
        int i = 700;
        if (isExtraBreakEnabled && list.size() == 1) {
            i = 500;
        }
        autoMineHelper.tryUpdatePriority(i, autoMineHelper2 -> {
            if (!isExtraBreakEnabled) {
                autoMineHelper2.setBlockPos((BlockPos) list.getFirst());
            } else if (list.size() == 1) {
                autoMineHelper2.setSecondaryBlockPos((BlockPos) list.getFirst());
            } else {
                autoMineHelper2.setBlockPos((BlockPos) list.getFirst());
                autoMineHelper2.setSecondaryBlockPos((BlockPos) list.get(1));
            }
        });
    }

    @Override // me.mioclient.module.combat.automine.MineStrategyBase, me.mioclient.module.combat.automine.MineTargetStrategy
    public boolean isEnabled() {
        return this.autoMine.burrow.getValue().booleanValue();
    }
}
