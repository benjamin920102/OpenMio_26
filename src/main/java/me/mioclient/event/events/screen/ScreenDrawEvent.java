package me.mioclient.event.events.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/feature/Event_2.class */
public class ScreenDrawEvent extends me.mioclient.event.Event {
    public final Screen screen;
    public DrawContext drawContext;
    public float x;
    public float y;
    public Runnable runnable = null;

    public ScreenDrawEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public MatrixStack getMatrixStack() {
        return this.drawContext.getMatrices();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public DrawContext getDrawContext() {
        return this.drawContext;
    }

    public void setDrawContext(DrawContext drawContext) {
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
