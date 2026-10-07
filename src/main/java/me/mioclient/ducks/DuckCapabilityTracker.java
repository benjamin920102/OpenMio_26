package me.mioclient.ducks;

public interface DuckCapabilityTracker {
    boolean getState();

    void set(boolean z);
}
