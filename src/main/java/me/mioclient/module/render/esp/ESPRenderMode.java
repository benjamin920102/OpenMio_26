package me.mioclient.module.render.esp;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ESPRenderMode.class */
public enum ESPRenderMode implements NamedValue {
    BOX("Box"),
    TEXT("Text"),
    BOTH("Both");

    public final String name;

    ESPRenderMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
