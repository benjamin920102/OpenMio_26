package me.mioclient.module.movement.warp;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WarpHelperMode.class */
public enum WarpHelperMode implements NamedValue {
    HOLD("Hold"),
    INSTANT("Instant"),
    NONE("None");

    public final String name;

    WarpHelperMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
