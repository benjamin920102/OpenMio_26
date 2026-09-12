package me.mioclient.util.math;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Data_3.class */
public final class Rectangle {
    public final float val;
    public final float val2;
    public final float val3;
    public final float val4;

    public Rectangle(float f, float f2, float f3, float f4) {
        this.val = f;
        this.val2 = f2;
        this.val3 = f3;
        this.val4 = f4;
    }

    public static Rectangle fromPositionAndSize(float f, float f2, float f3, float f4) {
        return new Rectangle(f, f2, f + f3, f2 + f4);
    }

    public float getWidth() {
        return this.val3 - this.val;
    }

    public float getHeight() {
        return this.val4 - this.val2;
    }

    public boolean containsPoint(double d, double d2) {
        return d >= ((double) this.val) && d <= ((double) this.val3) && d2 >= ((double) this.val2) && d2 <= ((double) this.val4);
    }

    public boolean overlaps(Rectangle other) {
        return (this.val3 < this.val || this.val3 > other.getMinX()) && (this.val4 < this.val2 || this.val4 > other.getMinY()) && ((other.getMaxX() < other.getMinX() || other.getMaxX() > this.val) && (other.getMaxY() < other.getMinY() || other.getMaxY() > this.val2));
    }

    public Rectangle expand(float f) {
        return expand(f, f);
    }

    public Rectangle expand(float f, float f2) {
        return new Rectangle(getMinX() - f, getMinY() - f2, getMaxX() + f, getMaxY() + f2);
    }

    public Rectangle merge(Rectangle... data_3Arr) {
        float f = getMinX();
        float f2 = getMaxX();
        float f3 = getMinY();
        float f4 = getMaxY();
        for (Rectangle data_3 : data_3Arr) {
            f = Math.min(f, data_3.getMinX());
            f3 = Math.min(f3, data_3.getMinY());
            f2 = Math.max(f2, data_3.getMaxX());
            f4 = Math.max(f4, data_3.getMaxY());
        }
        return new Rectangle(f, f3, f2, f4);
    }




    public float getMinX() {
        return this.val;
    }

    public float getMinY() {
        return this.val2;
    }

    public float getMaxX() {
        return this.val3;
    }

    public float getMaxY() {
        return this.val4;
    }
}
