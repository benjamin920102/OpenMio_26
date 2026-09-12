package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;

import me.mioclient.module.combat.automine.MineStrategyBase;
import me.mioclient.module.combat.automine.MineTargetState;

import me.mioclient.module.combat.AutoMine;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoMineSearchHelper42_4.class */
public final class EnderChestTargetStrategy extends MineStrategyBase {
    public EnderChestTargetStrategy(AutoMine autoMine) {
        super(autoMine);
    }

    @Override // me.mioclient.module.combat.automine.MineTargetStrategy
    public void findTargets(MineTargetState autoMineHelper) {
        BlockPos blockPos = null;
        for (BlockEntity blockEntity : ClientServices.stashFinderHelper.getLoadedBlockEntities()) {
            if (minecraftClient.player.getEyePos().distanceTo(blockEntity.getPos().toCenterPos()) <= Double.longBitsToDouble(4612136378390124954L) && (blockEntity instanceof EnderChestBlockEntity)) {
                if (isValidMineTarget(blockEntity.getPos())) {
                    if (blockPos != null) {
                        if (isSpeedMineTarget(blockEntity.getPos())) {
                        }
                    }
                    blockPos = blockEntity.getPos();
                }
            }
        }
        BlockPos blockPos2 = blockPos;
        if (blockPos2 == null) {
            return;
        }
        autoMineHelper.tryUpdatePriority(5, autoMineHelper2 -> {
            autoMineHelper2.setBlockPos(blockPos2);
        });
    }

    @Override // me.mioclient.module.combat.automine.MineStrategyBase, me.mioclient.module.combat.automine.MineTargetStrategy
    public boolean isEnabled() {
        return this.autoMine.enderChests.getValue().booleanValue();
    }
}
