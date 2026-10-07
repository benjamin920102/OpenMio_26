package me.mioclient.util.anim;

import net.minecraft.util.Mth;

public class ValueAnimator {
    public float startValue;
    public float targetValue;
    public long startTime;
    public long duration;

    public void animateTo(float f, long j) {
        if (this.targetValue == f) {
            return;
        }
        this.startValue = getValue();
        this.targetValue = f;
        this.startTime = System.currentTimeMillis();
        this.duration = j;
    }

    public void toggle(boolean z, long j) {
        animateTo(z ? Float.intBitsToFloat(1065353216) : 0.0f, j);
    }

    public void setImmediate(float f) {
        animateTo(f, 0L);
    }

    public float getValue() {
        return ((Float.intBitsToFloat(1065353216) - Mth.clamp(((float) ((this.startTime + this.duration) - System.currentTimeMillis())) / ((float) Math.max(this.duration, 1L)), 0.0f, Float.intBitsToFloat(1065353216))) * (this.targetValue - this.startValue)) + this.startValue;
    }
}
