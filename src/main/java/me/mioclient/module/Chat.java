package me.mioclient.module;

import me.mioclient.render.hud.ResizableHudElement;

import me.mioclient.render.hud.ResizableHudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Chat.class */
public class Chat extends me.mioclient.module.HudModule {
    public Chat() {
        super("Chat", new String[0]);
        setElement(new ResizableHudElement(this));
    }

    public float getChatYOffset() {
        if (this.moduleListSearchHelper4.getMeasuredSize() == null) {
            return 0.0f;
        }
        return ((this.moduleListSearchHelper4.getY() - (minecraftClient.getWindow().getScaledHeight() - Float.intBitsToFloat(1109393408))) / getScale()) + getSpeed();
    }

    public float getChatXOffset() {
        if (this.moduleListSearchHelper4.getMeasuredSize() == null) {
            return 0.0f;
        }
        return this.moduleListSearchHelper4.getX() / getScale();
    }

    public float getScale() {
        return ((Double) minecraftClient.options.getChatScale().getValue()).floatValue();
    }

    public float getSpeed() {
        return ChatHud.getHeight(((Double) minecraftClient.options.getChatHeightUnfocused().getValue()).floatValue());
    }

    @Override // me.mioclient.module.HudModule
    public void renderContent(DrawContext drawContext) {
        super.renderContent(drawContext);
    }

    @Override // me.mioclient.module.HudModule
    public float[] getSize() {
        return new float[]{(((Double) minecraftClient.options.getChatWidth().getValue()).floatValue() * Float.intBitsToFloat(1133248512)) + Float.intBitsToFloat(1109393408) + (Float.intBitsToFloat(1094713344) * getScale()), getSpeed() * getScale()};
    }
}
