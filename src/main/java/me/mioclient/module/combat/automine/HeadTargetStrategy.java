package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.player.PlayerUtils;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import me.mioclient.module.combat.AutoMine;
import me.mioclient.module.combat.Range;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HeadTargetStrategy.class */
public final class HeadTargetStrategy extends MineStrategyBase {
    public static Range range = (Range) ClientServices.moduleInjector.getModule(Range.class);

    public HeadTargetStrategy(AutoMine autoMine) {
        super(autoMine);
    }

    @Override // me.mioclient.module.combat.automine.MineTargetStrategy
    public void findTargets(MineTargetState autoMineHelper) {
        int i;
        LivingEntity playerEntity886 = this.autoMine.autoMineSearchHelper4.getTargetPlayer();
        if (playerEntity886 == null || ((PlayerEntity) playerEntity886).isCrawling() || isSpaceBelowEmpty((Entity) playerEntity886)) {
            return;
        }
        Box boundingBox = ((PlayerEntity) playerEntity886).getBoundingBox();
        boolean isExtraBreakEnabled = this.autoMine.isExtraBreakEnabled();
        boolean anyMatch = BlockPos.stream(boundingBox.withMaxY(boundingBox.minY)).anyMatch(blockPos -> {
            return BlockPlacer.getBlockAt(blockPos) == Blocks.BEDROCK;
        });
        if (BlockPos.stream(boundingBox.shrink(Double.longBitsToDouble(4587366580439587226L), Double.longBitsToDouble(4587366580439587226L), Double.longBitsToDouble(4587366580439587226L))).filter(blockPos2 -> {
            return (BlockPlacer.getBlockAt(blockPos2).getBlastResistance() < Float.intBitsToFloat(1142292480) || is724() || BlockPlacer.getBlockAt(blockPos2) == Blocks.AIR) ? false : true;
        }).toList().isEmpty()) {
            AtomicReference atomicReference = new AtomicReference((BlockPos) BlockPos.stream(boundingBox.withMaxY(boundingBox.maxY + Double.longBitsToDouble(4602678819172646912L))).map((v0) -> {
                return v0.toImmutable();
            }).filter(this::isValidHeadTarget).max(Comparator.comparing(blockPos3 -> {
                return Boolean.valueOf(this.isSpeedMineTarget(blockPos3));
            })).orElse(null));
            AtomicReference atomicReference2 = new AtomicReference((BlockPos) PlayerUtils.getSurroundPositions(playerEntity886).stream().map((v0) -> {
                return v0.up();
            }).map((v0) -> {
                return v0.toImmutable();
            }).filter(this::isValidHeadTarget).max(Comparator.comparing(blockPos4 -> {
                return Boolean.valueOf(this.isSpeedMineTarget(blockPos4));
            })).orElse(null));
            if (atomicReference.get() == null && atomicReference2.get() != null && !is724()) {
                atomicReference.set((BlockPos) atomicReference2.get());
                atomicReference2.set(null);
            }
            if (atomicReference.get() == null) {
                return;
            }
            int i2 = 599;
            if (is724()) {
                if (anyMatch) {
                    i = isExtraBreakEnabled ? 900 : 1000;
                } else {
                    i = isExtraBreakEnabled ? 499 : 599;
                }
                autoMineHelper.tryUpdatePriority(i, autoMineHelper2 -> {
                    if (!isExtraBreakEnabled) {
                        autoMineHelper2.setBlockPos((BlockPos) atomicReference.get());
                    } else if (atomicReference2.get() == null) {
                        autoMineHelper2.setBlockPos((BlockPos) atomicReference.get());
                    } else {
                        autoMineHelper2.setSecondaryBlockPos((BlockPos) atomicReference.get());
                        autoMineHelper2.setBlockPos((BlockPos) atomicReference2.get());
                    }
                });
                return;
            }
            if (isSpeedMineTarget((BlockPos) atomicReference.get())) {
                i2 = 650;
            }
            if (this.autoMine.trapOverride.getValue().booleanValue() && range.isToggled()) {
                i2 = 650;
            }
            autoMineHelper.tryUpdatePriority(i2, autoMineHelper3 -> {
                autoMineHelper3.setBlockPos((BlockPos) atomicReference.get());
            });
        }
    }

    @Override // me.mioclient.module.combat.automine.MineStrategyBase, me.mioclient.module.combat.automine.MineTargetStrategy
    public boolean isEnabled() {
        return this.autoMine.head.getValue().booleanValue();
    }

    public boolean isValidHeadTarget(BlockPos blockPos) {
        return ((WorldUtils.isBreakable(blockPos) && (BlockPlacer.getBlockAt(blockPos) == Blocks.OBSIDIAN || is724())) || isSpeedMineTarget(blockPos)) && (minecraftClient.world.isAir(blockPos.up()) || is724());
    }

    public boolean is724() {
        return this.autoMine.logic.getValue() == AutoMineLogic.GRIMV3;
    }
}
