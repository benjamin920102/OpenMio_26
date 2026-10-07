package me.mioclient.module.combat.antiphase;

import me.mioclient.util.math.BoxMath;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

public class PermissiveLadderTargetResolver extends LadderTargetResolver {
    @Override // me.mioclient.module.combat.antiphase.LadderTargetResolver
    public boolean isPositionBlocked(BlockPos blockPos, Player playerEntity, LadderShapeEntry antiPhaseHelperData) {
        return BoxMath.getServerBox((LivingEntity) playerEntity).intersects(antiPhaseHelperData.getBox().move(blockPos));
    }
}