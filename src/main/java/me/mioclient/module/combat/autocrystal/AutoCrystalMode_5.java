package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.NamedValue;

public enum AutoCrystalMode_5 implements NamedValue {
    NONE("None"),
    STRICT("Strict"),
    STRONG("Strong");

    public final String name;

    AutoCrystalMode_5(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public boolean isActiveCrystalMode() {
        return this != NONE;
    }
}