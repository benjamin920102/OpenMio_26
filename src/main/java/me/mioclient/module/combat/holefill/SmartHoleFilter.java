package me.mioclient.module.combat.holefill;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.Vec3dUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.HoleData;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.HoleData;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.module.combat.HoleFill;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SmartHoleFilter.class */
public class SmartHoleFilter extends RangeHoleFilter {
    public SmartHoleFilter(HoleFill holeFill) {
        super(holeFill);
    }

    @Override // me.mioclient.module.combat.holefill.RangeHoleFilter, me.mioclient.module.combat.holefill.HoleFilter
    public boolean isWithinRange(HoleData holeSnapData) {
        for (LivingEntity livingEntity : minecraftClient.world.getPlayers()) {
            if (!this.holeFill.ignoreNakeds.getValue().booleanValue() || PlayerUtils.hasArmor(livingEntity)) {
                Box box1109 = this.holeFill.extrapolation.getValue().booleanValue() ? ClientServices.mainhandHelper.getPredictedBox((PlayerEntity) livingEntity, BoxMath.getSnapDivisions((PlayerEntity) livingEntity, this.holeFill.ticks)) : BoxMath.getServerBox(livingEntity);
                boolean z = !MovementUtils.isPlayerMoving((PlayerEntity) livingEntity) || MovementUtils.isEntityMovingToward((PlayerEntity) livingEntity, holeSnapData.getBlockPos().toCenterPos());
                if (Vec3dUtils.getHorizontalDistance(holeSnapData.getBlockPos().toCenterPos(), BoxMath.getBottomCenter(box1109)) <= this.holeFill.smartRange.getValue().floatValue() && ((AbstractClientPlayerEntity) livingEntity).getY() > holeSnapData.getBlockPos().getY() && box1109.minY - holeSnapData.getBlockPos().getY() <= Double.longBitsToDouble(4616189618054758400L) && !((AbstractClientPlayerEntity) livingEntity).isDead() && minecraftClient.player != livingEntity) {
                    if (ClientServices.socialManager.isFriend(((AbstractClientPlayerEntity) livingEntity).getGameProfile().getName())) {
                        continue;
                    } else if (!ClientServices.holeSnapOverlayHelper.isHoleAt(BlockPos.ofFloored(((AbstractClientPlayerEntity) livingEntity).getPos())) && z) {
                        if (this.holeFill.selfSafety.getValue().booleanValue()) {
                            if (!ClientServices.holeSnapOverlayHelper.isHoleAt(BlockPos.ofFloored(minecraftClient.player.getPos()))) {
                                if (minecraftClient.player.getPos().distanceTo(holeSnapData.getBlockPos().toCenterPos()) < Double.longBitsToDouble(4611686018427387904L)) {
                                }
                            }
                        }
                        return super.isWithinRange(holeSnapData);
                    }
                }
            }
        }
        return false;
    }
}
