package me.mioclient.module.combat.antiphase;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.module.combat.AntiPhase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SolidBlockTargetResolver.class */
public class SolidBlockTargetResolver implements AntiPhaseTargetResolver {
    @Override // me.mioclient.module.combat.antiphase.AntiPhaseTargetResolver
    public AntiPhasePlacement resolveTarget(AntiPhase antiPhase, PlayerEntity playerEntity) {
        BlockPos blockPos2008 = PlayerUtils.getRoundedBlockPos((LivingEntity) playerEntity);
        Direction direction3029 = BlockPlacer.findSupportSide(blockPos2008, antiPhase.strictDirection.getValue().booleanValue());
        if (direction3029 == null || !WorldUtils.isAir(blockPos2008)) {
            return null;
        }
        return new AntiPhasePlacement(blockPos2008.offset(direction3029), direction3029.getOpposite(), getTargetBox(blockPos2008, direction3029));
    }

    public Box getTargetBox(BlockPos blockPos, Direction direction) {
        return new Box(blockPos);
    }
}
