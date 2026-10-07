package me.mioclient.module.misc;

import me.mioclient.api.NamedValue;

import java.util.concurrent.ThreadLocalRandom;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.network.chat.Component;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.StringSetting;

public class CustomDeathText extends Module {
    public Setting<CustomDeathTextMode> mode = add(new EnumSetting<>("Mode", CustomDeathTextMode.CUSTOM));
    public Setting<String> message = add(new StringSetting("Message", "Uh oh.", v -> this.mode.getValue() == CustomDeathText.CustomDeathTextMode.CUSTOM));
    public static final String[] stringArr = {"YOU USELESS SHITSTAIN", "GAME OVER MOTHERFUCKER! YOU SUCK!", "HOW THE HELL ARE YOU SO BAD AT THIS GAME?", "HOW DO YOU FUCK UP SO BADLY JESUS FUCKING CHRIST?", "YOU DIED! CONGRATS FUCKFACE", "OH MY GOD YOU SUCK! AUTO-UNINSTALLING", "HOLY FUCKING SHIT SERIOUSLY", "YOU FUCKING RETARD!", "YOU DENSE FUCK"};

    
    public enum CustomDeathTextMode implements NamedValue {
        CUSTOM("Custom"),
        RANDOM("Random");

        public final String name;

        CustomDeathTextMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public CustomDeathText() {
        super("CustomDeathText", "Displays a custom message on your death screen.", Category.MISC, new String[0]);
        setDrawn(false);
    }

    public Component getText327() {
        if (this.mode.getValue() == CustomDeathTextMode.CUSTOM) {
            return Component.literal(this.message.getValue().trim());
        }
        return Component.literal(stringArr[ThreadLocalRandom.current().nextInt(stringArr.length)]);
    }
}