package me.mioclient.module.combat.automine;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoMineLogic.class */
public enum AutoMineLogic implements NamedValue {
    PLAIN("Plain"),
    GRIM("Grim"),
    GRIMV3("NoVelocity");

    public final String name;

    AutoMineLogic(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
