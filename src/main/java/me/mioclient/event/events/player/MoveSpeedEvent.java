package me.mioclient.event.events.player;

import me.mioclient.mode.combat.KeyPearlMode;

public class MoveSpeedEvent {
    public final KeyPearlMode keyPearlMode;
    public float val;

    public MoveSpeedEvent(KeyPearlMode keyPearlMode, float f) {
        this.keyPearlMode = keyPearlMode;
        this.val = f;
    }

    public KeyPearlMode getPhase() {
        return this.keyPearlMode;
    }

    public float getSpeed() {
        return this.val;
    }

    public void setSpeed(float f) {
        this.val = f;
    }
}
