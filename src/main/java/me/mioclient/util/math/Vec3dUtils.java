package me.mioclient.util.math;

import net.minecraft.world.phys.Vec3;

/* 26.2 port: Vec3 is immutable - vector-mutating helpers removed. Use setDeltaMovement/setPos/new Vec3 at call sites. */
public class Vec3dUtils {

    public Vec3dUtils() {
        throw new AssertionError();
    }

    public static float getHorizontalDistance(Vec3 vec3d, Vec3 vec3d2) {
        return get2584(vec3d.x, vec3d.z, vec3d2.x, vec3d2.z);
    }

    public static float get2584(double d, double d2, double d3, double d4) {
        return (float) Math.hypot(d - d3, d2 - d4);
    }
}
