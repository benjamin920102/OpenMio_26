package me.mioclient.ducks;

import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/DuckEntityCamera.class */
public interface DuckEntityCamera {
    float getCameraYaw();

    float getCameraPitch();

    void setCameraYaw(float f);

    void setCameraPitch(float f);

    Vec3d mio$getPrevVelocity();
}
