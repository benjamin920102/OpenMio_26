package me.mioclient.util.anim;

import me.mioclient.api.ClientServices;

import java.util.function.Supplier;
import net.minecraft.util.Mth;

public class AnimationProgress extends InterpolatedFloat {
    public AnimationProgress(float f, boolean z) {
        super(f, z);
    }

    public AnimationProgress(float f) {
        super(f);
    }

    public AnimationProgress(Supplier<Float> supplier, boolean z) {
        super(supplier, z);
    }

    @Override // me.mioclient.util.anim.InterpolatedFloat
    public float getValue() {
        float f = ClientServices.hitmarkerHelper.getFrameDelta(Float.intBitsToFloat(1056964608)) * this.supplier.get().floatValue();
        if (this.flag) {
            double longBitsToDouble = Double.longBitsToDouble(4607182418800017408L);
            float intBitsToFloat = f - Float.intBitsToFloat(1065353216);
            f = (float) (longBitsToDouble - (intBitsToFloat * Math.pow(intBitsToFloat, Double.longBitsToDouble(4613937818241073152L))));
        }
        this.val = this.val3 + ((this.val - this.val3) * f);
        if (this.val == this.val2 || Math.abs(this.val2 - this.val) < Double.longBitsToDouble(4576918229304087675L)) {
            setImmediate(this.val2);
        }
        if (ClientServices.hitmarkerHelper.getFrameRate() <= 15) {
            this.val = this.val2;
        }
        return Mth.clamp(this.val - Float.intBitsToFloat(1065353216), 0.0f, Float.intBitsToFloat(1065353216));
    }

    @Override // me.mioclient.util.anim.InterpolatedFloat
    public void animate(float f) {
        super.animate(f + Float.intBitsToFloat(1065353216));
    }

    public float getRawValue() {
        return this.val;
    }

    public void animateToggle(boolean z) {
        animate(z ? Float.intBitsToFloat(1065353216) : 0.0f);
    }

    public void setToggledImmediate(boolean z) {
        setImmediate(z ? Float.intBitsToFloat(1073741824) : Float.intBitsToFloat(1065353216));
    }

    public void reset() {
        animate(0.0f);
    }
}
