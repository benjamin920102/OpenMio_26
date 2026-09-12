package me.mioclient.gui.widget;

import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.api.Toggleable;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.setting.ColorPickerComponent;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ToggleableActionButton.class */
public abstract class ToggleablePanelTextButton extends PanelTextButton implements Toggleable {
    public boolean toggled;

    public ToggleablePanelTextButton(int i, ColorPickerComponent stopwatchPresetHelper2, String str, java.lang.Runnable runnable) {
        super(i, stopwatchPresetHelper2, str, runnable);
        this.runnable = () -> {
            runnable.run();
            this.toggled = !this.toggled;
        };
    }

    @Override // me.mioclient.gui.widget.PanelTextButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        if (this.stopwatchPresetHelper2.isHidden()) {
            return;
        }
        ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, this.name, this.presetEnumSettingHelper.getX() + getX(), (((this.presetEnumSettingHelper.getY() + this.num) + getY()) + getTextOffsetY()) - getAnimationOffset(), this.toggled ? getUI().color.getValue() : getUI().textColor.getValue());
    }

    @Override // me.mioclient.api.Toggleable
    public boolean isToggled() {
        return this.toggled;
    }

    @Override // me.mioclient.api.Toggleable
    public void enable() {
    }

    @Override // me.mioclient.api.Toggleable
    public void disable() {
    }
}
