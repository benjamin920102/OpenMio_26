package me.mioclient.command;

import me.mioclient.mode.misc.KeybindMode;

import java.util.ArrayList;
import java.util.List;
import me.mioclient.api.Keybind;
import me.mioclient.module.Feature;

public abstract class KeybindFeature extends me.mioclient.module.Feature {
    public final List<String> list;
    public final KeybindMode mode_4;
    public final Keybind keybind;

    public KeybindFeature(String str, KeybindMode mode_4, Keybind keybind) {
        super(str);
        this.list = new ArrayList();
        this.mode_4 = mode_4;
        this.keybind = keybind;
    }

    public abstract void run();

    public KeybindMode getKeybindMode() {
        return this.mode_4;
    }

    public Keybind getKeybind() {
        return this.keybind;
    }

    public List<String> getCommands() {
        return this.list;
    }

    public void dispatch(String str) {
        for (String str2 : str.split(";")) {
            ClientCommandRegistry.dispatch(str2.trim());
        }
    }
}
