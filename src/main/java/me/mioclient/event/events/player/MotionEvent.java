package me.mioclient.event.events.player;

import me.mioclient.mode.combat.KeyPearlMode;

public final class MotionEvent extends me.mioclient.event.Event {
    public final KeyPearlMode keyPearlMode;
    public final double val;
    public final double val2;
    public final double val3;
    public final float val4;
    public final float val5;
    public final boolean flag;
    public double val6;
    public double val7;
    public double val8;
    public float val9;
    public float val10;
    public boolean flag2;
    public boolean flag3;

    public MotionEvent() {
        this(KeyPearlMode.Pre, 0.0d, 0.0d, 0.0d, 0.0f, 0.0f, false);
    }

    public MotionEvent(KeyPearlMode keyPearlMode, MotionEvent motionEvent) {
        this(keyPearlMode, motionEvent.val6, motionEvent.val7, motionEvent.val8, motionEvent.val9, motionEvent.val10, motionEvent.flag2);
    }

    public MotionEvent(KeyPearlMode keyPearlMode, double d, double d2, double d3, float f, float f2, boolean z) {
        this.keyPearlMode = keyPearlMode;
        this.val6 = d;
        this.val7 = d2;
        this.val8 = d3;
        this.val9 = f;
        this.val10 = f2;
        this.flag2 = z;
        this.val = d;
        this.val2 = d2;
        this.val3 = d3;
        this.val4 = f;
        this.val5 = f2;
        this.flag = z;
    }

    public double getOriginalX() {
        return this.val;
    }

    public double getOriginalY() {
        return this.val2;
    }

    public double getOriginalZ() {
        return this.val3;
    }

    public float getOriginalYaw() {
        return this.val4;
    }

    public float getOriginalPitch() {
        return this.val5;
    }

    public boolean isPreMotion() {
        return this.flag;
    }

    public float getRotationYaw() {
        return this.val9;
    }

    public float getRotationPitch() {
        return this.val10;
    }

    public boolean hasModifiedRotation() {
        return this.flag3;
    }

    public double getX() {
        return this.val6;
    }

    public void setX(double d) {
        this.flag3 = true;
        this.val6 = d;
    }

    public double getY() {
        return this.val7;
    }

    public void setY(double d) {
        this.flag3 = true;
        this.val7 = d;
    }

    public double getZ() {
        return this.val8;
    }

    public void setZ(double d) {
        this.flag3 = true;
        this.val8 = d;
    }

    public float getYaw() {
        return this.val9;
    }

    public void setYaw(float f) {
        this.flag3 = true;
        this.val9 = f;
    }

    public float getPitch() {
        return this.val10;
    }

    public void setPitch(float f) {
        this.flag3 = true;
        this.val10 = f;
    }

    public void setRotation(float[] fArr) {
        if (fArr == null || fArr.length != 2) {
            return;
        }
        setYaw(fArr[0]);
        setPitch(fArr[1]);
    }

    public boolean isOnGround() {
        return this.flag2;
    }

    public void setOnGround(boolean z) {
        this.flag3 = true;
        this.flag2 = z;
    }

    public KeyPearlMode getPhase() {
        return this.keyPearlMode;
    }
}
