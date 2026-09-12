package me.mioclient.module.misc;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/AntiQuit.class */
public class AntiQuit extends Module {
    public Setting<Boolean> disconnect = add(new BooleanSetting("Disconnect", false));
    public Setting<Boolean> gameClose = add(new BooleanSetting("GameClose", false));

    public AntiQuit() {
        super("AntiQuit", "Prevents you from quitting the game/server accidentally.", Category.MISC, "antidisconnect");
    }
}
