package me.mioclient.module.combat.holefill;
import net.minecraft.world.phys.Vec3;

import me.mioclient.util.world.HoleData;

import me.mioclient.module.combat.HoleFill;

public class RangeHoleFilter implements HoleFilter {
    public final HoleFill holeFill;

    public RangeHoleFilter(HoleFill holeFill) {
        this.holeFill = holeFill;
    }

    @Override // me.mioclient.module.combat.holefill.HoleFilter
    public boolean isWithinRange(HoleData holeSnapData) {
        if ((holeSnapData.getBox().getXsize() == Double.longBitsToDouble(4607182418800017408L) && holeSnapData.getBox().getZsize() == Double.longBitsToDouble(4607182418800017408L)) || this.holeFill.doubles.getValue().booleanValue()) {
            if (minecraftClient.player.getEyePosition().distanceTo(Vec3.atCenterOf(holeSnapData.getBlockPos())) <= this.holeFill.range.getValue().floatValue() && Math.abs(minecraftClient.player.getEyePosition().y - Vec3.atCenterOf(holeSnapData.getBlockPos()).y) <= this.holeFill.verticalRange.getValue().floatValue()) {
                return true;
            }
        }
        return false;
    }
}