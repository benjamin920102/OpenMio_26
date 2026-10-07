package me.mioclient.gui.panel;

import me.mioclient.api.ClientServices;
import me.mioclient.gui.widget.ActionButton;

import me.mioclient.gui.screen.PresetScreen;
import me.mioclient.manager.PresetEntry;

import me.mioclient.gui.GuiComponent;

import java.util.Iterator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.vertex.PoseStack;

public class PresetListPanel extends ClickGuiPanel {
    public final PresetScreen presetFontsLlamaRenderHelper;
    public boolean flag;

    
    
    public static final class Inner extends ActionButton {
        public final PresetScreen presetFontsLlamaRenderHelper;
        public final PresetEntry presetSearchHelper4;

        public Inner(PresetScreen presetFontsLlamaRenderHelper, ClickGuiPanel presetEnumSettingHelper, PresetEntry presetSearchHelper4) {
            super(presetEnumSettingHelper, presetSearchHelper4.getName(), () -> {
                presetFontsLlamaRenderHelper.getPresetPanel().do1032(presetSearchHelper4);
                presetFontsLlamaRenderHelper.getPresetPanel().recalculateLayout();
            });
            this.presetSearchHelper4 = presetSearchHelper4;
            this.presetFontsLlamaRenderHelper = presetFontsLlamaRenderHelper;
        }

        @Override // me.mioclient.gui.widget.ActionButton
        public boolean isHighlighted() {
            return this.presetSearchHelper4.equals(this.presetFontsLlamaRenderHelper.getPresetPanel().getPresetSearchHelper41033());
        }
    }

    public PresetListPanel(PresetScreen presetFontsLlamaRenderHelper) {
        super("List");
        this.presetFontsLlamaRenderHelper = presetFontsLlamaRenderHelper;
    }

    @Override // me.mioclient.gui.panel.ClickGuiPanel, me.mioclient.gui.GuiComponent
    public void render(GuiGraphicsExtractor drawContext, PoseStack matrixStack, double d, double d2) {
        if (this.flag) {
            Iterator<GuiComponent> it = this.registry.iterator();
            while (it.hasNext()) {
                it.next().update(d, d2);
            }
            recalculateLayout();
            this.flag = false;
        }
        super.render(drawContext, matrixStack, d, d2);
    }

    public void do34() {
        this.flag = true;
        this.registry.clear();
        Iterator it = ClientServices.presetHelper.getPresetRegistry(this.presetFontsLlamaRenderHelper.getPresetCategory()).getRegistry().iterator();
        while (it.hasNext()) {
            this.registry.add(new Inner(this.presetFontsLlamaRenderHelper, this, (PresetEntry) it.next()));
        }
    }
}
