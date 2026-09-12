package me.mioclient.util.time;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.util.LinkedList;
import net.minecraft.util.math.MathHelper;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FrameRateTracker.class */
public final class FrameRateTracker implements ClientAccessor {
    public final LinkedList<Long> linkedList = new LinkedList<>();
    public int num;

    public FrameRateTracker() {
        eventBus.register(this);
    }

    public void updateFrameCount() {
        long nanoTime = System.nanoTime();
        this.linkedList.add(Long.valueOf(nanoTime));
        while (nanoTime - this.linkedList.getFirst().longValue() > 1000000000) {
            this.linkedList.remove();
        }
        this.num = this.linkedList.size();
    }

    public int getFrameRate() {
        return this.num;
    }

    public float getFrameDelta(float f) {
        return MathHelper.clamp(Float.intBitsToFloat(1065353216) / (this.num * f), 0.0f, Float.intBitsToFloat(1065353216));
    }
}
