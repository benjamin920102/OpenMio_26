package me.mioclient.module.player.speedmine;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpeedMineMode_4.class */
public enum SpeedMineMode_4 implements NamedValue {
    OFF("Off"),
    TICK("Tick"),
    SLOW("Slow");

    public final String name;

    SpeedMineMode_4(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
