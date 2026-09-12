package me.mioclient.module.render;

import me.mioclient.mode.combat.KeyPearlMode;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.client.EndDrawingEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import me.mioclient.setting.ColorSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Glint.class */
public class Glint extends Module {
    public static final Identifier identifier = Identifier.of("mio-mount", "textures/shine.png");
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-1, true)));

    public Glint() {
        super("Glint", "Changes your enchantment glint's color.", Category.RENDER, new String[0]);
    }

    @Listen
    public void onEndDrawing(EndDrawingEvent endDrawingEvent) {
        if (endDrawingEvent.getPhase() != KeyPearlMode.Pre) {
            RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
            return;
        }
        Color value = this.color.getValue();
        MinecraftClient.getInstance().getTextureManager().getTexture(identifier).setFilter(true, false);
        RenderSystem.setShaderTexture(0, identifier);
        RenderSystem.setShaderColor(value.getRed() / Float.intBitsToFloat(1132396544), value.getGreen() / Float.intBitsToFloat(1132396544), value.getBlue() / Float.intBitsToFloat(1132396544), Float.intBitsToFloat(1065353216));
    }
}
