package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class AttackBlockEvent extends Event {
    public final BlockPos blockPos;
    public final Direction direction;

    public AttackBlockEvent(BlockPos blockPos, Direction direction) {
        this.blockPos = blockPos;
        this.direction = direction;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public Direction getDirection() {
        return this.direction;
    }
}
