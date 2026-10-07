package me.mioclient.gui.panel;

import me.mioclient.api.ClientServices;
import me.mioclient.gui.widget.ActionButton;
import me.mioclient.setting.StringSettingRow;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.screen.CategorySelectScreen;
import me.mioclient.gui.screen.PresetScreen;
import me.mioclient.manager.PresetEntry;
import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.mode.misc.PresetMode;

import me.mioclient.gui.ClosableElement;
import me.mioclient.gui.GuiComponent;
import me.mioclient.manager.PresetFileRegistry;

import com.google.gson.JsonElement;
import java.io.IOException;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.PresetPresetSettingButton;
import me.mioclient.setting.PresetSettingPresetSettingButton;
import me.mioclient.setting.StringSetting;
import me.mioclient.api.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.vertex.PoseStack;

public class PresetPanel extends ClickGuiPanel {
    public Setting<String> setting;
    public Setting<PresetHelperMode> setting2;
    public final PresetScreen presetFontsLlamaRenderHelper;
    public PresetMode presetMode;
    public PresetEntry presetSearchHelper4;
    public boolean flag;

    public PresetPanel(PresetScreen presetFontsLlamaRenderHelper) {
        super("Preset");
        this.setting = new StringSetting("Save", "");
        this.setting2 = new EnumSetting("Category", PresetHelperMode.MODULES);
        this.presetMode = PresetMode.NEW;
        this.presetFontsLlamaRenderHelper = presetFontsLlamaRenderHelper;
        this.setting.setChangeCallback(() -> {
            try {
                getPresetRegistry().savePreset(this.setting.getValue());
                presetFontsLlamaRenderHelper.getPresetList().do34();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.setting.setValue("");
        });
        this.setting2.setChangeCallback(() -> {
            presetFontsLlamaRenderHelper.setPresetCategory(this.setting2.getValue());
            presetFontsLlamaRenderHelper.getPresetList().do34();
        });
    }

    @Override // me.mioclient.gui.panel.ClickGuiPanel, me.mioclient.gui.GuiComponent
    public void init() {
        do34();
    }

    @Override // me.mioclient.gui.panel.ClickGuiPanel, me.mioclient.gui.GuiComponent
    public void render(GuiGraphicsExtractor drawContext, PoseStack matrixStack, double d, double d2) {
        if (this.flag) {
            do34();
            this.flag = false;
        }
        super.render(drawContext, matrixStack, d, d2);
    }

    public void do34() {
        this.registry.clear();
        switch (this.presetMode) {
            case NEW:
                addComponent(new PresetSettingPresetSettingButton(this, ClosableElement.presetHelper_2, this.setting2));
                addComponent(new StringSettingRow(this, ClosableElement.presetHelper_2, this.setting));
                addComponent(new ActionButton(this, "Refresh", () -> {
                    getPresetRegistry().do34();
                    this.presetFontsLlamaRenderHelper.getPresetList().do34();
                }));
                addComponent(new ActionButton(this, "Restore", () -> {
                    JsonElement jsonElement1434 = ClientServices.presetHelper.getSnapshot().consumeJsonElement();
                    if (jsonElement1434 != null) {
                        PresetHelperMode.ALL.fromJson(jsonElement1434);
                    }
                }));
                break;
            case EDIT:
                addComponent(new PresetPresetSettingButton(this, new StringTemplate().arg(this.presetSearchHelper4.getName()).format("Preset \u0001")));
                addComponent(new ActionButton(this, "Load", () -> {
                    ClientServices.presetHelper.getSnapshot().do41();
                    getPresetRegistry().loadPreset(this.presetSearchHelper4.getName());
                }));
                addComponent(new ActionButton(this, "Save", () -> {
                    try {
                        getPresetRegistry().savePreset(this.presetSearchHelper4.getName());
                        this.presetFontsLlamaRenderHelper.getPresetList().do34();
                    } catch (Exception e) {
                    }
                }));
                addComponent(new ActionButton(this, "Delete", () -> {
                    getPresetRegistry().deletePreset(this.presetSearchHelper4.getName());
                    switchMode(PresetMode.NEW);
                }));
                addComponent(new ActionButton(this, "Back", () -> {
                    switchMode(PresetMode.NEW);
                }));
                break;
        }
        this.presetFontsLlamaRenderHelper.getPresetList().do34();
    }

    public void addComponent(GuiComponent presetHelper_5) {
        this.registry.add(presetHelper_5);
    }

    public PresetFileRegistry getPresetRegistry() {
        return ClientServices.presetHelper.getPresetRegistry(this.presetFontsLlamaRenderHelper.getPresetCategory());
    }

    public void do1032(PresetEntry presetSearchHelper4) {
        this.presetSearchHelper4 = presetSearchHelper4;
        switchMode(PresetMode.EDIT);
    }

    public PresetEntry getPresetSearchHelper41033() {
        if (this.presetMode != PresetMode.EDIT) {
            return null;
        }
        return this.presetSearchHelper4;
    }

    public void switchMode(PresetMode presetMode) {
        this.presetMode = presetMode;
        this.flag = true;
    }
}
