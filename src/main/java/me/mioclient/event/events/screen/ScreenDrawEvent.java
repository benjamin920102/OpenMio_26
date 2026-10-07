package me.mioclient.event.events.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.vertex.PoseStack;

public class ScreenDrawEvent extends me.mioclient.event.Event {
    public final Screen screen;
    public GuiGraphicsExtractor drawContext;
    public float x;
    public float y;
    public Runnable runnable = null;

    public ScreenDrawEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public org.joml.Matrix3x2fStack getMatrixStack() {
        return this.drawContext.pose();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public GuiGraphicsExtractor getDrawContext() {
        return this.drawContext;
    }

    public void setDrawContext(GuiGraphicsExtractor drawContext) {
        this.drawContext = drawContext;
    }

    public void setX(float f) {
        this.x = f;
    }

    public void setY(float f) {
        this.y = f;
    }

    public void setCallback(Runnable runnable) {
        this.runnable = runnable;
    }

    public void runCallback() {
        if (this.runnable != null) {
            this.runnable.run();
        }
    }
}
