package me.mioclient.ducks;

import net.minecraft.core.BlockPos;

public interface DuckClientPlayerInteractionManager {
    boolean isBreakingBlock();

    void setBreakingBlock(boolean z);

    float getBreakingProgress();

    void setBreakingProgress(float f);

    BlockPos getCurrentBreakingBlock();

    void setCurrentBreakingBlock(BlockPos blockPos);
}
