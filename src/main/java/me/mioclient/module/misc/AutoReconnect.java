package me.mioclient.module.misc;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import me.mioclient.setting.NumberSetting;

public class AutoReconnect extends Module {
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 5.0f, 1.0f, 60.0f).setSuffix("s"));

    public AutoReconnect() {
        super("AutoReconnect", "Reconnects to the server automatically if you get kicked.", Category.MISC, new String[0]);
        setDrawn(false);
    }
}