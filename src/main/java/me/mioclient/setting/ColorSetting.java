package me.mioclient.setting;

import me.mioclient.api.ClientServices;
import me.mioclient.api.ValueProvider;

import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import me.mioclient.api.Setting;
import me.mioclient.module.client.Colors;
import me.mioclient.api.ValueProvider;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ColorSetting.class */
public final class ColorSetting extends Setting<Color> {
    public static Colors colors = (Colors) ClientServices.moduleInjector.getModule(Colors.class);
    public final ColorJsonConverter converter;
    public boolean flag;
    public boolean flag2;

    public ColorSetting(String str, Color color, java.util.function.Predicate<Color> predicate) {
        super(str, color, predicate);
        this.converter = new ColorJsonConverter();
    }

    public ColorSetting(String str, Color color) {
        super(str, color);
        this.converter = new ColorJsonConverter();
    }

    @Override // me.mioclient.api.Setting, me.mioclient.api.ValueProvider
    /* renamed from: getColor2856, reason: merged with bridge method [inline-methods] */
    public Color getValue() {
        Color color = (Color) super.getValue();
        return this.flag2 ? ColorUtils.withAlpha(colors.themeColor.getValue(), color.getAlpha()) : this.flag ? computeAnimatedColor(color, 0) : color;
    }

    public Color computeAnimatedColor(Color color, int i) {
        float[] RGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null);
        return ColorUtils.getAnimatedHSBColor(MathConstants.num3, i, RGBtoHSB[1], RGBtoHSB[2], color.getAlpha());
    }

    @Override // me.mioclient.api.Setting
    public void loadFromString(String str) {
        try {
            setValue(ColorUtils.colorFromHex(str));
        } catch (Throwable th) {
        }
    }

    @Override // me.mioclient.api.Setting
    /* renamed from: do2858, reason: merged with bridge method [inline-methods] */
    public void setValue(Color color) {
        if (this.flag3) {
            super.setValue(new Color(color.hashCode(), false));
        } else {
            super.setValue(color);
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("rgba", this.converter.doForward(getValue()));
        jsonObject.addProperty("rainbow", Boolean.valueOf(isRainbow()));
        jsonObject.addProperty("sync", Boolean.valueOf(isSyncToTheme()));
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            setValue(this.converter.doBackward(jsonElement));
            return;
        }
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        setValue(this.converter.doBackward(asJsonObject.get("rgba")));
        setRainbowEnabled(asJsonObject.get("rainbow").getAsBoolean());
        setSyncEnabled(asJsonObject.get("sync").getAsBoolean());
    }

    public boolean isSyncToTheme() {
        if (colors == null || this != colors.themeColor) {
            return this.flag2;
        }
        return false;
    }

    public void setSyncEnabled(boolean z) {
        if (colors == null || this != colors.themeColor) {
            this.flag2 = z;
        }
    }

    public boolean isThemeColor() {
        return colors != null && this == colors.themeColor;
    }

    /** Chainable form of {@link #setSyncEnabled(boolean)} for use in field initializers. */
    public ColorSetting setThemeSync(boolean z) {
        setSyncEnabled(z);
        return this;
    }

    /** Chainable form of {@link #setRainbowEnabled(boolean)} for use in field initializers. */
    public ColorSetting setRainbow(boolean z) {
        setRainbowEnabled(z);
        return this;
    }

    public boolean isRainbow() {
        return this.flag;
    }

    public void setRainbowEnabled(boolean z) {
        this.flag = z;
    }
}
