package me.mioclient.mode.world;

import me.mioclient.api.NamedValue;

public enum StashFinderMode implements NamedValue {
    OVERWORLD("overworld", "Overworld"),
    THE_NETHER("the_nether", "Nether"),
    THE_END("the_end", "End");

    public final String name;
    public final String string;

    StashFinderMode(String str, String str2) {
        this.name = str;
        this.string = str2;
    }

    public boolean isOverworld() {
        return this == OVERWORLD;
    }

    public boolean isNether() {
        return this == THE_NETHER;
    }

    public boolean isTheEnd() {
        return this == THE_END;
    }

    public String getDisplayName() {
        return this.string;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
