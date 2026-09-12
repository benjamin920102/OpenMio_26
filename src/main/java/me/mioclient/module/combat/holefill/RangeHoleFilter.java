package me.mioclient.module.combat.holefill;

import me.mioclient.util.world.HoleData;

import me.mioclient.util.world.HoleData;

import me.mioclient.module.combat.HoleFill;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/RangeHoleFilter.class */
public class RangeHoleFilter implements HoleFilter {
    public final HoleFill holeFill;

    public RangeHoleFilter(HoleFill holeFill) {
        this.holeFill = holeFill;
    }

    @Override // me.mioclient.module.combat.holefill.HoleFilter
    public boolean isWithinRange(HoleData holeSnapData) {
        if ((holeSnapData.getBox().getLengthX() == Double.longBitsToDouble(4607182418800017408L) && holeSnapData.getBox().getLengthZ() == Double.longBitsToDouble(4607182418800017408L)) || this.holeFill.doubles.getValue().booleanValue()) {
            if (minecraftClient.player.getEyePos().distanceTo(holeSnapData.getBlockPos().toCenterPos()) <= this.holeFill.range.getValue().floatValue() && Math.abs(minecraftClient.player.getEyePos().y - holeSnapData.getBlockPos().toCenterPos().y) <= this.holeFill.verticalRange.getValue().floatValue()) {
                return true;
            }
        }
        return false;
    }
}
