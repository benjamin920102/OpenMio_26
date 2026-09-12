package me.mioclient.gui.screen;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.panel.CategoryPanel;
import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.widget.ModuleButton;
import me.mioclient.mode.client.CursorType;
import me.mioclient.mode.client.Season;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shader.BlurFramebuffer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.render.shape.SnowflakeRenderer;
import me.mioclient.util.anim.InterpolatedFloat;
import me.mioclient.util.client.GuiScaleOverride;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.RenderUtil;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.client.GuiScaleOverride;
import me.mioclient.util.anim.InterpolatedFloat;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.mioclient.module.client.UI;
import me.mioclient.module.render.Blur;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import me.mioclient.render.shader.BlurFramebuffer;
import me.mioclient.mode.client.CursorType;
import me.mioclient.mode.client.Season;
import me.mioclient.render.shape.SnowflakeRenderer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AbstractClickGuiScreen.class */
public class AbstractClickGuiScreen extends Screen implements ClientAccessor {
    public final ArrayList<ClickGuiPanel> arrayList;
    public final List<SnowflakeRenderer> list;
    public final InterpolatedFloat hUDHelper_2;
    public final InterpolatedFloat hUDHelper_22;
    public boolean flag;
    public float val;
    public boolean flag2;
    public String string;
    public long num2;
    public long num3;
    public Screen screen;
    public boolean flag3;
    public static final int num = 25;
    public static Blur blur = (Blur) ClientServices.moduleInjector.getModule(Blur.class);
    public static CursorType cursorType = CursorType.STANDARD;

    public AbstractClickGuiScreen() {
        super(Text.literal("mio"));
        this.arrayList = new ArrayList<>();
        this.list = new ArrayList();
        this.hUDHelper_2 = new InterpolatedFloat(Float.intBitsToFloat(1075838976), true);
        this.hUDHelper_22 = new InterpolatedFloat(Float.intBitsToFloat(1075838976), true);
        this.flag = false;
        this.string = "";
        this.num2 = 0L;
        resetSnowflakes();
    }

    public void init() {
        int scaledWidth = minecraftClient.getWindow().getScaledWidth();
        int scaledHeight = minecraftClient.getWindow().getScaledHeight();
        if (getParentScreen() != null) {
            getParentScreen().init(minecraftClient, scaledWidth, scaledHeight);
        }
        if (!isNotInGame()) {
            this.hUDHelper_2.setImmediate(scaledWidth / Float.intBitsToFloat(1073741824));
            this.hUDHelper_22.setImmediate(scaledHeight / Float.intBitsToFloat(1073741824));
        }
        this.flag = false;
        getGuiPanels().forEach(presetEnumSettingHelper -> {
            presetEnumSettingHelper.setDragging(false);
        });
        this.val = UI.uI.guiScale.getValue().floatValue();
        this.flag2 = false;
        this.num3 = System.currentTimeMillis();
        Iterator<ClickGuiPanel> it = this.arrayList.iterator();
        while (it.hasNext()) {
            it.next().init();
        }
    }

