package me.mioclient.module.combat.automine;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.PlayerUtils;

import me.mioclient.module.combat.AutoMine;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

public final class FloorTargetStrategy extends MineStrategyBase {
    public FloorTargetStrategy(AutoMine autoMine) {
        super(autoMine);
    }

    @Override // me.mioclient.module.combat.automine.MineTargetStrategy
    public void findTargets(MineTargetState autoMineHelper) {
        LivingEntity playerEntity886 = this.autoMine.autoMineSearchHelper4.getTargetPlayer();
        if (playerEntity886 != null && BlockPos.betweenClosedStream(this.autoMine.autoMineSearchHelper4.getFeetBox((Player) playerEntity886)).count() == 1) {
            BlockPos blockPos2008 = PlayerUtils.getRoundedBlockPos(playerEntity886);
            if (WorldUtils.isAir(blockPos2008) || WorldUtils.isBreakable(blockPos2008) || !WorldUtils.isBreakable(blockPos2008.below())) {
                return;
            }
            autoMineHelper.tryUpdatePriority(400, autoMineHelper2 -> {
                if (this.autoMine.isExtraBreakEnabled()) {
                    autoMineHelper2.setSecondaryBlockPos(blockPos2008.below());
                } else {
                    autoMineHelper2.setBlockPos(blockPos2008.below());
                }
            });
        }
    }

    @Override // me.mioclient.module.combat.automine.MineStrategyBase, me.mioclient.module.combat.automine.MineTargetStrategy
    public boolean isEnabled() {
        return this.autoMine.fish.getValue().booleanValue();
    }
}