package me.mioclient.mode.combat;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import net.minecraft.util.math.Vec3i;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BlockerOffsetMode.class */
public enum BlockerOffsetMode implements NamedValue {
    FULL("Full", new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, -1), new Vec3i(1, 1, 0), new Vec3i(0, 1, 1), new Vec3i(-1, 1, 0), new Vec3i(0, 1, -1), new Vec3i(0, 2, 0), new Vec3i(0, 3, 0)),
    SURROUND("Surround", new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, -1)),
    CEV("Cev", new Vec3i(0, 2, 0), new Vec3i(0, 3, 0));

    public final String name;
    public final Vec3i[] vec3iArr;

    BlockerOffsetMode(String str, Vec3i... vec3iArr) {
        this.name = str;
        this.vec3iArr = vec3iArr;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public Vec3i[] getOffsets() {
        return this.vec3iArr;
    }
}
