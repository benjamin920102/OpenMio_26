package me.mioclient.module.combat.antiphase;

import me.mioclient.util.math.BoxMath;

import me.mioclient.util.math.BoxMath;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PermissiveLadderTargetResolver.class */
public class PermissiveLadderTargetResolver extends LadderTargetResolver {
    @Override // me.mioclient.module.combat.antiphase.LadderTargetResolver
    public boolean isPositionBlocked(BlockPos blockPos, PlayerEntity playerEntity, LadderShapeEntry antiPhaseHelperData) {
        return BoxMath.getServerBox((LivingEntity) playerEntity).intersects(antiPhaseHelperData.getBox().offset(blockPos));
    }
}
