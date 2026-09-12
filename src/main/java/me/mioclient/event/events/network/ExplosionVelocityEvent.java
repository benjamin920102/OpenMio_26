package me.mioclient.event.events.network;

import me.mioclient.event.Event;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/ExplosionVelocityEvent.class */
public class ExplosionVelocityEvent extends Event {
    public final double val;
    public final double val2;
    public final double val3;
    public final float val4;
    public float val5;
    public float val6;
    public float val7;

    public ExplosionVelocityEvent(ExplosionS2CPacket explosionS2CPacket) {
        this(explosionS2CPacket.getX(), explosionS2CPacket.getY(), explosionS2CPacket.getZ(), explosionS2CPacket.getRadius(), explosionS2CPacket.getPlayerVelocityX(), explosionS2CPacket.getPlayerVelocityY(), explosionS2CPacket.getPlayerVelocityZ());
    }

    public ExplosionVelocityEvent(double d, double d2, double d3, float f, float f2, float f3, float f4) {
        this.val = d;
        this.val2 = d2;
        this.val3 = d3;
        this.val4 = f;
        this.val5 = f2;
        this.val6 = f3;
        this.val7 = f4;
    }

    public double getX() {
        return this.val;
    }

    public double getY() {
        return this.val2;
    }

    public double getZ() {
        return this.val3;
    }

    public float getRadius() {
        return this.val4;
    }

    public float getPlayerVelocityX() {
        return this.val5;
    }

    public void setPlayerVelocityX(float f) {
        this.val5 = f;
    }

    public float getPlayerVelocityY() {
        return this.val6;
    }

    public void setPlayerVelocityY(float f) {
        this.val6 = f;
    }

    public float getPlayerVelocityZ() {
        return this.val7;
    }

    public void setPlayerVelocityZ(float f) {
        this.val7 = f;
    }
}
