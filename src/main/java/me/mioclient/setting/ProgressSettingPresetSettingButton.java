package me.mioclient.setting;

import me.mioclient.gui.widget.PanelComponent;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.anim.AnimationProgress;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.ClosableElement;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;

import java.util.function.Supplier;
import me.mioclient.api.Setting;
import me.mioclient.util.anim.AnimationProgress;
import me.mioclient.module.client.UI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import me.mioclient.gui.widget.PanelComponent;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ProgressSettingPresetSettingButton.class */
public class ProgressSettingPresetSettingButton extends SettingPresetSettingButton<Boolean> {
    public final AnimationProgress progress;

    public ProgressSettingPresetSettingButton(ClickGuiPanel presetEnumSettingHelper, ClosableElement presetHelper_2, Setting<?> setting) {
        super(presetEnumSettingHelper, presetHelper_2, (BooleanSetting) setting);
        this.progress = new AnimationProgress((Supplier<Float>) () -> {
            return Float.valueOf(Float.intBitsToFloat(1073741824) * UI.uI.animSpeed.getValue().floatValue());
        }, true);
    }

    @Override // me.mioclient.setting.SettingPresetSettingButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void mouseClicked(double d, double d2, int i) {
        if (isHidden()) {
            return;
        }
        if (this.setting.flag4 && i == 0) {
            return;
        }
        super.mouseClicked(d, d2, i);
        if (containsPoint(d, d2)) {
            if (i == 0) {
                this.setting.setValue(Boolean.valueOf(!((Boolean) this.setting.getValue()).booleanValue()));
            }
            if (i == 1 && this.setting.flag2) {
                this.setting.flag = !this.setting.flag;
            }
        }
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        if (isHidden()) {
            this.progress.reset();
            return;
        }
        this.progress.animateToggle(((Boolean) this.setting.getValue()).booleanValue());
        float f = this.progress.getValue();
        super.render(drawContext, matrixStack, d, d2);
        if ((((Boolean) this.setting.getValue()).booleanValue() || f > Double.longBitsToDouble(4576918229304087675L)) && !this.setting.flag4) {
            Shape2DRenderer.drawRect(matrixStack, this.presetEnumSettingHelper.getX() + getPadding(), this.presetEnumSettingHelper.getY() + this.num + Float.intBitsToFloat(1056964608), this.presetEnumSettingHelper.getX() + Math.max((this.presetEnumSettingHelper.getTotalWidth() * f) - Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1073741824)), ((this.presetEnumSettingHelper.getY() + this.num) + getFontHeight()) - Float.intBitsToFloat(1056964608), getUI().color.getValue());
        }
        String name = this.setting.getName();
        if (!name.isEmpty()) {
            renderScrolling(matrixStack, name, () -> {
                ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, name, this.presetEnumSettingHelper.getX() + 4, ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
            });
        }
        if (this.setting.flag2 && !this.setting.flag4) {
            String str = this.setting.flag ? "-" : "+";
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str, ((this.presetEnumSettingHelper.getX() + this.presetEnumSettingHelper.getTotalWidth()) - 4) - ClientTextRenderer.INSTANCE.getStringWidth(str), ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
        }
        if (this.setting.flag4) {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, "...", ((this.presetEnumSettingHelper.getX() + this.presetEnumSettingHelper.getTotalWidth()) - 4) - ClientTextRenderer.INSTANCE.getStringWidth("..."), ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
        }
    }
}
