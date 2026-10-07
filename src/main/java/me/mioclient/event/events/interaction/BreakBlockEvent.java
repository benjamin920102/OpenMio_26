package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.core.BlockPos;

public class BreakBlockEvent extends Event {
    public BlockPos blockPos;

    public BreakBlockEvent(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}
