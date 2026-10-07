package me.mioclient.module.combat.antiphase;

import me.mioclient.module.combat.antiphase.LadderShapeEntry;
import me.mioclient.module.combat.antiphase.SolidBlockTargetResolver;

import java.util.List;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;

public class FacePlateTargetResolver extends SolidBlockTargetResolver {
    public static final List<LadderShapeEntry> list = List.of(LadderShapeEntry.fromShape(Direction.EAST, Shapes.box(0.0d, Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L))), LadderShapeEntry.fromShape(Direction.WEST, Shapes.box(Double.longBitsToDouble(4624633867356078080L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4625196817309499392L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L))), LadderShapeEntry.fromShape(Direction.NORTH, Shapes.box(Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), 0.0d, Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4607182418800017408L))), LadderShapeEntry.fromShape(Direction.SOUTH, Shapes.box(Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624633867356078080L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4625196817309499392L))), LadderShapeEntry.fromShape(Direction.DOWN, Shapes.box(Double.longBitsToDouble(4611686018427387904L), 0.0d, Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4624070917402656768L))), LadderShapeEntry.fromShape(Direction.UP, Shapes.box(Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624633867356078080L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4625196817309499392L), Double.longBitsToDouble(4624070917402656768L))));

    @Override // me.mioclient.module.combat.antiphase.SolidBlockTargetResolver
    public AABB getTargetBox(BlockPos blockPos, Direction direction) {
        for (LadderShapeEntry antiPhaseHelperData : list) {
            if (antiPhaseHelperData.getDirection() == direction.getOpposite()) {
                return antiPhaseHelperData.getBox().move(blockPos);
            }
        }
        return new AABB(blockPos);
    }
}