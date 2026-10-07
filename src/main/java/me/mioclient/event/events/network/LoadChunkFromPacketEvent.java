package me.mioclient.event.events.network;

import me.mioclient.event.Event;

import net.minecraft.world.level.chunk.LevelChunk;

public class LoadChunkFromPacketEvent extends Event {
    public final LevelChunk worldChunk;

    public LoadChunkFromPacketEvent(LevelChunk worldChunk) {
        this.worldChunk = worldChunk;
    }

    public LevelChunk getChunk() {
        return this.worldChunk;
    }
}
