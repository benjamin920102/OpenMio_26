package me.mioclient.ducks;

import net.minecraft.world.phys.Vec3;

public interface DuckEntityCamera {
    float getCameraYaw();

    float getCameraPitch();

    void setCameraYaw(float f);

    void setCameraPitch(float f);

    Vec3 mio$getPrevVelocity();
}
