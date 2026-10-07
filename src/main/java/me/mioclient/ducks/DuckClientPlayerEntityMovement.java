package me.mioclient.ducks;

public interface DuckClientPlayerEntityMovement {
    void sendMovementPacketsWrapper();

    void superTick();

    void resetEvent();

    void resetRotations();
}
