package me.mioclient.mode.render;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/NameTagsHelperMode.class */
public enum NameTagsHelperMode implements NamedValue {
    FRIEND("friend", "friends"),
    ENEMY("enemy", "enemies");

    public final String name;
    public final String string;

    NameTagsHelperMode(String str, String str2) {
        this.name = str;
        this.string = str2;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public String getPluralName() {
        return this.string;
    }
}
