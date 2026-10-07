package me.mioclient.mode.client;

import me.mioclient.api.NamedValue;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import me.mioclient.module.client.HUD;
import me.mioclient.setting.ColorSetting;

public enum MixinTitleScreenMode implements NamedValue {
    NONE("None") { // from class: me.mioclient.mode.client.MixinTitleScreenMode.Inner_3
        @Override // me.mioclient.mode.client.MixinTitleScreenMode
        public Color computeColor(HUD hud, float f) {
            if (!((ColorSetting) hud.setting7).isRainbow()) {
                return hud.setting7.getValue();
            }
            return ((ColorSetting) hud.setting7).computeAnimatedColor(hud.setting7.getValue(), (int) (f * hud.setting9.getValue().intValue() * MixinTitleScreenMode.getFontScaleFactor()));
        }
    },
    PULSE("Pulse") { // from class: me.mioclient.mode.client.MixinTitleScreenMode.Inner_2
        @Override // me.mioclient.mode.client.MixinTitleScreenMode
        public Color computeColor(HUD hud, float f) {
            return ColorUtils.getGradientColor(hud.setting7.getValue(), hud.setting10.getValue(), 2000.0d, f * hud.setting9.getValue().intValue() * MixinTitleScreenMode.getFontScaleFactor());
        }
    },
    RAW("Raw") { // from class: me.mioclient.mode.client.MixinTitleScreenMode.Inner
        @Override // me.mioclient.mode.client.MixinTitleScreenMode
        public Color computeColor(HUD hud, float f) {
            int intValue = hud.setting11.getValue().intValue();
            float intValue2 = 1.0f / hud.setting9.getValue().intValue();
            int currentTimeMillis = (int) (((float) ((System.currentTimeMillis() / hud.setting12.getValue().intValue()) % (intValue * hud.setting9.getValue().intValue()))) + (f / MixinTitleScreenMode.getFontScaleFactor()));
            int floor = (int) Math.floor(currentTimeMillis * intValue2);
            if (floor < 0) {
                floor = 0;
            }
            if (!hud.setting13.getValue().booleanValue()) {
                return hud.list.get(floor % intValue).getValue();
            }
            return ColorUtils.lerpColor(hud.list.get(floor % intValue).getValue(), hud.list.get((floor + 1) % intValue).getValue(), 1.0f - ((currentTimeMillis * intValue2) - floor));
        }
    };

    public final String name;

    MixinTitleScreenMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public abstract Color computeColor(HUD hud, float f);

    public static float getFontScaleFactor() {
        return 10.0f / (ClientTextRenderer.INSTANCE.getFontHeight() + 2);
    }
}
