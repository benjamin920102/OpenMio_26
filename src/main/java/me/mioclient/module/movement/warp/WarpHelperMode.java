package me.mioclient.module.movement.warp;

import me.mioclient.api.NamedValue;

public enum WarpHelperMode implements NamedValue {
    HOLD("Hold"),
    INSTANT("Instant"),
    NONE("None");

    public final String name;

    WarpHelperMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}