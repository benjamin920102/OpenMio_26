package me.mioclient.gui.screen;

import me.mioclient.gui.panel.PresetListPanel;
import me.mioclient.gui.panel.PresetPanel;
import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.util.anim.ValueAnimator;

import java.nio.file.Path;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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
    public void renderCustomContent(GuiGraphicsExtractor drawContext, int i, int i2, float f) {
        this.zoomHelper.toggle(!minecraftClient.getWindow().isFocused(), 250L);
        float f2 = this.zoomHelper.getValue();
        if (f2 == 0.0f) {
            return;
        }
        drawContext.centeredText(minecraftClient.font, "Drop preset files here", drawContext.guiWidth() / 2, drawContext.guiHeight() / 2, -1);
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
