package me.mioclient.module.client;

import me.mioclient.module.KeybindModule;

import me.mioclient.mode.client.SearchMode;

import java.awt.Color;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;

public class Colors extends KeybindModule {
    public Setting<Color> friendColor = add(new ColorSetting("FriendColor", new Color(-16711681, true)));
    public Setting<Color> enemyColor = add(new ColorSetting("EnemyColor", new Color(-4980736, true)));
    public Setting<Color> themeColor = add(new ColorSetting("ThemeColor", new Color(-4351489, true)));
    public Setting<SearchMode> scheme = add(new EnumSetting<>("Scheme", SearchMode.NORMAL));

    public Colors() {
        super("Colors", "Manages the client's color system.", Category.CLIENT, new String[0]);
    }
}