package me.mioclient.event.events.player;

import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.event.Event;

import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/MoveEvent.class */
public class MoveEvent extends Event {
    public final Vec3d vec3d;
    public final Vec3d vec3d2;
    public MovementType movementType;

    public MoveEvent(Vec3d vec3d, MovementType movementType) {
        this.vec3d = new Vec3d(vec3d.x, vec3d.y, vec3d.z);
        this.vec3d2 = vec3d;
        this.movementType = movementType;
    }

    public Vec3d getMovement() {
        return this.vec3d2;
    }

    public void setMovement(Vec3d vec3d) {
        Vec3dUtils.setVectorComponents(this.vec3d2, vec3d.x, vec3d.y, vec3d.z);
    }

    public void setHorizontal(double d, double d2) {
        setMovement(new Vec3d(d, getY(), d2));
    }

    public double getX() {
        return this.vec3d2.x;
    }

    public double getY() {
        return this.vec3d2.y;
    }

    public void setY(double d) {
        setMovement(new Vec3d(getX(), d, getZ()));
    }

    public double getZ() {
        return this.vec3d2.z;
    }

    public MovementType getMovementType() {
        return this.movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Vec3d getOriginalMovement() {
        return this.vec3d;
    }
}
