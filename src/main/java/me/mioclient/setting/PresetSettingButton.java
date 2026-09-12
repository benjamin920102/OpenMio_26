package me.mioclient.setting;

import me.mioclient.api.NamedValue;
import me.mioclient.gui.widget.PanelComponent;

import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import me.mioclient.gui.widget.PanelComponent;
import me.mioclient.api.Setting;
import me.mioclient.gui.widget.PanelComponent;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PresetSettingButton.class */
public class PresetSettingButton extends PanelComponent {
    public final PresetSettingPresetSettingButton presetSettingSearchHelper419;
    public final String string;
    public final int index;

    public PresetSettingButton(PresetSettingPresetSettingButton presetSettingSearchHelper419, String str, int i, int i2) {
        super(presetSettingSearchHelper419.getPanel(), i);
        this.string = str;
        this.index = i2;
        this.presetSettingSearchHelper419 = presetSettingSearchHelper419;
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        super.render(drawContext, matrixStack, d, d2);
        ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, this.string, (getPanel().getX() + (getPanel().getTotalWidth() / Float.intBitsToFloat(1073741824))) - (ClientTextRenderer.INSTANCE.getStringWidth(this.string) / Float.intBitsToFloat(1073741824)), (((this.presetSettingSearchHelper419.getY() + this.num) + getTextOffsetY()) - Float.intBitsToFloat(1065353216)) - getAnimationOffset(), getUI().textColor.getValue());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void mouseClicked(double d, double d2, int i) {
        if (!this.presetSettingSearchHelper419.isHidden() && containsPoint(d, d2) && this.presetSettingSearchHelper419.isShown()) {
            super.mouseClicked(d, d2, i);
            if (this.presetSettingSearchHelper419.getClosable().isShown() && i == 0) {
                for (Object obj : (Enum[]) this.presetSettingSearchHelper419.getSetting().getValue().getDeclaringClass().getEnumConstants()) {
                    if ((obj instanceof NamedValue) && ((NamedValue) obj).getName().equalsIgnoreCase(this.string)) {
                        ((me.mioclient.api.Setting) this.presetSettingSearchHelper419.getSetting()).setValue(obj);
                        this.presetSettingSearchHelper419.setSelectedIndex(this.index);
                    }
                }
            }
        }
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public boolean containsPoint(double d, double d2) {
        return d > ((double) this.presetSettingSearchHelper419.getX()) && d < ((double) (this.presetSettingSearchHelper419.getX() + getPanel().getTotalWidth())) && d2 > ((double) (this.presetSettingSearchHelper419.getY() + this.num)) && d2 < ((double) ((this.presetSettingSearchHelper419.getY() + getRowHeight()) + this.num));
    }

    @Override // me.mioclient.gui.GuiComponent
    public int getFontHeight() {
        if (this.presetSettingSearchHelper419.isHidden()) {
            return 0;
        }
        return super.getFontHeight();
    }

    public String getLabel() {
        return this.string;
    }
}
