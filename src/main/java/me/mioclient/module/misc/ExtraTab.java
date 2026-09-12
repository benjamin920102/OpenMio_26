package me.mioclient.module.misc;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.render.NameTagsHelperMode;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import java.awt.Color;
import java.util.Comparator;
import me.mioclient.mode.render.NameTagsHelperMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/ExtraTab.class */
public class ExtraTab extends Module {
    public Setting<MixinPlayerListHudMode> latency = add(new EnumSetting<>("Latency", MixinPlayerListHudMode.VANILLA));
    public Setting<Float> scale = add(new NumberSetting<>("Scale", 1.0f, 0.5f, 1.5f));
    public Setting<Boolean> betterSkin = add(new BooleanSetting("BetterSkin", true));
    public Setting<Boolean> highlight = add(new BooleanSetting("Highlight", true).setVisible());
    public Setting<Color> self = add(new ColorSetting("Self", new Color(-65281, true), v -> this.highlight.isShown()).setThemeSync(true));
    public Setting<Boolean> social = add(new BooleanSetting("Social", true, v -> this.highlight.isShown()));
    public Setting<Boolean> sort = add(new BooleanSetting("Sort", false).setVisible());
    public Setting<ExtraTabMode> mode = add(new EnumSetting<>("Mode", ExtraTabMode.ALPHABET).requiresSettings(this.sort));
    public Setting<Boolean> reverse = add(new BooleanSetting("Reverse", false).requiresSettings(this.sort));

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/misc/ExtraTab$ExtraTabMode.class */
    public enum ExtraTabMode implements NamedValue {
        ALPHABET("Alphabet"),
        SOCIALS("Socials"),
        LATENCY("Latency"),
        LENGTH("Length");

        public final String name;

        ExtraTabMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/misc/ExtraTab$MixinPlayerListHudMode.class */
    public enum MixinPlayerListHudMode implements NamedValue {
        VANILLA("Vanilla"),
        TEXT("Text"),
        NONE("None");

        public final String name;

        MixinPlayerListHudMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public ExtraTab() {
        super("ExtraTab", "Allows you to customize your TAB-screen.", Category.MISC, new String[0]);
        setDrawn(false);
    }

    public float getTabScale() {
        return !isToggled() ? Float.intBitsToFloat(1065353216) : this.scale.getValue().floatValue();
    }

    public int getSortWeight(PlayerListEntry playerListEntry) {
        switch (this.mode.getValue()) {
            case ALPHABET:
                return 0;
            case SOCIALS:
                NameTagsHelperMode nameTagsHelperMode525 = ClientServices.socialManager.getRole(playerListEntry.getProfile().getName());
                int i = 0;
                if (nameTagsHelperMode525 == NameTagsHelperMode.FRIEND) {
                    i = -1;
                }
                if (nameTagsHelperMode525 == NameTagsHelperMode.ENEMY) {
                    i = -2;
                }
                return i;
            case LATENCY:
                return -playerListEntry.getLatency();
            case LENGTH:
                return -minecraftClient.textRenderer.getWidth(playerListEntry.getProfile().getName());
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public Comparator<PlayerListEntry> getComparator() {
        Comparator<PlayerListEntry> thenComparing = Comparator.comparingInt((PlayerListEntry playerListEntry) -> {
            int i = playerListEntry.getGameMode() == GameMode.SPECTATOR ? 1 : 0;
            if (this.reverse.getValue().booleanValue()) {
                i *= -1;
            }
            return i;
        }).thenComparing(playerListEntry2 -> {
            return (String) Nullables.mapOrElse(playerListEntry2.getScoreboardTeam(), (v0) -> {
                return v0.getName();
            }, "");
        }).thenComparing(playerListEntry3 -> {
            return Integer.valueOf(getSortWeight(playerListEntry3));
        }).thenComparing(playerListEntry4 -> {
            return playerListEntry4.getProfile().getName();
        }, (v0, v1) -> {
            return v0.compareToIgnoreCase(v1);
        });
        if (this.reverse.getValue().booleanValue()) {
            thenComparing = thenComparing.reversed();
        }
        return thenComparing;
    }
}
