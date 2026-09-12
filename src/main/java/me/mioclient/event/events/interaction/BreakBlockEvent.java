package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/BreakBlockEvent.class */
public class BreakBlockEvent extends Event {
    public BlockPos blockPos;

    public BreakBlockEvent(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}
