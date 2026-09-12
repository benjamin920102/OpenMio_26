package me.mioclient.setting;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.mode.client.CursorType;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.ClosableElement;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;

import me.mioclient.api.Setting;
import me.mioclient.util.time.Stopwatch;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import me.mioclient.mode.client.CursorType;
import me.mioclient.setting.SettingPresetSettingButton;
import me.mioclient.setting.StringSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PresetSettingPresetActionButton.class */
public class StringSettingRow extends SettingPresetSettingButton<String> {
    public final Stopwatch stopwatch;
    public boolean flag;
    public String string;
    public int num;

    public StringSettingRow(ClickGuiPanel presetEnumSettingHelper, ClosableElement presetHelper_2, Setting<?> setting) {
        super(presetEnumSettingHelper, presetHelper_2, (StringSetting) setting);
        this.stopwatch = new Stopwatch();
        this.flag = false;
        this.num = 0;
    }

    @Override // me.mioclient.setting.SettingPresetSettingButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void mouseClicked(double d, double d2, int i) {
        if (isHidden()) {
            this.flag2 = false;
            return;
        }
        super.mouseClicked(d, d2, i);
        if (containsPoint(d, d2) && i == 0) {
            this.flag2 = !this.flag2;
            if (this.flag2) {
                if (getClickGuiScreen().getActiveSettingButton() != null && getClickGuiScreen().getActiveSettingButton() != this) {
                    getClickGuiScreen().getActiveSettingButton().flag2 = false;
                }
                getClickGuiScreen().setActiveSetting(this);
            } else if (getClickGuiScreen().getActiveSettingButton() == this) {
                getClickGuiScreen().setActiveSetting(null);
            }
            this.string = (String) this.setting.getValue();
            if (this.string == null || this.string.isEmpty()) {
                return;
            }
            this.num = this.string.length();
        }
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        if (isHidden()) {
            if (this.flag2 && getClickGuiScreen().getActiveSettingButton() == this) {
                getClickGuiScreen().setActiveSetting(null);
            }
            this.flag2 = false;
            return;
        }
        super.render(drawContext, matrixStack, d, d2);
        if (this.flag) {
            AbstractClickGuiScreen.cursorType = CursorType.INPUT;
        }
        Shape2DRenderer.drawRect(matrixStack, this.presetEnumSettingHelper.getX() + getPadding(), this.presetEnumSettingHelper.getY() + this.num + Float.intBitsToFloat(1056964608), (this.presetEnumSettingHelper.getX() + this.presetEnumSettingHelper.getTotalWidth()) - 1, ((this.presetEnumSettingHelper.getY() + this.num) + getFontHeight()) - Float.intBitsToFloat(1056964608), getUI().color.getValue());
        if (this.string != null) {
            this.num = MathHelper.clamp(this.num, 0, this.string.length());
        }
        if (!this.flag2) {
            renderScrolling(matrixStack, new StringTemplate().arg((String) this.setting.getValue()).arg(this.setting.getName()).format("\u0001: \u0001"), () -> {
                ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, new StringTemplate().arg((String) this.setting.getValue()).arg(this.setting.getName()).format("\u0001: \u0001"), this.presetEnumSettingHelper.getX() + 4, ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
            });
            return;
        }
        matrixStack.push();
        String string95 = getBlinkingCursor();
        float f = ClientTextRenderer.INSTANCE.getStringWidth(new StringTemplate().arg(this.string.substring(0, this.num)).format("\u0001_"));
        if (f > getPanel().getTotalWidth() - (getPadding() * 2)) {
            matrixStack.translate((getPanel().getTotalWidth() - (getPadding() * 2)) - f, 0.0f, 0.0f);
        }
        if (!this.string.isEmpty()) {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, this.string, this.presetEnumSettingHelper.getX() + 4, ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
        }
        if (!string95.isBlank()) {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, string95, this.presetEnumSettingHelper.getX() + 4 + ClientTextRenderer.INSTANCE.getStringWidth(this.string.substring(0, this.num)), ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
        }
        matrixStack.pop();
    }

    @Override // me.mioclient.gui.GuiComponent
    public void keyPressed(int i) {
        if (!isHidden() && this.flag2) {
            ClientServices.getClickGui().suppressEscapeClose();
            switch (i) {
                case 86:
                    if (GLFW.glfwGetKey(minecraftClient.getWindow().getHandle(), 341) == 1) {
                        this.string = new StringBuilder(this.string).insert(this.num, minecraftClient.keyboard.getClipboard()).toString();
                        return;
                    }
                    return;
                case 256:
                    this.flag2 = false;
                    return;
                case 257:
                    this.flag2 = false;
                    this.setting.setValue(this.string);
                    this.num = this.string.length();
                    return;
                case 259:
                    if (this.string.length() == 0 || this.num == 0) {
                        return;
                    }
                    int i2 = this.num - 1;
                    if (this.num == this.string.length()) {
                        this.string = this.string.substring(0, this.string.length() - 1);
                    } else if (i2 >= 0 && i2 <= this.string.length()) {
                        this.string = new StringBuilder(this.string).deleteCharAt(i2).toString();
                    }
                    this.num--;
                    return;
                case 262:
                    if (this.num < this.string.length()) {
                        this.num++;
                        return;
                    }
                    return;
                case 263:
                    if (this.num > 0) {
                        this.num--;
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    @Override // me.mioclient.gui.GuiComponent
    public void keyTyped(char c) {
        if (!isHidden() && this.flag2) {
            this.string = new StringBuilder(this.string).insert(this.num, c).toString();
            this.num++;
        }
    }

    public String getBlinkingCursor() {
        if (this.stopwatch.hasElapsedMs(500L)) {
            this.flag = !this.flag;
            this.stopwatch.reset();
        }
        return this.flag ? "_" : "";
    }
}
