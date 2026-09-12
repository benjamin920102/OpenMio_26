package me.mioclient.module.movement;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/FastLadder.class */
public class FastLadder extends Module {
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 0.2f, 0.0f, 1.0f));

    public FastLadder() {
        super("FastLadder", "Makes you move faster on ladders.", Category.MOVEMENT, new String[0]);
    }
}
