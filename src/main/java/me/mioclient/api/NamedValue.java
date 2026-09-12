package me.mioclient.api;

import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/NamedValue.class */
public interface NamedValue {
    String getName();

    default NamedValue ofName(String str) {
        return () -> {
            return str;
        };
    }

    default Text getAsText() {
        return Text.literal(getName());
    }

    static boolean isNamedValue(Object obj) {
        return obj instanceof NamedValue;
    }
}
