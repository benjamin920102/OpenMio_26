package me.mioclient.module.render;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.render.shader.BlurFramebuffer;

import me.mioclient.gui.screen.AbstractClickGuiScreen;

import java.awt.Color;
import me.mioclient.render.shader.BlurFramebuffer;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.render.HudRenderEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Blur.class */
public class Blur extends Module {
    public Setting<Integer> radius = add(new NumberSetting<>("Radius", 6, 1, 8));

    public Blur() {
        super("Blur", "Blurs the background in GUI's.", Category.RENDER, new String[0]);
    }

    @Listen(get219= RenderConstants.num5)
    public void onHudRender(HudRenderEvent matrixStackEvent_2) {
        do991(matrixStackEvent_2.getDrawContext());
    }

    public void do991(DrawContext drawContext) {
        Screen screen = minecraftClient.currentScreen;
        if (screen instanceof AbstractClickGuiScreen) {
            return;
        }
        if ((minecraftClient.currentScreen == null || (minecraftClient.currentScreen instanceof ChatScreen)) ? false : true) {
            BlurFramebuffer.renderWithBlur(() -> {
                drawContext.fill(0, 0, minecraftClient.getWindow().getScaledWidth(), minecraftClient.getWindow().getScaledHeight(), new Color(0.0f, 0.0f, 0.0f, Float.intBitsToFloat(1065353216)).hashCode());
            }, getBlurRadius());
        }
    }

    public int getBlurRadius() {
        return this.radius.getValue().intValue();
    }
}
