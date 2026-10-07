package me.mioclient.module.player.cheststealer;

import me.mioclient.api.NamedValue;

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