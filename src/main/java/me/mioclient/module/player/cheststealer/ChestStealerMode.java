package me.mioclient.module.player.cheststealer;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChestStealerMode.class */
public enum ChestStealerMode implements NamedValue {
    STEAL("Steal"),
    FILL("Fill"),
    DROP("Drop"),
    REFILL("Refill"),
    REKIT("Rekit");

    public final String name;

    ChestStealerMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
