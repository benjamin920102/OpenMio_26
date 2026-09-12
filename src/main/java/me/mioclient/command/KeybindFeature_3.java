package me.mioclient.command;

import me.mioclient.mode.misc.KeybindMode;

import me.mioclient.api.Keybind;
import me.mioclient.mode.misc.KeybindMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/KeybindFeature_3.class */
public class KeybindFeature_3 extends KeybindFeature {
    public boolean flag;

    public KeybindFeature_3(String str, Keybind keybind) {
        super(str, KeybindMode.HOLD, keybind);
    }

    @Override // me.mioclient.command.KeybindFeature
    public void run() {
    }

    public void do1072(boolean z) {
        if (this.flag == z) {
            return;
        }
        this.flag = z;
        if (this.list.isEmpty()) {
            return;
        }
        if (this.flag) {
            dispatch((String) this.list.getFirst());
        } else {
            dispatch((String) this.list.getLast());
        }
    }
}
