package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoCrystalMode_4.class */
public enum AutoCrystalMode_4 implements NamedValue {
    NORMAL("Normal"),
    ALTERNATIVE("Alternative"),
    PICK("Pick");

    public final String name;

    AutoCrystalMode_4(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
