package me.mioclient.api;

import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.util.client.InputState;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

public final class Keybind {
    public final int keyCode;
    public final KeybindMode keybindMode;
    public final boolean isMouse;
    public static final Keybind keybind = new Keybind(-1, KeybindMode.TOGGLE, false);

    
    public enum KeybindMode implements NamedValue {
        TOGGLE("Toggle", "toggle"),
        HOLD("Hold", "hold"),
        HOLD_REVERSE("ReverseHold", "hold_reverse");

        public final String name;
        public final String serializedName;

        KeybindMode(String str, String str2) {
            this.name = str;
            this.serializedName = str2;
        }

        public String getSerialized() {
            return this.serializedName;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public static KeybindMode fromStringMode(String str) {
            String lowerCase = str.trim().toLowerCase();
            int z = -1;
            switch (lowerCase.hashCode()) {
                case 3208383:
                    if (lowerCase.equals("hold")) {
                        z = 0;
                        break;
                    }
                    break;
                case 1999224674:
                    if (lowerCase.equals("hold_reverse")) {
                        z = 1;
                        break;
                    }
                    break;
            }
            switch (z) {
                case 0:
                    return HOLD;
                case 1:
                    return HOLD_REVERSE;
                default:
                    return TOGGLE;
            }
        }
    }

    public Keybind(int i, KeybindMode keybindMode, boolean z) {
        this.keyCode = i;
        this.keybindMode = keybindMode;
        this.isMouse = z;
    }

    public Keybind withKey(int i) {
        return new Keybind(i, this.keybindMode, this.isMouse);
    }

    public Keybind withMode(KeybindMode keybindMode) {
        return new Keybind(this.keyCode, keybindMode, this.isMouse);
    }

    public Keybind withMouse(boolean z) {
        return new Keybind(this.keyCode, this.keybindMode, z);
    }

    public boolean isUnbound() {
        return this.keyCode < 0;
    }

    public String getBindString() {
        return InputState.getString2602(this);
    }

    public int getKey() {
        return this.keyCode;
    }

    public KeybindMode getMode() {
        return this.keybindMode;
    }

    public boolean isMouseKeybind() {
        return this.isMouse;
    }
}
