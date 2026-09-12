package me.mioclient.util.world;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.Block;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/TimedBlockEntry.class */
public final class TimedBlockEntry {
    public final long num;
    public final Block block;

    public TimedBlockEntry(long j, Block block) {
        this.num = j;
        this.block = block;
    }

    public static TimedBlockEntry getBlockData2589(Block block) {
        return new TimedBlockEntry(System.currentTimeMillis(), block);
    }




    public long getTimestamp() {
        return this.num;
    }

    public Block getBlockState() {
        return this.block;
    }
}
