package me.mioclient.mode.client;

import me.mioclient.api.NamedValue;

public enum FontStyle implements NamedValue {
    PLAIN("Plain"),
    BOLD("Bold"),
    ITALIC("Italic"),
    BOLDITALIC("BoldItalic");

    public final String name;

    FontStyle(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
