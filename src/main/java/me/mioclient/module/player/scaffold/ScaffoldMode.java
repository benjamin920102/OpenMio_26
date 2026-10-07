package me.mioclient.module.player.scaffold;

import me.mioclient.module.player.scaffold.PlacementTarget;

import net.minecraft.world.phys.Vec3;

public enum ScaffoldMode {
    PRE,
    POST;

    public PlacementTarget scaffoldData;
    public Vec3 vec3d;

    public ScaffoldMode getScaffoldMode874(PlacementTarget scaffoldData, Vec3 vec3d) {
        this.scaffoldData = scaffoldData;
        this.vec3d = vec3d;
        return this;
    }
}