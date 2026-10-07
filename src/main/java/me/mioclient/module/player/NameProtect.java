package me.mioclient.module.player;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.StringSetting;

public class NameProtect extends Module {
    public Setting<String> name = add(new StringSetting("Name", "Me"));
    public Setting<Boolean> skin = add(new BooleanSetting("Skin", true));
    public Setting<Boolean> slim = add(new BooleanSetting("Slim", false).requiresSettings(this.skin));

    public NameProtect() {
        super("NameProtect", "Hides your nickname from the curious people.", Category.PLAYER, new String[0]);
    }
}