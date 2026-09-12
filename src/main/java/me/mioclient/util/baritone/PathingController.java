package me.mioclient.util.baritone;

import net.minecraft.util.math.BlockPos;
import me.mioclient.module.Feature;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PathingController.class */
public interface PathingController {
    void addPauseRequest(me.mioclient.module.Feature feature);

    void removePauseRequest(me.mioclient.module.Feature feature);

    boolean isPaused();

    boolean isPathing();

    void pathForward(double d);

    void pathTo(BlockPos blockPos);

    void elytraPathTo(BlockPos blockPos);

    void cancelAll();

    void setGoal(BlockPos blockPos);

    boolean isInSelection(BlockPos blockPos);

    boolean isBaritoneLoaded();

    BlockPos getCurrentGoal();
}
