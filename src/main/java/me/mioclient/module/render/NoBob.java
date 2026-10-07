package me.mioclient.module.render;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.setting.NumberSetting;

public class NoBob extends Module {
    public Setting<Float> multiplier = add(new NumberSetting<>("Multiplier", 0.0f, 0.0f, 1.0f));

    public NoBob() {
        super("NoBob", "Modifies the bobbing animation.", Category.RENDER, new String[0]);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (!isNotInGame() && this.multiplier.getValue().floatValue() <= 0.0f) {
            minecraftClient.player.xBob = 0.0f;
            minecraftClient.player.yBob = 0.0f;
            minecraftClient.player.xBobO = 0.0f;
            minecraftClient.player.yBobO = 0.0f;
        }
    }
}