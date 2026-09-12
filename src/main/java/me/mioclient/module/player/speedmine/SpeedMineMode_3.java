package me.mioclient.module.player.speedmine;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpeedMineMode_3.class */
public enum SpeedMineMode_3 implements NamedValue {
    NONE("None"),
    NORMAL("Normal"),
    SILENT("Silent");

    public final String name;

    SpeedMineMode_3(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
