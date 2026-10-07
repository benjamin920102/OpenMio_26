package me.mioclient.module.misc;

import me.mioclient.api.NamedValue;

import me.mioclient.util.time.Stopwatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.PlayerModelPart;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class SkinFlicker extends Module {
    public static final PlayerModelPart[] playerModelPartArr = {PlayerModelPart.LEFT_SLEEVE, PlayerModelPart.JACKET, PlayerModelPart.HAT, PlayerModelPart.LEFT_PANTS_LEG, PlayerModelPart.RIGHT_PANTS_LEG, PlayerModelPart.RIGHT_SLEEVE};
    public static final PlayerModelPart[] playerModelPartArr2 = {PlayerModelPart.HAT, PlayerModelPart.JACKET, PlayerModelPart.LEFT_SLEEVE, PlayerModelPart.RIGHT_SLEEVE, PlayerModelPart.LEFT_PANTS_LEG, PlayerModelPart.RIGHT_PANTS_LEG};
    public Setting<SkinFlickerMode> mode = add(new EnumSetting<>("Mode", SkinFlickerMode.FULL));
    public Setting<Boolean> cape = add(new BooleanSetting("Cape", false));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 1.0f, 0.0f, 10.0f).setSuffix("s"));
    public final Map<PlayerModelPart, Boolean> map;
    public final int num;
    public final Stopwatch stopwatch;
    public int num2;

    
    public enum SkinFlickerMode implements NamedValue {
        FULL("Full"),
        VERTICAL("Vertical"),
        HORIZONTAL("Horizontal"),
        RANDOM("Random");

        public final String name;

        SkinFlickerMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public SkinFlicker() {
        super("SkinFlicker", "Flicks your skin parts.", Category.MISC, "skinblinker");
        this.map = new HashMap();
        this.num = PlayerModelPart.values().length;
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            this.map.put(playerModelPart, Boolean.valueOf(minecraftClient.options.isModelPartEnabled(playerModelPart)));
        }
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        Map<PlayerModelPart, Boolean> map = this.map;
        Options gameOptions = minecraftClient.options;
        Objects.requireNonNull(gameOptions);
        map.forEach((v1, v2) -> {
            gameOptions.setModelPart(v1, v2);
        });
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS)) {
            switch (this.mode.getValue()) {
                case FULL:
                    for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                        if (shouldFlickPart(playerModelPart)) {
                            toggleSkinPart(playerModelPart);
                        }
                    }
                    break;
                case VERTICAL:
                case HORIZONTAL:
                    PlayerModelPart playerModelPart2 = (this.mode.getValue() == SkinFlickerMode.HORIZONTAL ? playerModelPartArr : playerModelPartArr2)[this.num2 % 6];
                    if (shouldFlickPart(playerModelPart2)) {
                        toggleSkinPart(playerModelPart2);
                        this.num2 = (this.num2 + 1) % 6;
                        break;
                    }
                    break;
                case RANDOM:
                    PlayerModelPart playerModelPart3 = PlayerModelPart.values()[ThreadLocalRandom.current().nextInt(this.num)];
                    if (shouldFlickPart(playerModelPart3)) {
                        toggleSkinPart(playerModelPart3);
                        break;
                    }
                    break;
            }
            this.stopwatch.reset();
        }
    }

    public void toggleSkinPart(PlayerModelPart playerModelPart) {
        minecraftClient.options.setModelPart(playerModelPart, !minecraftClient.options.isModelPartEnabled(playerModelPart));
    }

    public boolean shouldFlickPart(PlayerModelPart playerModelPart) {
        return this.cape.getValue().booleanValue() || playerModelPart != PlayerModelPart.CAPE;
    }
}