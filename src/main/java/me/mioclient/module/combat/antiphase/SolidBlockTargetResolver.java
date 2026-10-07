package me.mioclient.module.combat.antiphase;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.module.combat.AntiPhase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;

public class SolidBlockTargetResolver implements AntiPhaseTargetResolver {
    @Override // me.mioclient.module.combat.antiphase.AntiPhaseTargetResolver
    public AntiPhasePlacement resolveTarget(AntiPhase antiPhase, Player playerEntity) {
        BlockPos blockPos2008 = PlayerUtils.getRoundedBlockPos((LivingEntity) playerEntity);
        Direction direction3029 = BlockPlacer.findSupportSide(blockPos2008, antiPhase.strictDirection.getValue().booleanValue());
        if (direction3029 == null || !WorldUtils.isAir(blockPos2008)) {
            return null;
        }
        return new AntiPhasePlacement(blockPos2008.relative(direction3029), direction3029.getOpposite(), getTargetBox(blockPos2008, direction3029));
    }

    public AABB getTargetBox(BlockPos blockPos, Direction direction) {
        return new AABB(blockPos);
    }
}