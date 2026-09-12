package me.mioclient.module.combat.automine;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.PlayerUtils;

import me.mioclient.util.player.PlayerUtils;

import me.mioclient.module.combat.AutoMine;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FloorTargetStrategy.class */
public final class FloorTargetStrategy extends MineStrategyBase {
    public FloorTargetStrategy(AutoMine autoMine) {
        super(autoMine);
    }

    @Override // me.mioclient.module.combat.automine.MineTargetStrategy
    public void findTargets(MineTargetState autoMineHelper) {
        LivingEntity playerEntity886 = this.autoMine.autoMineSearchHelper4.getTargetPlayer();
        if (playerEntity886 != null && BlockPos.stream(this.autoMine.autoMineSearchHelper4.getFeetBox((PlayerEntity) playerEntity886)).count() == 1) {
            BlockPos blockPos2008 = PlayerUtils.getRoundedBlockPos(playerEntity886);
            if (WorldUtils.isAir(blockPos2008) || WorldUtils.isBreakable(blockPos2008) || !WorldUtils.isBreakable(blockPos2008.down())) {
                return;
            }
            autoMineHelper.tryUpdatePriority(400, autoMineHelper2 -> {
                if (this.autoMine.isExtraBreakEnabled()) {
                    autoMineHelper2.setSecondaryBlockPos(blockPos2008.down());
                } else {
                    autoMineHelper2.setBlockPos(blockPos2008.down());
                }
            });
        }
    }

    @Override // me.mioclient.module.combat.automine.MineStrategyBase, me.mioclient.module.combat.automine.MineTargetStrategy
    public boolean isEnabled() {
        return this.autoMine.fish.getValue().booleanValue();
    }
}
