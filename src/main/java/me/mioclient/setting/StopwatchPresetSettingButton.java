package me.mioclient.setting;

import me.mioclient.gui.widget.PanelComponent;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.render.shape.Shape2DRenderer;

import java.awt.Color;
import me.mioclient.util.time.Stopwatch;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import me.mioclient.gui.widget.PanelComponent;
import me.mioclient.gui.widget.PanelComponent;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/StopwatchPresetSettingButton.class */
public abstract class StopwatchPresetSettingButton extends PanelComponent {
    public final int num;
    public final int num2;
    public final Stopwatch stopwatch;

    public StopwatchPresetSettingButton(ClickGuiPanel presetEnumSettingHelper, int i, int i2, int i3) {
        super(presetEnumSettingHelper, i);
        this.stopwatch = new Stopwatch();
        this.num = i2;
        this.num2 = i3;
    }

    public StopwatchPresetSettingButton(ClickGuiPanel presetEnumSettingHelper, int i) {
        this(presetEnumSettingHelper, i, 22, 22);
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        super.render(drawContext, matrixStack, d, d2);
        if (this.stopwatch.hasElapsedMs(getTickDelay())) {
            onTickElapsed();
            this.stopwatch.reset();
        }
    }

    public abstract void onTickElapsed();

    public abstract int getTickDelay();

    public void drawGridCell(MatrixStack matrixStack, int i, int i2, Color color) {
        Shape2DRenderer.drawRect(matrixStack, getPanel().getX() + (i * getCellWidth()), getPanel().getY() + this.num + (i2 * getCellHeight()), getPanel().getX() + (i * getCellWidth()) + getCellWidth(), getPanel().getY() + this.num + (i2 * getCellHeight()) + getCellHeight(), color);
    }

    public int getCellWidth() {
        return getPanel().getTotalWidth() / this.num;
    }

    public int getCellHeight() {
        return getFontHeight() / this.num2;
    }
}
