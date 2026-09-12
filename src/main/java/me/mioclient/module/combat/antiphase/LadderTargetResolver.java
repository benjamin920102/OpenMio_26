package me.mioclient.module.combat.antiphase;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.PlayerUtils;

import java.util.List;
import me.mioclient.module.combat.AntiPhase;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/LadderTargetResolver.class */
public class LadderTargetResolver implements ClientAccessor, AntiPhaseTargetResolver {
    public static final List<LadderShapeEntry> list = List.of(LadderShapeEntry.fromShape(Direction.EAST, LadderBlock.EAST_SHAPE), LadderShapeEntry.fromShape(Direction.WEST, LadderBlock.WEST_SHAPE), LadderShapeEntry.fromShape(Direction.NORTH, LadderBlock.NORTH_SHAPE), LadderShapeEntry.fromShape(Direction.SOUTH, LadderBlock.SOUTH_SHAPE));

    @Override // me.mioclient.module.combat.antiphase.AntiPhaseTargetResolver
    public AntiPhasePlacement resolveTarget(AntiPhase antiPhase, PlayerEntity playerEntity) {
        BlockPos blockPos2008 = PlayerUtils.getRoundedBlockPos((LivingEntity) playerEntity);
        Direction direction = null;
        Box box = null;
        float intBitsToFloat = Float.intBitsToFloat(1148829696);
        if (!WorldUtils.isAir(blockPos2008)) {
            return null;
        }
        for (LadderShapeEntry antiPhaseHelperData : list) {
            Direction direction842 = antiPhaseHelperData.getDirection();
            BlockPos offset = blockPos2008.offset(direction842);
            if (!isPositionBlocked(blockPos2008, playerEntity, antiPhaseHelperData) && (!antiPhase.strictDirection.getValue().booleanValue() || !isSideBlocked(offset, direction842))) {
                float squaredDistanceTo = (float) playerEntity.getPos().squaredDistanceTo(offset.toCenterPos());
                if (squaredDistanceTo < intBitsToFloat) {
                    box = antiPhaseHelperData.getBox();
                    direction = direction842;
                    intBitsToFloat = squaredDistanceTo;
                }
            }
        }
        if (direction == null) {
            return null;
        }
        return new AntiPhasePlacement(blockPos2008.offset(direction), direction.getOpposite(), box.offset(blockPos2008));
    }

    public boolean isPositionBlocked(BlockPos blockPos, PlayerEntity playerEntity, LadderShapeEntry antiPhaseHelperData) {
        return BoxMath.getServerBox((LivingEntity) playerEntity).intersects(antiPhaseHelperData.getBox().offset(blockPos)) || minecraftClient.world.getBlockState(blockPos.offset(antiPhaseHelperData.getDirection())).isReplaceable();
    }

    public boolean isSideBlocked(BlockPos blockPos, Direction direction) {
        return !BlockPlacer.getInteractableSides(blockPos).contains(direction.getOpposite());
    }
}
