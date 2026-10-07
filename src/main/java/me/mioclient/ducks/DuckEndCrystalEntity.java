package me.mioclient.ducks;

public interface DuckEndCrystalEntity {
    long getSpawnTime();

    boolean isMioAttacked();

    void setMioAttacked(boolean z);
}
