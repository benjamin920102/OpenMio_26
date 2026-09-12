package me.mioclient.setting;

import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.screen.SoundSelectScreen;
import me.mioclient.mode.client.CursorType;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.ClosableElement;
import me.mioclient.gui.screen.SoundSelectScreen;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;

import me.mioclient.api.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import me.mioclient.mode.client.CursorType;
import me.mioclient.setting.SettingPresetSettingButton;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SettingPresetActionButton.class */
public class SoundSettingRow extends SettingPresetSettingButton<SoundIdentifier> {
    public SoundSettingRow(ClickGuiPanel presetEnumSettingHelper, ClosableElement presetHelper_2, Setting<?> setting) {
        super(presetEnumSettingHelper, presetHelper_2, (Setting<SoundIdentifier>) setting);
    }

    @Override // me.mioclient.setting.SettingPresetSettingButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void mouseClicked(double d, double d2, int i) {
        if (!isHidden() && containsPoint(d, d2) && i == 0) {
            minecraftClient.setScreen(new SoundSelectScreen(this.setting, getClickGuiScreen()));
        }
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        if (isHidden()) {
            return;
        }
        super.render(drawContext, matrixStack, d, d2);
        if (containsPoint(d, d2)) {
            AbstractClickGuiScreen.cursorType = CursorType.POINTER;
        }
        Shape2DRenderer.drawRect(matrixStack, this.presetEnumSettingHelper.getX() + getPadding(), this.presetEnumSettingHelper.getY() + this.num + Float.intBitsToFloat(1056964608), (this.presetEnumSettingHelper.getX() + this.presetEnumSettingHelper.getTotalWidth()) - 1, ((this.presetEnumSettingHelper.getY() + this.num) + getFontHeight()) - Float.intBitsToFloat(1056964608), getUI().color.getValue());
        String string2921 = new StringTemplate().arg(((SoundIdentifier) this.setting.getValue()).getName()).arg(this.setting.getName()).format("\u0001: \u0001");
        renderScrolling(matrixStack, string2921, () -> {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, string2921, this.presetEnumSettingHelper.getX() + 4, ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
        });
    }
}
