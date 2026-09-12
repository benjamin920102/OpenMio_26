package me.mioclient.module;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.api.NamedValue;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.StringSetting;
import me.mioclient.api.Setting;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Welcomer.class */
public class Welcomer extends me.mioclient.module.HudModule {
    public Setting<WelcomerMode> setting;
    public Setting<String> setting2;

    static enum WelcomerMode implements NamedValue {
        welcomerMode("Name") {
            @Override public Text getWelcomeText(Welcomer welcomer) {
                return Text.literal("Hello " + ClientAccessor.minecraftClient.player.getName().getString() + " :')");
            }
        },
        welcomerMode2("UID") {
            @Override public Text getWelcomeText(Welcomer welcomer) {
                return Text.literal("Hello uid" + ClientServices.welcomerHelper.getRank() + " :')");
            }
        },
        welcomerMode3("Custom") {
            @Override public Text getWelcomeText(Welcomer welcomer) {
                return Text.literal(welcomer.setting2.getValue());
            }
        };

        public final String name;
        WelcomerMode(String str2) { this.name = str2; }
        @Override public String getName() { return this.name; }
        public Text getWelcomeText(Welcomer welcomer) { return null; }
    }

    public Welcomer() {
        super("Welcomer", new String[0]);
        this.setting = add(new EnumSetting("Mode", WelcomerMode.welcomerMode));
        this.setting2 = add(new StringSetting("Text", "Welcome to Mio.", str -> {
            return this.setting.getValue() == WelcomerMode.welcomerMode3;
        }));
        setElement(new TextHudElement(this, new HudTextLine(() -> {
            return this.setting.getValue().getWelcomeText(this);
        }, () -> {
            return true;
        })));
    }
}
