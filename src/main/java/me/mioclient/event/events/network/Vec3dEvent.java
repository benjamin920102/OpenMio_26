package me.mioclient.event.events.network;

import me.mioclient.event.Event;

import net.minecraft.world.phys.Vec3;

/* 26.2 port: Yarn Vec3 -> MojMap Vec3. */
public final class Vec3dEvent extends Event {
    public Vec3 vec3d;

    public Vec3dEvent(Vec3 vec3d) {
        this.vec3d = vec3d;
    }

    public Vec3 getPosition() {
        return this.vec3d;
    }
}
