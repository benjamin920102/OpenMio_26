package me.mioclient.mode.misc;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import java.util.function.BiFunction;
import me.mioclient.api.Keybind;
import me.mioclient.command.KeybindFeature;
import me.mioclient.command.KeybindFeature_2;
import me.mioclient.command.KeybindFeature_3;
import me.mioclient.command.KeybindFeature_4;
import me.mioclient.command.StopwatchKeybindFeature;
import me.mioclient.command.KeybindFeature;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Mode_4.class */
public enum KeybindMode implements NamedValue {
    SIMPLE("simple", KeybindFeature_2::new),
    QUEUE("queue", KeybindFeature_4::new),
    DOUBLE_TAP("double_tap", StopwatchKeybindFeature::new),
    HOLD("hold", KeybindFeature_3::new);

    public final String name;
    public final BiFunction<String, Keybind, KeybindFeature> biFunction;

    KeybindMode(String str, BiFunction<String, Keybind, KeybindFeature> biFunction) {
        this.name = str;
        this.biFunction = biFunction;
    }

    public static KeybindMode getByName(String str) {
        for (KeybindMode keybindMode : values()) {
            if (keybindMode.getName().equalsIgnoreCase(str)) {
                return keybindMode;
            }
        }
        return null;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public KeybindFeature createKeybindFeature(String str, Keybind keybind) {
        return this.biFunction.apply(str, keybind);
    }
}
