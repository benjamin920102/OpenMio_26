package me.mioclient.module.player.cheststealer;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChestStealerMode_2.class */
public enum ChestStealerMode_2 implements NamedValue {
    NAME("Name"),
    ITEM("Item");

    public final String name;

    ChestStealerMode_2(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
