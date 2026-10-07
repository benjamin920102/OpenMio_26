package me.mioclient.event.events.network;

import me.mioclient.event.Event;

import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.phys.Vec3;

/* 26.2 port: ClientboundExplodePacket is now a record (center/radius/knockback);
   knockback overrides are applied by redispatching a rebuilt packet (see mixin). */
public class ExplosionVelocityEvent extends Event {
    public final double val;
    public final double val2;
    public final double val3;
    public final float val4;
    public float val5;
    public float val6;
    public float val7;

    public ExplosionVelocityEvent(ClientboundExplodePacket explosionS2CPacket) {
        this(explosionS2CPacket.center().x, explosionS2CPacket.center().y, explosionS2CPacket.center().z, explosionS2CPacket.radius(), (float) explosionS2CPacket.playerKnockback().orElse(Vec3.ZERO).x, (float) explosionS2CPacket.playerKnockback().orElse(Vec3.ZERO).y, (float) explosionS2CPacket.playerKnockback().orElse(Vec3.ZERO).z);
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

    public Vec3 getKnockback() {
        return new Vec3(this.val5, this.val6, this.val7);
    }
}
