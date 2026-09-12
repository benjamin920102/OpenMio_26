package me.mioclient.mode.player;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpeedMineMode_2.class */
public enum SpeedMineMode_2 implements NamedValue {
    NONE("None"),
    FAST("Fast"),
    INSTANT("Instant");

    public final String name;

    SpeedMineMode_2(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
