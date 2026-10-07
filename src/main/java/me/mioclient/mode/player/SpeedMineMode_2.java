package me.mioclient.mode.player;

import me.mioclient.api.NamedValue;

public enum SpeedMineMode_2 implements NamedValue {
    NONE("None"),
    FAST("Fast"),
    INSTANT("Instant");

    public final String name;

    SpeedMineMode_2(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