    public void render(DrawContext drawContext, int i, int i2, float f) {
        this.hUDHelper_2.animate(i);
        this.hUDHelper_22.animate(i2);
        float f2 = this.hUDHelper_2.getValue();
        float f3 = this.hUDHelper_22.getValue();
        if ((getParentScreen() instanceof TitleScreen) || (getParentScreen() instanceof MultiplayerScreen)) {
            GlStateManager._enablePolygonOffset();
            GlStateManager._polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1148846080));
            getParentScreen().render(drawContext, -99, -99, f);
            minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();
            drawContext.draw();
            GlStateManager._polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-998637568));
            GlStateManager._disablePolygonOffset();
        }
        cursorType.applyCursor();
        cursorType = CursorType.STANDARD;
        this.list.removeIf((v0) -> {
            return v0.hasLanded();
        });
        if (this.list.size() < 25) {
            this.list.add(new SnowflakeRenderer());
        }
        RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), getAlpha());
        float f4 = (float) GuiScaleOverride.getScaleMultiplier(this.val);
        GuiScaleOverride.applyScale(this.val);
        drawContext.getMatrices().scale(f4, f4, Float.intBitsToFloat(1065353216));
        int i3 = (int) (i / f4);
        int i4 = (int) (i2 / f4);
        float f5 = f2 / f4;
        float f6 = f3 / f4;
        this.string = "";
        if (!isClosing() && blur.isToggled()) {
            BlurFramebuffer.renderWithBlur(() -> {
                Iterator<ClickGuiPanel> it = getGuiPanels().iterator();
                while (it.hasNext()) {
                    ClickGuiPanel next = it.next();
                    float f7 = next.getAnimationProgress();
                    int i5 = next.getFontHeight();
                    Objects.requireNonNull(next);
                    int x = next.getX();
                    int y = next.getY();
                    int x2 = next.getX() + next.getTotalWidth();
                    int y2 = next.getY();
                    Objects.requireNonNull(next);
                    drawContext.fill(x, y, x2, (int) (y2 + 14 + ((i5 - 14) * f7)), -1);
                }
            }, blur.getBlurRadius());
        }
        renderOverlay(drawContext);
        if (Season.WINTER.containsMonth(LocalDate.now().getMonthValue())) {
            Iterator<SnowflakeRenderer> it = this.list.iterator();
            while (it.hasNext()) {
                it.next().renderContent(drawContext);
            }
        }
        Iterator<ClickGuiPanel> it2 = this.arrayList.iterator();
        while (it2.hasNext()) {
            ClickGuiPanel next = it2.next();
            next.handleDrag(i3, i4);
            next.render(drawContext, drawContext.getMatrices(), i3, i4);
        }
        if (!this.string.isBlank()) {
            Shape2DRenderer.resetShadowOffset();
            String[] split = this.string.split("\n");
            float f7 = 0.0f;
            for (String str : split) {
                float f8 = ClientTextRenderer.INSTANCE.getStringWidth(str);
                if (f8 > f7) {
                    f7 = f8;
                }
            }
            float intBitsToFloat = f5 + Float.intBitsToFloat(1091567616);
            float intBitsToFloat2 = f5 + f7 + Float.intBitsToFloat(1092616192);
            if (intBitsToFloat2 > minecraftClient.getWindow().getScaledWidth()) {
                intBitsToFloat = (f5 - f7) - Float.intBitsToFloat(1073741824);
                intBitsToFloat2 = f5 - Float.intBitsToFloat(1065353216);
            }
            float f9 = intBitsToFloat;
            float f10 = intBitsToFloat2;
            int i5 = (int) f6;
            float intBitsToFloat3 = (f6 - Float.intBitsToFloat(1065353216)) + ((ClientTextRenderer.INSTANCE.getFontHeight() + Float.intBitsToFloat(1065353216)) * split.length);
            BlurFramebuffer.renderWithBlur(() -> {
                RenderUtil.INSTANCE.drawFilledRect(drawContext.getMatrices(), f9, i5 - Float.intBitsToFloat(1065353216), f10, intBitsToFloat3, ColorUtils.argbToInt(10, 10, 10, 140));
            }, Float.intBitsToFloat(1086324736));
            RenderUtil.INSTANCE.drawFilledRectColor(drawContext.getMatrices(), intBitsToFloat, f6 - Float.intBitsToFloat(1065353216), intBitsToFloat2, intBitsToFloat3, new Color(10, 10, 10, 80));
            int i6 = 0;
            for (String str2 : split) {
                ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str2, intBitsToFloat + Float.intBitsToFloat(1065353216), f6 + ((ClientTextRenderer.INSTANCE.getFontHeight() + Float.intBitsToFloat(1065353216)) * i6), Color.white);
                i6++;
            }
            this.num2 = System.currentTimeMillis();
        } else if (!UI.uI.constantReset.getValue().booleanValue() && this.num2 != 0 && System.currentTimeMillis() > this.num2 + 750) {
            this.num2 = 0L;
            ModuleButton.resetTooltipState();
        }
        renderCustomContent(drawContext, i3, i4, f);
        Shape2DRenderer.resetShadowOffset();
        RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
        GuiScaleOverride.do604();
    }

    public boolean mouseClicked(double d, double d2, int i) {
        if (this.flag2) {
            return false;
        }
        double d3 = d / GuiScaleOverride.getScaleMultiplier(this.val);
        double d4 = d2 / GuiScaleOverride.getScaleMultiplier(this.val);
        boolean mouseClicked = super.mouseClicked(d3, d4, i);
        ClickGuiPanel presetEnumSettingHelper = null;
        for (int size = this.arrayList.size() - 1; size >= 0; size--) {
            ClickGuiPanel presetEnumSettingHelper2 = this.arrayList.get(size);
            if (presetEnumSettingHelper2.isTitleBarClicked(d3, d4) && i == 0 && presetEnumSettingHelper == null) {
                presetEnumSettingHelper = presetEnumSettingHelper2;
                presetEnumSettingHelper2.setDragging(true);
                presetEnumSettingHelper2.dragOffsetY = (int) (d3 - presetEnumSettingHelper2.getX());
                presetEnumSettingHelper2.dragOffsetX = (int) (d4 - presetEnumSettingHelper2.getY());
            }
            presetEnumSettingHelper2.mouseClicked(d3, d4, i);
        }
        if (presetEnumSettingHelper != null) {
            this.arrayList.remove(presetEnumSettingHelper);
            this.arrayList.add(presetEnumSettingHelper);
        }
        reset();
        handleMouseClicked(d3, d4, i);
        return mouseClicked;
    }

    public boolean mouseReleased(double d, double d2, int i) {
        double d3 = d / GuiScaleOverride.getScaleMultiplier(this.val);
        double d4 = d2 / GuiScaleOverride.getScaleMultiplier(this.val);
        for (int size = this.arrayList.size() - 1; size >= 0; size--) {
            ClickGuiPanel presetEnumSettingHelper = this.arrayList.get(size);
            presetEnumSettingHelper.setDragging(false);
            presetEnumSettingHelper.mouseReleased(d3, d4, i);
        }
        handleMouseReleased(d3, d4, i);
        return super.mouseReleased(d3, d4, i);
    }

    public boolean mouseScrolled(double d, double d2, double d3, double d4) {
        double d5 = GuiScaleOverride.getScaleMultiplier(this.val);
        double d6 = d / d5;
        double d7 = d2 / d5;
        int i = 2 + ClientTextRenderer.INSTANCE.getFontHeight() + UI.uI.buttonHeight.getValue().intValue();
        int orElse = this.arrayList.stream().mapToInt((v0) -> {
            return v0.getFontHeight();
        }).max().orElse(i);
        int scaledHeight = ((double) orElse) > ((double) (minecraftClient.getWindow().getScaledHeight() + 5)) / d5 ? orElse - ((int) ((minecraftClient.getWindow().getScaledHeight() - 5) / d5)) : (int) (Double.longBitsToDouble(-4606056518893174784L) / d5);
        if (d4 != 0.0d) {
            int i2 = (int) (d4 * i);
            Iterator<ClickGuiPanel> it = this.arrayList.iterator();
            while (it.hasNext()) {
                ClickGuiPanel next = it.next();
                if (!next.isDragging()) {
                    next.mouseScrolled(d6, d7, d4);
                    int y = next.getY() + i2;
                    if (shouldRenderBackground()) {
                        y = MathHelper.clamp(y, -scaledHeight, 5);
                    }
                    next.setY(y);
                }
            }
        }
        handleMouseScrolled(d6, d7, d4);
        return super.mouseScrolled(d6, d7, d3, d4);
    }

    public boolean charTyped(char c, int i) {
        for (int size = this.arrayList.size() - 1; size >= 0; size--) {
            this.arrayList.get(size).keyTyped(c);
        }
        return super.charTyped(c, i);
    }

    public void tick() {
        if (!this.flag2 || System.currentTimeMillis() < this.num3 + 150) {
            return;
        }
        Iterator<ClickGuiPanel> it = this.arrayList.iterator();
        while (it.hasNext()) {
            it.next().setDragging(false);
        }
        if (this.screen != null) {
            minecraftClient.setScreen(this.screen);
        } else {
            close();
        }
        setParentScreen(null);
    }

    public boolean keyPressed(int i, int i2, int i3) {
        this.flag3 = true;
        for (int size = this.arrayList.size() - 1; size >= 0; size--) {
            this.arrayList.get(size).keyPressed(i);
        }
        if (i != 256 || !this.flag3) {
            return false;
        }
        requestClose();
        return false;
    }

    public boolean shouldPause() {
        return false;
    }

    public void renderCustomContent(DrawContext drawContext, int i, int i2, float f) {
    }

    public void handleMouseClicked(double d, double d2, int i) {
    }

    public void handleMouseReleased(double d, double d2, int i) {
    }

    public void handleMouseScrolled(double d, double d2, double d3) {
    }

    public void close() {
        super.close();
        CursorType.STANDARD.applyCursor();
    }

    public void reset() {
        ModuleButton.resetTooltipState();
        this.num2 = 0L;
    }

    public void resetSnowflakes() {
        this.list.clear();
        for (int i = 0; i < 25; i++) {
            this.list.add(new SnowflakeRenderer());
        }
    }

    public float getAlpha() {
        float currentTimeMillis = ((float) (System.currentTimeMillis() - this.num3)) / Float.intBitsToFloat(1125515264);
        if (this.flag2) {
            currentTimeMillis = Float.intBitsToFloat(1065353216) - currentTimeMillis;
        }
        return MathHelper.clamp(currentTimeMillis, 0.0f, Float.intBitsToFloat(1065353216));
    }

    public void resetPanelLayout() {
        resetSnowflakes();
        this.arrayList.sort(Comparator.comparing(presetEnumSettingHelper -> {
            if (presetEnumSettingHelper instanceof CategoryPanel) {
                return Integer.valueOf(((CategoryPanel) presetEnumSettingHelper).category.ordinal());
            }
            return 100;
        }));
        int i = 10;
        Iterator<ClickGuiPanel> it = this.arrayList.iterator();
        while (it.hasNext()) {
            ClickGuiPanel next = it.next();
            next.setX(i);
            i += next.getTotalWidth() + 3;
        }
    }

    public void renderOverlay(DrawContext drawContext) {
        drawContext.fill(0, 0, minecraftClient.getWindow().getScaledWidth(), minecraftClient.getWindow().getScaledHeight(), UI.uI.tint.getValue().hashCode());
        drawContext.fillGradient(0, 0, minecraftClient.getWindow().getScaledWidth(), minecraftClient.getWindow().getScaledHeight(), ColorUtils.toIntWithAlpha(UI.uI.gradientColor.getValue(), 0), UI.uI.gradientColor.getValue().hashCode());
    }

    public void requestClose() {
        if (this.flag2) {
            close();
        } else {
            this.flag = false;
            this.num3 = System.currentTimeMillis();
            if (UI.uI.isToggled()) {
                UI.uI.toggle();
            }
        }
        this.flag2 = true;
    }

    public void markTooltipActive() {
        this.num2 = System.currentTimeMillis();
    }

    public void setTooltipText(String str) {
        this.string = str;
    }

    public ArrayList<ClickGuiPanel> getGuiPanels() {
        return this.arrayList;
    }

    public boolean isSearchActive() {
        return this.flag;
    }

    public void setSearchActive(boolean z) {
        this.flag = z;
    }

    public boolean isClosing() {
        return this.flag2;
    }

    public void openWithParent() {
        Screen screen = minecraftClient.currentScreen;
        if (screen instanceof AbstractClickGuiScreen) {
            setParentScreen(((AbstractClickGuiScreen) screen).screen);
        } else {
            setParentScreen(minecraftClient.currentScreen);
        }
        minecraftClient.setScreen(this);
    }

    public boolean shouldRenderBackground() {
        return true;
    }

    public void setParentScreen(Screen screen) {
        this.screen = screen;
    }

    public Screen getParentScreen() {
        return this.screen;
    }

    public void suppressEscapeClose() {
        this.flag3 = false;
    }
}
