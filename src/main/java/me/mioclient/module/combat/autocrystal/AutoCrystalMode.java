package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoCrystalMode.class */
public enum AutoCrystalMode implements NamedValue {
    ALWAYS("Always"),
    STRICT("Strict"),
    NONE("None");

    public final String name;

    AutoCrystalMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
