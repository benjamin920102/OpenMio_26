package me.mioclient.mode.combat;

import me.mioclient.api.NamedValue;

public enum ScaffoldHelperMode implements NamedValue {
    NONE("None"),
    NORMAL("Normal"),
    SILENT("Silent");

    public final String name;

    ScaffoldHelperMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
