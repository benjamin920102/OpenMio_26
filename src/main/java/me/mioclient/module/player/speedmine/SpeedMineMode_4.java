package me.mioclient.module.player.speedmine;

import me.mioclient.api.NamedValue;

public enum SpeedMineMode_4 implements NamedValue {
    OFF("Off"),
    TICK("Tick"),
    SLOW("Slow");

    public final String name;

    SpeedMineMode_4(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}