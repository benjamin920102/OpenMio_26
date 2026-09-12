package me.mioclient.gui.screen;

import me.mioclient.gui.panel.PresetListPanel;
import me.mioclient.gui.panel.PresetPanel;
import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.util.anim.ValueAnimator;

import me.mioclient.util.anim.ValueAnimator;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.file.Path;
import me.mioclient.gui.panel.PresetListPanel;
import me.mioclient.gui.panel.PresetPanel;
import net.minecraft.client.gui.DrawContext;
import me.mioclient.mode.misc.PresetHelperMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PresetScreen.class */
public class PresetScreen extends AbstractClickGuiScreen {
    public final PresetListPanel list = new PresetListPanel(this);
    public final PresetPanel preset = new PresetPanel(this);
    public PresetHelperMode presetHelperMode = PresetHelperMode.MODULES;
    public final ValueAnimator zoomHelper = new ValueAnimator();

    public PresetScreen() {
        this.arrayList.add(this.list);
        this.preset.setX(this.list.getX() + this.list.getTotalWidth() + 3);
        this.arrayList.add(this.preset);
    }

    @Override // me.mioclient.gui.screen.AbstractClickGuiScreen
    public void renderCustomContent(DrawContext drawContext, int i, int i2, float f) {
        this.zoomHelper.toggle(!minecraftClient.isWindowFocused(), 250L);
        float f2 = this.zoomHelper.getValue();
        if (f2 == 0.0f) {
            return;
        }
        RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1061158912) * f2);
        drawContext.drawCenteredTextWithShadow(minecraftClient.textRenderer, "Drop preset files here", drawContext.getScaledWindowWidth() / 2, drawContext.getScaledWindowHeight() / 2, -1);
        RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
    }

    public void filesDragged(java.util.List<Path> list) {
        CategorySelectScreen.tryOpenForDroppedFiles(this, list);
    }

    public PresetListPanel getPresetList() {
        return this.list;
    }

    public PresetPanel getPresetPanel() {
        return this.preset;
    }

    public PresetHelperMode getPresetCategory() {
        return this.presetHelperMode;
    }

    public void setPresetCategory(PresetHelperMode presetHelperMode) {
        this.presetHelperMode = presetHelperMode;
    }
}
