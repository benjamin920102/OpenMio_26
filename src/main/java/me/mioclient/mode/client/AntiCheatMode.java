package me.mioclient.mode.client;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Mode_6.class */
public enum AntiCheatMode implements NamedValue {
    NCP("NCP"),
    GRIM("Grim");

    public final String name;

    AntiCheatMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
