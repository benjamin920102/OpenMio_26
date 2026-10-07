package me.mioclient.util.world;

import me.mioclient.mode.world.ReachMode;

import java.util.Collections;
import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public interface BlockRaycastFilter {
    public static final BlockRaycastFilter holeSnapHelper = (blockPos, blockState) -> {
        return ReachMode.NONE;
    };
    public static final BlockRaycastFilter holeSnapHelper2 = (blockPos, blockState) -> {
        // 26.2 port: Block.getBlastResistance() (no-arg) replaces Yarn getBlastResistance().
        return blockState.getBlock().getExplosionResistance() < 600.0f ? ReachMode.IGNORE : ReachMode.NONE;
    };

    ReachMode test(BlockPos blockPos, BlockState blockState);

    static BlockRaycastFilter getHoleSnapHelper1674(BlockPos blockPos) {
        return createIgnoreFilter(Collections.singleton(blockPos));
    }

    static BlockRaycastFilter createIgnoreFilter(Iterable<BlockPos> iterable) {
        return (blockPos, blockState) -> {
            Iterator it = iterable.iterator();
            while (it.hasNext()) {
                if (blockPos.equals((BlockPos) it.next())) {
                    return ReachMode.IGNORE;
                }
            }
            return ReachMode.NONE;
        };
    }

    static BlockRaycastFilter getHoleSnapHelper1676(BlockPos blockPos) {
        return createSolidFilter(Collections.singleton(blockPos));
    }

    static BlockRaycastFilter createSolidFilter(Iterable<BlockPos> iterable) {
        return (blockPos, blockState) -> {
            Iterator it = iterable.iterator();
            while (it.hasNext()) {
                if (blockPos.equals((BlockPos) it.next())) {
                    return ReachMode.SOLID;
                }
            }
            return ReachMode.NONE;
        };
    }

    static BlockRaycastFilter getHoleSnapHelper1678(BlockRaycastFilter... holeSnapHelperArr) {
        return (blockPos, blockState) -> {
            for (BlockRaycastFilter holeSnapHelper3 : holeSnapHelperArr) {
                ReachMode test = holeSnapHelper3.test(blockPos, blockState);
                if (test != ReachMode.NONE) {
                    return test;
                }
            }
            return ReachMode.NONE;
        };
    }
}
