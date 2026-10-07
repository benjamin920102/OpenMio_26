package me.mioclient.event.events.player;

import me.mioclient.event.Event;

import net.minecraft.world.phys.Vec3;

public class UpdateSetPosEvent extends Event {
    public double val;
    public double val2;
    public double val3;

    public UpdateSetPosEvent(double d, double d2, double d3) {
        this.val = d;
        this.val2 = d2;
        this.val3 = d3;
    }

    public void setPos(Vec3 vec3d) {
        this.val = vec3d.x;
        this.val2 = vec3d.y;
        this.val3 = vec3d.z;
    }

    public Vec3 toVec3d() {
        return new Vec3(this.val, this.val2, this.val3);
    }
}
