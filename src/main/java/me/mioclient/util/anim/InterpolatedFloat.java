package me.mioclient.util.anim;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.MathConstants;

import java.util.function.Supplier;

public class InterpolatedFloat {
    public float val;
    public float val2;
    public float val3;
    public boolean flag;
    public Supplier<Float> supplier;

    public InterpolatedFloat(float f, boolean z) {
        this.supplier = () -> {
            return Float.valueOf(f);
        };
        this.flag = z;
    }

    public InterpolatedFloat(float f) {
        this(f, true);
    }

    public InterpolatedFloat(Supplier<Float> supplier, boolean z) {
        this.supplier = supplier;
        this.flag = z;
    }

    public float getValue() {
        float f = ClientServices.hitmarkerHelper.getFrameDelta(Float.intBitsToFloat(1056964608)) * this.supplier.get().floatValue();
        if (this.flag) {
            double longBitsToDouble = Double.longBitsToDouble(4607182418800017408L);
            float intBitsToFloat = f - Float.intBitsToFloat(1065353216);
            f = (float) (longBitsToDouble - (intBitsToFloat * Math.pow(intBitsToFloat, Double.longBitsToDouble(4613937818241073152L))));
        }
        this.val = this.val3 + ((this.val - this.val3) * f);
        if (this.val == this.val2 || Math.abs(this.val2 - this.val) < MathConstants.val2) {
            setImmediate(this.val2);
        }
        if (ClientServices.hitmarkerHelper.getFrameRate() <= 15) {
            this.val = this.val2;
        }
        return this.val;
    }

    public void animate(float f) {
        if (this.val == f) {
            return;
        }
        this.val3 = this.val;
        this.val = f;
        this.val2 = f;
    }

    public void setImmediate(float f) {
        if (this.val == f) {
            return;
        }
        this.val3 = f;
        this.val = f;
        this.val2 = f;
    }

    public Supplier<Float> getValueSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier<Float> supplier) {
        this.supplier = supplier;
    }

    public void setEnabled(boolean z) {
        this.flag = z;
    }
}
