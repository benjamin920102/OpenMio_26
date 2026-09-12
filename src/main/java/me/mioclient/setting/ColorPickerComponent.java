package me.mioclient.setting;

import me.mioclient.gui.widget.PanelComponent;
import me.mioclient.gui.widget.PanelComponentFlag;
import me.mioclient.gui.widget.PanelTextButton;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.mode.client.CursorType;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.anim.AnimationProgress;
import me.mioclient.util.anim.InterpolatedFloat;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.ClosableElement;
import me.mioclient.gui.GuiComponent;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.anim.InterpolatedFloat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.mioclient.api.Setting;
import me.mioclient.util.anim.AnimationProgress;
import me.mioclient.util.time.Stopwatch;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.mode.client.CursorType;
import me.mioclient.gui.widget.PanelComponent;
import me.mioclient.gui.widget.PanelComponentFlag;
import me.mioclient.gui.widget.PanelTextButton;
import me.mioclient.setting.ProgressSettingPresetSettingButton;
import me.mioclient.setting.SettingPresetSettingButton;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ColorPickerComponent.class */
public class ColorPickerComponent extends SettingPresetSettingButton<Color> implements ClosableElement {
    public final List<GuiComponent> list;
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public final Stopwatch stopwatch3;
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;
    public boolean flag5;
    public float[] floatArr;
    public final InterpolatedFloat hUDHelper_2;
    public final InterpolatedFloat hUDHelper_22;
    public final InterpolatedFloat hUDHelper_23;
    public final InterpolatedFloat hUDHelper_24;
    public final AnimationProgress progress;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/StopwatchPresetHelper2$Inner.class */
    private static class Inner extends ProgressSettingPresetSettingButton {
        public Inner(ColorPickerComponent stopwatchPresetHelper2) {
            super(stopwatchPresetHelper2.presetEnumSettingHelper, stopwatchPresetHelper2, new BooleanSetting("", Boolean.valueOf(((ColorSetting) stopwatchPresetHelper2.setting).isRainbow())));
            this.setting.setChangeCallback(() -> {
                ((ColorSetting) stopwatchPresetHelper2.setting).setRainbowEnabled(((Boolean) this.setting.getValue()).booleanValue());
            });
            addFlags(PanelComponentFlag.PADDING_SHIFT);
        }

        @Override // me.mioclient.setting.ProgressSettingPresetSettingButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
        public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
            super.render(drawContext, matrixStack, d, d2);
            String str = "Rainbow";
            renderScrolling(matrixStack, "Rainbow", () -> {
                ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str, (this.presetEnumSettingHelper.getX() + (getPanel().getTotalWidth() * Float.intBitsToFloat(1056964608))) - (ClientTextRenderer.INSTANCE.getStringWidth(str) * Float.intBitsToFloat(1056964608)), ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
            });
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/StopwatchPresetHelper2$Inner_2.class */
    private static class Inner_2 extends PanelTextButton {
        public Inner_2(int i, ColorPickerComponent stopwatchPresetHelper2) {
            super(i, stopwatchPresetHelper2, "Paste", () -> {
                try {
                    Color color828 = ColorUtils.colorFromHex(minecraftClient.keyboard.getClipboard().trim());
                    stopwatchPresetHelper2.syncHsbFromColor(color828);
                    stopwatchPresetHelper2.getSetting().setValue(color828);
                    stopwatchPresetHelper2.stopwatch2.reset();
                } catch (Exception e) {
                    stopwatchPresetHelper2.stopwatch.reset();
                }
            });
        }

        @Override // me.mioclient.gui.widget.PanelTextButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
        public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
            if (this.stopwatchPresetHelper2.isHidden()) {
                return;
            }
            Shape2DRenderer.drawRect(matrixStack, this.presetEnumSettingHelper.getX() + (this.presetEnumSettingHelper.getTotalWidth() / Float.intBitsToFloat(1073741824)) + Float.intBitsToFloat(1056964608), (int) (this.presetEnumSettingHelper.getY() + this.num + getY()), (this.presetEnumSettingHelper.getX() + this.presetEnumSettingHelper.getTotalWidth()) - Float.intBitsToFloat(1056964608), (((this.presetEnumSettingHelper.getY() + this.num) + getY()) + getFontHeight()) - Float.intBitsToFloat(1056964608), getUI().color.getValue());
            super.render(drawContext, matrixStack, d, d2);
        }

