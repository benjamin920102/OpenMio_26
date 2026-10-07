package me.mioclient.util.baritone;

import net.minecraft.core.BlockPos;

public final class NoopPathingController implements PathingController {
    @Override // me.mioclient.util.baritone.PathingController
    public void addPauseRequest(me.mioclient.module.Feature feature) {
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void removePauseRequest(me.mioclient.module.Feature feature) {
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isPaused() {
        return false;
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isPathing() {
        return false;
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void pathForward(double d) {
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void pathTo(BlockPos blockPos) {
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void elytraPathTo(BlockPos blockPos) {
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void cancelAll() {
    }

    @Override // me.mioclient.util.baritone.PathingController
    public void setGoal(BlockPos blockPos) {
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isInSelection(BlockPos blockPos) {
        return false;
    }

    @Override // me.mioclient.util.baritone.PathingController
    public boolean isBaritoneLoaded() {
        return false;
    }

    @Override // me.mioclient.util.baritone.PathingController
    public BlockPos getCurrentGoal() {
        return null;
    }
}
