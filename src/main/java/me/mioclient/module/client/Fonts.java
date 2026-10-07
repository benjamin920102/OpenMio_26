package me.mioclient.module.client;

import me.mioclient.event.events.client.ClientTickEvent;
import net.minecraft.client.gui.Font;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.mode.client.FontStyle;
import me.mioclient.render.font.ClientTextRenderer;

import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import org.lwjgl.glfw.GLFW;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.StringSetting;

public class Fonts extends Module {
    public static Fonts fonts;
    public final AtomicBoolean atomicBoolean;
    public static final Runnable runnable = () -> {
        if (minecraftClient.player != null || (minecraftClient.gui.screen() instanceof AbstractClickGuiScreen)) {
            ClientTextRenderer.INSTANCE.setAdvanceGlyph(ClientTextRenderer.INSTANCE.createAdvanceGlyph());
        }
    };
    public Setting<String> font = add(new StringSetting("Font", "Verdana"));
    public Setting<FontStyle> style = add(new EnumSetting<>("Style", FontStyle.PLAIN));
    public Setting<Boolean> antiAlias = add(new BooleanSetting("AntiAlias", false));
    public Setting<Boolean> chat = add(new BooleanSetting("Chat", false));
    public Setting<Integer> size = add(new NumberSetting<>("Size", 16, 12, 30));
    public Setting<Integer> shift = add(new NumberSetting<>("Shift", -2, -4, 4));
    public Setting<Float> shadow = add(new NumberSetting<>("Shadow", 0.7f, 0.5f, 1.0f));
    public Setting<Integer> translate = add(new NumberSetting<>("Translate", -1, -4, 4));

    public Fonts() {
        super("Fonts", "Manages the client's font renderer.", Category.CLIENT, new String[0]);
        this.atomicBoolean = new AtomicBoolean(false);
        this.style.setChangeCallback(runnable);
        this.antiAlias.setChangeCallback(runnable);
        this.size.setChangeCallback(() -> {
            this.atomicBoolean.set(true);
        });
        this.font.setChangeCallback(runnable);
        fonts = this;
        setDrawn(false);
    }

    @Listen
    public void onClientTick(ClientTickEvent fontsEvent) {
        if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().handle(), 0) == 1 || !this.atomicBoolean.get()) {
            return;
        }
        ClientTextRenderer.INSTANCE.setAdvanceGlyph(ClientTextRenderer.INSTANCE.createAdvanceGlyph());
        this.atomicBoolean.set(false);
    }
}