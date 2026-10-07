package me.mioclient.mode.combat;

import me.mioclient.api.NamedValue;

public enum CrystalRotationMode implements NamedValue {
    MOTION("Motion"),
    SILENT("Silent");

    public final String name;

    CrystalRotationMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
