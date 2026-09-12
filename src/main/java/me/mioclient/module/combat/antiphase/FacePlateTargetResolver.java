package me.mioclient.module.combat.antiphase;

import me.mioclient.module.combat.antiphase.LadderShapeEntry;
import me.mioclient.module.combat.antiphase.SolidBlockTargetResolver;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AntiPhaseHelper3_2.class */
public class FacePlateTargetResolver extends SolidBlockTargetResolver {
    public static final List<LadderShapeEntry> list = List.of(LadderShapeEntry.fromShape(Direction.EAST, Block.createCuboidShape(0.0d, Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L))), LadderShapeEntry.fromShape(Direction.WEST, Block.createCuboidShape(Double.longBitsToDouble(4624633867356078080L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4625196817309499392L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L))), LadderShapeEntry.fromShape(Direction.NORTH, Block.createCuboidShape(Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), 0.0d, Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4607182418800017408L))), LadderShapeEntry.fromShape(Direction.SOUTH, Block.createCuboidShape(Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624633867356078080L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4625196817309499392L))), LadderShapeEntry.fromShape(Direction.DOWN, Block.createCuboidShape(Double.longBitsToDouble(4611686018427387904L), 0.0d, Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4624070917402656768L))), LadderShapeEntry.fromShape(Direction.UP, Block.createCuboidShape(Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624633867356078080L), Double.longBitsToDouble(4611686018427387904L), Double.longBitsToDouble(4624070917402656768L), Double.longBitsToDouble(4625196817309499392L), Double.longBitsToDouble(4624070917402656768L))));

    @Override // me.mioclient.module.combat.antiphase.SolidBlockTargetResolver
    public Box getTargetBox(BlockPos blockPos, Direction direction) {
        for (LadderShapeEntry antiPhaseHelperData : list) {
            if (antiPhaseHelperData.getDirection() == direction.getOpposite()) {
                return antiPhaseHelperData.getBox().offset(blockPos);
            }
        }
        return new Box(blockPos);
    }
}
