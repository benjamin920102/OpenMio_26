package me.mioclient.module.render;

import me.mioclient.mode.combat.KeyPearlMode;

import java.awt.Color;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.client.EndDrawingEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.resources.Identifier;
import me.mioclient.setting.ColorSetting;

public class Glint extends Module {
    public static final Identifier identifier = Identifier.fromNamespaceAndPath("mio-mount", "textures/shine.png");
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-1, true)));
    public static volatile Color activeGlintColor = new Color(-1, true);
    public static volatile boolean glintActive = false;

    public Glint() {
        super("Glint", "Changes your enchantment glint's color.", Category.RENDER, new String[0]);
    }

    @Listen
    public void onEndDrawing(EndDrawingEvent endDrawingEvent) {
        if (endDrawingEvent.getPhase() != KeyPearlMode.Pre) {
            glintActive = false;
            activeGlintColor = new Color(255, 255, 255, 255);
            return;
        }
        Color value = this.color.getValue();
        glintActive = isToggled();
        activeGlintColor = value;
    }

    public static Color getActiveGlintColor() {
        return activeGlintColor;
    }

    public static boolean isGlintActive() {
        return glintActive;
    }
}