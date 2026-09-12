package me.mioclient.module.render;

import me.mioclient.api.NamedValue;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import java.awt.Color;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/SkyColor.class */
public class SkyColor extends Module {
    public Setting<MixinClientWorldMode> type = add(new EnumSetting<>("Type", MixinClientWorldMode.NONE));
    public Setting<Boolean> dense = add(new BooleanSetting("Dense", false));
    public Setting<Color> fog = add(new ColorSetting("Fog", new Color(-4351489, true)).setThemeSync(true));
    public Setting<Color> sky = add(new ColorSetting("Sky", new Color(-4351489, true)));
    public Setting<Boolean> dimensions = add(new BooleanSetting("Dimensions", true).setDependent());
    public Setting<Boolean> overworld = add(new BooleanSetting("Overworld", true).requiresSettings(this.dimensions));
    public Setting<Boolean> nether = add(new BooleanSetting("Nether", true).requiresSettings(this.dimensions));
    public Setting<Boolean> end = add(new BooleanSetting("End", true).requiresSettings(this.dimensions));

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/SkyColor$MixinClientWorldMode.class */
    public enum MixinClientWorldMode implements NamedValue {
        NONE("None"),
        FLAT("Flat"),
        END("End");

        public final String name;

        MixinClientWorldMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public SkyColor() {
        super("SkyColor", "Changes the fog color.", Category.RENDER, new String[0]);
        setDrawn(false);
        this.fog.addVisibilityPredicate(color -> {
            return this.type.getValue() != MixinClientWorldMode.END;
        });
        this.sky.addVisibilityPredicate(color2 -> {
            return this.type.getValue() != MixinClientWorldMode.END;
        });
        this.dense.addVisibilityPredicate(bool -> {
            return this.type.getValue() == MixinClientWorldMode.FLAT || ((this.nether.getValue().booleanValue() || this.end.getValue().booleanValue()) && this.type.getValue() == MixinClientWorldMode.NONE);
        });
    }

    public boolean isDimensionEnabled() {
        switch (WorldUtils.getDimension()) {
            case OVERWORLD:
                return this.overworld.getValue().booleanValue();
            case THE_NETHER:
                return this.nether.getValue().booleanValue();
            default:
                return this.end.getValue().booleanValue();
        }
    }
}
