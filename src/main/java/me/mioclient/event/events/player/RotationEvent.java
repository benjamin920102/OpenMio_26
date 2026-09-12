package me.mioclient.event.events.player;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/feature/Event_3.class */
public final class RotationEvent extends me.mioclient.event.Event {
    public float yaw;
    public float pitch;
    public boolean flag;

    public RotationEvent(float f, float f2, boolean z) {
        this.yaw = f;
        this.pitch = f2;
        this.flag = z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float f) {
        this.yaw = f;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float f) {
        this.pitch = f;
    }

    public boolean isCurrent() {
        return this.flag;
    }
}
