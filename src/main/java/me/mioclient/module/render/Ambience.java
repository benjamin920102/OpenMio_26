package me.mioclient.module.render;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import java.awt.Color;
import java.util.Calendar;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.biome.Biome;
import me.mioclient.ducks.DuckSimpleOption;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Ambience.class */
public class Ambience extends Module {
    public Setting<MixinEntityRendererMode> brightness = add(new EnumSetting<>("Brightness", MixinEntityRendererMode.GAMMA));
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-1, true), v -> this.brightness.getValue() == Ambience.MixinEntityRendererMode.SCREEN));
    public Setting<Integer> lightLevel = add(new NumberSetting<>("LightLevel", 1, 1, 15, v -> this.brightness.getValue() == Ambience.MixinEntityRendererMode.SKY));
    public Setting<Boolean> worldTime = add(new BooleanSetting("WorldTime", false).setVisible());
    public Setting<Boolean> sync = add(new BooleanSetting("Sync", false).requiresSettings(this.worldTime));
    public Setting<Float> time = add(new NumberSetting<>("Time", 0.0f, 0.0f, 24.0f, v -> this.worldTime.isShown() && !this.sync.getValue().booleanValue()).setSuffix("h"));
    public Setting<Boolean> worldWeather = add(new BooleanSetting("WorldWeather", false).setVisible());
    public Setting<AmbiencePredicateMode> weather = add(new EnumSetting<>("Weather", AmbiencePredicateMode.CLEAR).requiresSettings(this.worldWeather));
    public Setting<Boolean> force = add(new BooleanSetting("Force", false));
    public Setting<Float> amount = add(new NumberSetting<>("Amount", 1.0f, 0.0f, 1.0f));
    public double val;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Ambience$AmbiencePredicateMode.class */
    public enum AmbiencePredicateMode implements NamedValue {
        CLEAR("Clear", Biome.Precipitation.NONE),
        SNOW("Snow", Biome.Precipitation.SNOW),
        RAIN("Rain", Biome.Precipitation.RAIN),
        DUSTY("Dusty", Biome.Precipitation.NONE);

        public final String name;
        public final Biome.Precipitation precipitation;

        AmbiencePredicateMode(String str, Biome.Precipitation precipitation) {
            this.name = str;
            this.precipitation = precipitation;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public Biome.Precipitation getPrecipitation2196() {
            return this.precipitation;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Ambience$MixinEntityRendererMode.class */
    public enum MixinEntityRendererMode implements NamedValue {
        SCREEN("Screen"),
        GAMMA("Gamma"),
        SKY("Sky"),
        POTION("Potion"),
        NONE("None");

        public final String name;

        MixinEntityRendererMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Ambience() {
        super("Ambience", "Changes several ambient things.", Category.RENDER, new String[0]);
        this.lightLevel.setChangeCallback(() -> {
            if (this.brightness.getValue() != MixinEntityRendererMode.SKY || minecraftClient.world == null) {
                return;
            }
            minecraftClient.worldRenderer.reload();
        });
        this.brightness.setChangeCallback(() -> {
            if (isNotInGame()) {
                return;
            }
            if (minecraftClient.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                if (minecraftClient.player.getStatusEffect(StatusEffects.NIGHT_VISION).getAmplifier() == 68) {
                    minecraftClient.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                }
            }
            if (this.brightness.getValue() != MixinEntityRendererMode.GAMMA) {
                ((me.mioclient.ducks.DuckSimpleOption) (Object) minecraftClient.options.getGamma()).forceSetValue(Double.valueOf(this.val));
            }
        });
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.val = ((Double) minecraftClient.options.getGamma().getValue()).doubleValue();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (!isNotInGame()) {
            minecraftClient.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
        ((me.mioclient.ducks.DuckSimpleOption) (Object) minecraftClient.options.getGamma()).forceSetValue(Double.valueOf(this.val));
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        if (minecraftClient.worldRenderer == null) {
            return;
        }
        if (this.brightness.getValue() == MixinEntityRendererMode.SKY || this.brightness.getValue() == MixinEntityRendererMode.SCREEN) {
            minecraftClient.worldRenderer.reload();
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (isNotInGame()) {
            return;
        }
        if (this.worldTime.getValue().booleanValue()) {
            long j = getWorldTime();
            minecraftClient.world.setTime(j);
            minecraftClient.world.setTimeOfDay(j);
        }
        if (this.brightness.getValue() == MixinEntityRendererMode.GAMMA) {
            ((me.mioclient.ducks.DuckSimpleOption) (Object) minecraftClient.options.getGamma()).forceSetValue(Double.valueOf(Double.longBitsToDouble(4652007308841189376L)));
        } else if (this.brightness.getValue() == MixinEntityRendererMode.POTION) {
            if (minecraftClient.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                return;
            }
            minecraftClient.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 68));
        }
    }

    public long getWorldTime() {
        if (!this.sync.getValue().booleanValue()) {
            return (long) ((this.time.getValue().floatValue() * Float.intBitsToFloat(1148846080)) + Float.intBitsToFloat(1183621120));
        }
        Calendar calendar = Calendar.getInstance();
        return (long) (((calendar.get(11) + (calendar.get(12) / Float.intBitsToFloat(1114636288))) * Float.intBitsToFloat(1148846080)) + Float.intBitsToFloat(1183621120));
    }

    public boolean isWeatherForced() {
        return isToggled() && this.worldWeather.getValue().booleanValue() && this.force.getValue().booleanValue();
    }
}
