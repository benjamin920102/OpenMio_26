package me.mioclient.event.events.player;

import me.mioclient.event.Event;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

/* 26.2 port: Yarn MovementType -> MojMap MoverType, mutable Vec3 -> immutable Vec3. */
public class MoveEvent extends Event {
    public final Vec3 vec3d;
    public Vec3 vec3d2;
    public MoverType movementType;

    public MoveEvent(Vec3 vec3d, MoverType movementType) {
        this.vec3d = new Vec3(vec3d.x, vec3d.y, vec3d.z);
        this.vec3d2 = vec3d;
        this.movementType = movementType;
    }

    public Vec3 getMovement() {
        return this.vec3d2;
    }

    public void setMovement(Vec3 vec3d) {
        this.vec3d2 = new Vec3(vec3d.x, vec3d.y, vec3d.z);
    }

    public void setHorizontal(double d, double d2) {
        setMovement(new Vec3(d, getY(), d2));
    }

    public double getX() {
        return this.vec3d2.x;
    }

    public double getY() {
        return this.vec3d2.y;
    }

    public void setY(double d) {
        setMovement(new Vec3(getX(), d, getZ()));
    }

    public double getZ() {
        return this.vec3d2.z;
    }

    public MoverType getMovementType() {
        return this.movementType;
    }

    public void setMovementType(MoverType movementType) {
        this.movementType = movementType;
    }

    public Vec3 getOriginalMovement() {
        return this.vec3d;
    }
}
