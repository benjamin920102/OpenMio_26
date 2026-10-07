package me.mioclient.module.player.cheststealer;

import me.mioclient.api.NamedValue;

public enum ChestStealerMode implements NamedValue {
    STEAL("Steal"),
    FILL("Fill"),
    DROP("Drop"),
    REFILL("Refill"),
    REKIT("Rekit");

    public final String name;

    ChestStealerMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}