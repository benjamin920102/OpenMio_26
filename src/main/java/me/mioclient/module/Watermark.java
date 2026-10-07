package me.mioclient.module;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.StringSetting;
import me.mioclient.api.Setting;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class Watermark extends me.mioclient.module.HudModule {
    public Setting<Boolean> setting;
    public Setting<String> setting2;

    public Watermark() {
        super("Watermark", new String[0]);
        this.setting = add(new BooleanSetting("VersionColor", true));
        this.setting2 = add(new StringSetting("Component", "Mio"));
        setElement(new TextHudElement(this, new HudTextLine(() -> {
            return Component.literal(new StringTemplate().arg(getVersionSuffix()).arg(String.valueOf(this.setting.getValue().booleanValue() ? ChatFormatting.WHITE : "")).arg(this.setting2.getValue().trim()).format("\u0001\u0001\u0001"));
        }, () -> {
            return true;
        })));
        getElement().setAnchor(ModuleListMode.TOP_LEFT);
        setToggled(true);
    }

    public String getVersionSuffix() {
        return ClientServices.welcomerHelper.getRank() == 62 ? " v2.1.7+" : " v2.1.7";
    }
}