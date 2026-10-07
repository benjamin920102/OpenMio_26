package me.mioclient.util.player;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

public final class RotationRequest {
    public final float[] floatArr;
    public final int num;

    public RotationRequest(float[] fArr, int i) {
        this.floatArr = fArr;
        this.num = i;
    }

    public float[] getRotationAngles() {
        return this.floatArr;
    }

    public int get219() {
        return this.num;
    }
}