        @Override // me.mioclient.gui.widget.PanelTextButton
        public float getX() {
            return ((getPanel().getTotalWidth() - (getPanel().getTotalWidth() / Float.intBitsToFloat(1082130432))) - ClientTextRenderer.INSTANCE.getStringWidth(getName())) + (ClientTextRenderer.INSTANCE.getStringWidth(getName()) * Float.intBitsToFloat(1056964608));
        }

        @Override // me.mioclient.gui.widget.PanelTextButton
        public float getY() {
            return 0.0f;
        }

        @Override // me.mioclient.gui.widget.PanelTextButton
        public String getName() {
            return !this.stopwatchPresetHelper2.stopwatch.hasElapsedMs(1000L) ? "Invalid" : !this.stopwatchPresetHelper2.stopwatch2.hasElapsedMs(1000L) ? "Pasted" : super.getName();
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/StopwatchPresetHelper2$Inner_3.class */
    private static class Inner_3 extends ProgressSettingPresetSettingButton {
        public Inner_3(ColorPickerComponent stopwatchPresetHelper2) {
            super(stopwatchPresetHelper2.presetEnumSettingHelper, stopwatchPresetHelper2, new BooleanSetting("", Boolean.valueOf(((ColorSetting) stopwatchPresetHelper2.setting).isSyncToTheme())));
            this.setting.setChangeCallback(() -> {
                ((ColorSetting) stopwatchPresetHelper2.setting).setSyncEnabled(((Boolean) this.setting.getValue()).booleanValue());
            });
            addFlags(PanelComponentFlag.PADDING_SHIFT);
        }

        @Override // me.mioclient.setting.ProgressSettingPresetSettingButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
        public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
            super.render(drawContext, matrixStack, d, d2);
            String str = "Sync";
            renderScrolling(matrixStack, "Sync", () -> {
                ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str, (this.presetEnumSettingHelper.getX() + (getPanel().getTotalWidth() * Float.intBitsToFloat(1056964608))) - (ClientTextRenderer.INSTANCE.getStringWidth(str) * Float.intBitsToFloat(1056964608)), ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
            });
        }

        @Override // me.mioclient.gui.GuiComponent
        public void init() {
            this.setting.setValue(Boolean.valueOf(((ColorSetting) ((ColorPickerComponent) this.presetHelper_2).getSetting()).isSyncToTheme()));
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/StopwatchPresetHelper2$Inner_4.class */
    private static class Inner_4 extends PanelTextButton {
        public Inner_4(int i, ColorPickerComponent stopwatchPresetHelper2) {
            super(i, stopwatchPresetHelper2, "Copy", () -> {
                minecraftClient.keyboard.setClipboard(ColorUtils.colorToHex(stopwatchPresetHelper2.getColor(), true));
                stopwatchPresetHelper2.stopwatch3.reset();
            });
        }

        @Override // me.mioclient.gui.widget.PanelTextButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
        public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
            if (this.stopwatchPresetHelper2.isHidden()) {
                return;
            }
            Shape2DRenderer.drawRect(matrixStack, this.presetEnumSettingHelper.getX() + 2, (int) (this.presetEnumSettingHelper.getY() + this.num + getY()), (this.presetEnumSettingHelper.getX() + (this.presetEnumSettingHelper.getTotalWidth() / Float.intBitsToFloat(1073741824))) - Float.intBitsToFloat(1056964608), (((this.presetEnumSettingHelper.getY() + this.num) + getY()) + getFontHeight()) - Float.intBitsToFloat(1056964608), getUI().color.getValue());
            super.render(drawContext, matrixStack, d, d2);
        }

        @Override // me.mioclient.gui.widget.PanelTextButton
        public float getX() {
            return (getPanel().getTotalWidth() / Float.intBitsToFloat(1082130432)) - (ClientTextRenderer.INSTANCE.getStringWidth(getName()) / Float.intBitsToFloat(1073741824));
        }

        @Override // me.mioclient.gui.widget.PanelTextButton
        public float getY() {
            return 0.0f;
        }

        @Override // me.mioclient.gui.widget.PanelTextButton
        public String getName() {
            return !this.stopwatchPresetHelper2.stopwatch3.hasElapsedMs(1000L) ? "Copied" : super.getName();
        }
    }

    public ColorPickerComponent(ClickGuiPanel presetEnumSettingHelper, ClosableElement presetHelper_2, Setting<?> setting) {
        super(presetEnumSettingHelper, presetHelper_2, (ColorSetting) setting);
        this.list = new ArrayList(3);
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        this.stopwatch3 = new Stopwatch();
        this.floatArr = new float[3];
        this.hUDHelper_2 = new InterpolatedFloat(Float.intBitsToFloat(1084227584), true);
        this.hUDHelper_22 = new InterpolatedFloat(Float.intBitsToFloat(1084227584), true);
        this.hUDHelper_23 = new InterpolatedFloat(Float.intBitsToFloat(1084227584), true);
        this.hUDHelper_24 = new InterpolatedFloat(Float.intBitsToFloat(1084227584), true);
        this.progress = new AnimationProgress(Float.intBitsToFloat(1082130432), true);
        int i = (int) (this.num + getRowHeight() + getPanel().getTotalWidth() + Double.longBitsToDouble(4619004367821864960L) + getRowSpacing());
        this.list.add(new Inner_4(i, this));
        this.list.add(new Inner_2(i, this));
        if (!((ColorSetting) setting).isThemeColor()) {
            this.list.add(new Inner_3(this));
        }
        this.list.add(new Inner(this));
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        super.render(drawContext, matrixStack, d, d2);
        if (isHidden() || !this.flag) {
            this.hUDHelper_2.setImmediate(0.0f);
            this.hUDHelper_22.setImmediate(0.0f);
            this.hUDHelper_23.setImmediate(0.0f);
            this.hUDHelper_24.setImmediate(0.0f);
        }
        if (isHidden()) {
            return;
        }
        if (this.flag && this.flag) {
            AbstractClickGuiScreen.cursorType = CursorType.STANDARD;
        }
        renderScrolling(matrixStack, this.setting.getName(), () -> {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, this.setting.getName(), this.presetEnumSettingHelper.getX() + 4, ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
        });
        Shape2DRenderer.drawRect(matrixStack, (getX() + this.presetEnumSettingHelper.getTotalWidth()) - Float.intBitsToFloat(1095237632), getY() + Float.intBitsToFloat(1065353216), (getX() + this.presetEnumSettingHelper.getTotalWidth()) - Float.intBitsToFloat(1080033280), getY() + Float.intBitsToFloat(1092616192), getUI().color.getValue());
        Shape2DRenderer.drawRect(matrixStack, (getX() + this.presetEnumSettingHelper.getTotalWidth()) - Float.intBitsToFloat(1094713344), getY() + Float.intBitsToFloat(1069547520), (getX() + this.presetEnumSettingHelper.getTotalWidth()) - Float.intBitsToFloat(1082130432), getY() + Float.intBitsToFloat(1092091904), ColorUtils.withAlpha((Color) this.setting.getValue(), 255));
        if (this.flag) {
            int alpha = ((Color) this.setting.getValue()).getAlpha();
            int i = getRowHeight();
            if (this.flag3) {
                AbstractClickGuiScreen.cursorType = CursorType.POINTER;
                this.floatArr[1] = (float) MathHelper.clamp(((d - getX()) - Double.longBitsToDouble(4617315517961601024L)) / (getPanel().getTotalWidth() - 8), 0.0d, Double.longBitsToDouble(4607182418800017408L));
                this.floatArr[2] = Float.intBitsToFloat(1065353216) - ((float) MathHelper.clamp((((d2 - getY()) - i) - Double.longBitsToDouble(4613937818241073152L)) / (getPanel().getTotalWidth() - 8), 0.0d, Double.longBitsToDouble(4607182418800017408L)));
            } else if (this.flag4) {
                AbstractClickGuiScreen.cursorType = CursorType.POINTER;
                this.floatArr[0] = Math.min((((float) Math.min(Math.max(getX(), d - Double.longBitsToDouble(4607182418800017408L)), getX() + (getPanel().getTotalWidth() - 4))) - getX()) / (getPanel().getTotalWidth() - 4), Float.intBitsToFloat(1065353216));
            } else if (this.flag5 && !this.setting.flag3) {
                AbstractClickGuiScreen.cursorType = CursorType.POINTER;
                alpha = (int) (Math.min(Float.intBitsToFloat(1065353216) - ((((float) Math.min(Math.max(getX(), d - Double.longBitsToDouble(4613937818241073152L)), getX() + (getPanel().getTotalWidth() - 4))) - getX()) / (getPanel().getTotalWidth() - 4)), Float.intBitsToFloat(1065353216)) * Float.intBitsToFloat(1132396544));
            }
            int HSBtoRGB = Color.HSBtoRGB(this.floatArr[0], Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
            float intBitsToFloat = ((HSBtoRGB >> 16) & 255) / Float.intBitsToFloat(1132396544);
            float intBitsToFloat2 = ((HSBtoRGB >> 8) & 255) / Float.intBitsToFloat(1132396544);
            float intBitsToFloat3 = (HSBtoRGB & 255) / Float.intBitsToFloat(1132396544);
            int hashCode = this.flag3 ? -1 : getUI().color.getValue().hashCode();
            Shape2DRenderer.drawGradientRect(matrixStack.peek().getPositionMatrix(), getX() + Float.intBitsToFloat(1075838976), getY() + i + Float.intBitsToFloat(1056964608), getPanel().getTotalWidth() - 3, getPanel().getTotalWidth() - 3, hashCode, hashCode, hashCode, hashCode);
            Shape2DRenderer.drawGradientRect(matrixStack.peek().getPositionMatrix(), getX() + Float.intBitsToFloat(1077936128), getY() + i + Float.intBitsToFloat(1065353216), getPanel().getTotalWidth() - 4, getPanel().getTotalWidth() - 4, -1, HSBtoRGB, -16777216, -16777216);
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float f = getX() + Float.intBitsToFloat(1075838976);
            float f2 = ((getY() + getPanel().getTotalWidth()) + i) - Float.intBitsToFloat(1056964608);
            float f3 = getPanel().getTotalWidth() - 3;
            float intBitsToFloat4 = Float.intBitsToFloat(1088421888);
            int[] iArr = new int[2];
            iArr[0] = this.flag4 ? -1 : getAccentColor().hashCode();
            iArr[1] = this.flag4 ? -1 : getAccentColor().hashCode();
            Shape2DRenderer.drawMultiGradientRect(positionMatrix, f, f2, f3, intBitsToFloat4, iArr);
            Shape2DRenderer.drawMultiGradientRect(matrixStack.peek().getPositionMatrix(), getX() + Float.intBitsToFloat(1077936128), getY() + getPanel().getTotalWidth() + i, getPanel().getTotalWidth() - 4, Float.intBitsToFloat(1086324736), -65536, -256, -16711936, -16711681, -16776961, -65281, -65536);
            applyAnimatedValue(this.hUDHelper_23, this.floatArr[0] * (getPanel().getTotalWidth() - 2));
            float clamp = MathHelper.clamp(this.hUDHelper_23.getValue(), Float.intBitsToFloat(1077936128), getPanel().getTotalWidth() - 3);
            Shape2DRenderer.drawRect(matrixStack, ((int) (getX() + clamp)) - Float.intBitsToFloat(1056964608), ((int) ((getY() + getPanel().getTotalWidth()) + i)) - Float.intBitsToFloat(1056964608), ((int) (getX() + clamp + Float.intBitsToFloat(1073741824))) + Float.intBitsToFloat(1056964608), (int) (getY() + getPanel().getTotalWidth() + i + Float.intBitsToFloat(1087373312)), this.flag3 ? -1 : getAccentColor().hashCode());
            Shape2DRenderer.drawRect(matrixStack, (int) (getX() + clamp), (int) (getY() + getPanel().getTotalWidth() + i), (int) (getX() + clamp + Float.intBitsToFloat(1073741824)), (int) (getY() + getPanel().getTotalWidth() + i + Float.intBitsToFloat(1087373312)), -1);
            if (!this.setting.flag3) {
                Matrix4f positionMatrix2 = matrixStack.peek().getPositionMatrix();
                float f4 = getX() + Float.intBitsToFloat(1075838976);
                float f5 = ((int) getY()) + getPanel().getTotalWidth() + i + Float.intBitsToFloat(1089470464);
                float f6 = getPanel().getTotalWidth() - 3;
                float intBitsToFloat5 = Float.intBitsToFloat(1088421888);
                int[] iArr2 = new int[2];
                iArr2[0] = this.flag5 ? -1 : getAccentColor().hashCode();
                iArr2[1] = this.flag5 ? -1 : getAccentColor().hashCode();
                Shape2DRenderer.drawMultiGradientRect(positionMatrix2, f4, f5, f6, intBitsToFloat5, iArr2);
                drawAlphaSlider(matrixStack, ((int) getX()) + 3, ((int) getY()) + getPanel().getTotalWidth() + i + 8, getPanel().getTotalWidth() - 4, 6, intBitsToFloat, intBitsToFloat2, intBitsToFloat3, alpha / Float.intBitsToFloat(1132396544));
            }
            applyAnimatedValue(this.hUDHelper_2, MathHelper.clamp(this.floatArr[1], 0.0f, Float.intBitsToFloat(1065353216)) * (getPanel().getTotalWidth() - 8));
            applyAnimatedValue(this.hUDHelper_22, (Float.intBitsToFloat(1065353216) - MathHelper.clamp(this.floatArr[2], 0.0f, Float.intBitsToFloat(1065353216))) * (getPanel().getTotalWidth() - 8));
            float f7 = getX() + Float.intBitsToFloat(1084227584) + this.hUDHelper_2.getValue();
            float f8 = getY() + i + Float.intBitsToFloat(1077936128) + this.hUDHelper_22.getValue();
            this.progress.animateToggle(this.flag3);
            float intBitsToFloat6 = Float.intBitsToFloat(1073741824) * Float.intBitsToFloat(1065353216) * this.progress.getValue();
            Color hSBColor = Color.getHSBColor(this.floatArr[0], this.floatArr[1], this.floatArr[2]);
            Shape2DRenderer.drawRect(matrixStack, (f7 - Float.intBitsToFloat(1069547520)) - intBitsToFloat6, (f8 - Float.intBitsToFloat(1069547520)) - intBitsToFloat6, f7 + Float.intBitsToFloat(1069547520) + intBitsToFloat6, f8 + Float.intBitsToFloat(1069547520) + intBitsToFloat6, this.flag3 ? Color.white : getAccentColor());
            Shape2DRenderer.drawRect(matrixStack, (f7 - Float.intBitsToFloat(1065353216)) - intBitsToFloat6, (f8 - Float.intBitsToFloat(1065353216)) - intBitsToFloat6, f7 + Float.intBitsToFloat(1065353216) + intBitsToFloat6, f8 + Float.intBitsToFloat(1065353216) + intBitsToFloat6, this.flag3 ? hSBColor : hSBColor.darker());
            this.list.forEach(presetHelper_5 -> {
                presetHelper_5.render(drawContext, matrixStack, d, d2);
            });
            this.setting.setValue(ColorUtils.withAlpha(new Color(Color.HSBtoRGB(this.floatArr[0], this.floatArr[1], this.floatArr[2])), MathHelper.clamp(alpha, 0, 255)));
            if (((ColorSetting) this.setting).isRainbow()) {
                this.floatArr[0] = Color.RGBtoHSB(((Color) this.setting.getValue()).getRed(), ((Color) this.setting.getValue()).getGreen(), ((Color) this.setting.getValue()).getBlue(), (float[]) null)[0];
            }
        }
        int i2 = (int) (this.num + getRowHeight() + getPanel().getTotalWidth() + 6 + getRowSpacing());
        float intBitsToFloat7 = Float.intBitsToFloat(1065353216);
        for (GuiComponent presetHelper_52 : this.list) {
            presetHelper_52.update(d, d2);
            presetHelper_52.setOffsetY(i2);
            if (!(presetHelper_52 instanceof PanelTextButton)) {
                i2 += (int) (presetHelper_52.getFontHeight() + intBitsToFloat7);
                presetHelper_52.setOffsetY(i2);
                intBitsToFloat7 = Float.intBitsToFloat(1056964608);
            }
        }
    }

    public void drawAlphaSlider(MatrixStack matrixStack, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
        boolean z = true;
        int i5 = i4 / 2;
        int i6 = -i5;
        while (true) {
            int i7 = i6;
            if (i7 >= i3) {
                break;
            }
            if (!z) {
                Shape2DRenderer.drawRect(matrixStack, i + i7, i2, i + i7 + i5, i2 + i4, -1);
                Shape2DRenderer.drawRect(matrixStack, i + i7, i2 + i5, i + i7 + i5, i2 + i4, -7303024);
                if (i7 < i3 - i5) {
                    int i8 = i + i7 + i5;
                    int min = Math.min(i + i3, i + i7 + (i5 * 2));
                    Shape2DRenderer.drawRect(matrixStack, i8, i2, min, i2 + i4, -7303024);
                    Shape2DRenderer.drawRect(matrixStack, i8, i2 + i5, min, i2 + i4, -1);
                }
            }
            z = !z;
            i6 = i7 + i5;
        }
        Shape2DRenderer.drawMultiGradientRect(matrixStack.peek().getPositionMatrix(), i, i2, i3, i4, new Color(f, f2, f3, Float.intBitsToFloat(1065353216)).hashCode(), 0);
        applyAnimatedValue(this.hUDHelper_24, i3 - ((i3 - 2) * f4));
        float f5 = (i + this.hUDHelper_24.getValue()) - Float.intBitsToFloat(1065353216);
        Shape2DRenderer.drawRect(matrixStack, f5 - Float.intBitsToFloat(1069547520), i2 - Float.intBitsToFloat(1056964608), f5 + Float.intBitsToFloat(1069547520), i2 + i4 + Float.intBitsToFloat(1056964608), this.flag5 ? Color.white : getAccentColor());
        Shape2DRenderer.drawRect(matrixStack, f5 - Float.intBitsToFloat(1065353216), i2, f5 + Float.intBitsToFloat(1065353216), i2 + i4, -1);
    }

    @Override // me.mioclient.setting.SettingPresetSettingButton, me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void mouseClicked(double d, double d2, int i) {
        if (isHidden()) {
            return;
        }
        int i2 = getRowHeight();
        if (isMouseOverRow(d, d2) && i == 1) {
            this.flag = !this.flag;
        }
        if (this.flag) {
            this.list.forEach(presetHelper_5 -> {
                presetHelper_5.mouseClicked(d, d2, i);
            });
        }
        if (this.flag && i == 0) {
            if (d > getX() + Float.intBitsToFloat(1073741824) && d < (getX() + getPanel().getTotalWidth()) - Float.intBitsToFloat(1073741824) && d2 > getY() + i2 && d2 < ((getY() + i2) + getPanel().getTotalWidth()) - Float.intBitsToFloat(1073741824)) {
                this.flag3 = true;
            }
            if (d > getX() + Float.intBitsToFloat(1073741824) && d < (getX() + getPanel().getTotalWidth()) - Float.intBitsToFloat(1073741824) && d2 > getY() + i2 + getPanel().getTotalWidth() && d2 < getY() + i2 + getPanel().getTotalWidth() + Float.intBitsToFloat(1086324736)) {
                this.flag4 = true;
            }
            if (d > getX() + Float.intBitsToFloat(1073741824) && d < (getX() + getPanel().getTotalWidth()) - Float.intBitsToFloat(1073741824) && d2 > getY() + i2 + getPanel().getTotalWidth() + Float.intBitsToFloat(1090519040) && d2 < getY() + i2 + getPanel().getTotalWidth() + Float.intBitsToFloat(1096810496) && !this.setting.flag3) {
                this.flag5 = true;
            }
            super.mouseClicked(d, d2, i);
        }
    }

    @Override // me.mioclient.gui.GuiComponent
    public void mouseReleased(double d, double d2, int i) {
        if (isHidden()) {
            return;
        }
        this.list.forEach(presetHelper_5 -> {
            presetHelper_5.mouseReleased(d, d2, i);
        });
        super.mouseReleased(d, d2, i);
        this.flag5 = false;
        this.flag4 = false;
        this.flag3 = false;
    }

    @Override // me.mioclient.gui.GuiComponent
    public void keyTyped(char c) {
        if (isHidden()) {
            return;
        }
        this.list.forEach(presetHelper_5 -> {
            presetHelper_5.keyTyped(c);
        });
        super.keyTyped(c);
    }

    @Override // me.mioclient.gui.GuiComponent
    public void keyPressed(int i) {
        if (isHidden()) {
            return;
        }
        this.list.forEach(presetHelper_5 -> {
            presetHelper_5.keyPressed(i);
        });
        super.keyPressed(i);
    }

    @Override // me.mioclient.setting.SettingPresetSettingButton, me.mioclient.gui.GuiComponent
    public int getFontHeight() {
        if (!this.flag || isHidden()) {
            return super.getFontHeight();
        }
        int i = (int) (getPanel().getTotalWidth() + Float.intBitsToFloat(1088421888) + (getRowHeight() * 2) + getRowSpacing());
        if (this.list.size() > 2) {
            i = (int) (i + (getRowHeight() * (this.list.size() - 2)) + Float.intBitsToFloat(1056964608));
        }
        return i;
    }

    @Override // me.mioclient.gui.GuiComponent
    public void init() {
        syncHsbFromColor((Color) this.setting.getValue());
        this.list.forEach((v0) -> {
            v0.init();
        });
    }

    @Override // me.mioclient.gui.ClosableElement
    public boolean isClosed() {
        return super.presetHelper_2.isClosed() || !this.flag;
    }

    public boolean isMouseOverRow(double d, double d2) {
        return d > ((double) this.presetEnumSettingHelper.getX()) && d < ((double) (this.presetEnumSettingHelper.getX() + this.presetEnumSettingHelper.getTotalWidth())) && d2 > ((double) (this.presetEnumSettingHelper.getY() + this.num)) && d2 < ((double) ((this.presetEnumSettingHelper.getY() + getRowHeight()) + this.num));
    }

    public float getX() {
        return this.presetEnumSettingHelper.getX();
    }

    public float getY() {
        return this.presetEnumSettingHelper.getY() + this.num;
    }

    public Color getAccentColor() {
        return ColorUtils.withAlphaPercent(getUI().color.getValue(), Float.intBitsToFloat(1065353216));
    }

    public float getRowSpacing() {
        return this.setting.flag3 ? Float.intBitsToFloat(1073741824) : Float.intBitsToFloat(1092616192);
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public boolean containsPoint(double d, double d2) {
        return super.containsPoint(d, d2) || this.flag4 || this.flag3 || this.flag5;
    }

    public void syncHsbFromColor(Color color) {
        float[] RGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null);
        this.floatArr[0] = RGBtoHSB[0];
        this.floatArr[1] = RGBtoHSB[1];
        this.floatArr[2] = RGBtoHSB[2];
    }

    public Color getColor() {
        return ColorUtils.withAlpha(Color.getHSBColor(this.floatArr[0], this.floatArr[1], this.floatArr[2]), ((Color) this.setting.getValue()).getAlpha());
    }

    public void applyAnimatedValue(InterpolatedFloat hUDHelper_2, float f) {
        if (this.flag2) {
            hUDHelper_2.setImmediate(f);
        } else {
            hUDHelper_2.animate(f);
        }
    }
}
