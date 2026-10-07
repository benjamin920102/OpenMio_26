package me.mioclient.api;

import net.minecraft.network.chat.Component;

public interface NamedValue {
    String getName();

    default NamedValue ofName(String str) {
        return () -> {
            return str;
        };
    }

    default Component getAsText() {
        return Component.literal(getName());
    }

    static boolean isNamedValue(Object obj) {
        return obj instanceof NamedValue;
    }
}
