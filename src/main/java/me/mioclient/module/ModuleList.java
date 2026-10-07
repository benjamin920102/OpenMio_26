package me.mioclient.module;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.module.modulelist.ModuleListEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.client.HUD;

public class ModuleList extends me.mioclient.module.HudModule {
    public Setting<Boolean> setting;
    public Setting<HUD.ModuleListMode> setting2;
    public final List<ModuleListEntry> list;

    public ModuleList() {
        super("ModuleList", "arraylist");
        this.setting = add(new BooleanSetting("OnlyBound", false));
        this.setting2 = add(new EnumSetting("Sort", HUD.ModuleListMode.LENGTH));
        this.list = new ArrayList();
        setElement(new TextHudElement(this, this.list));
        getElement().setAnchor(ModuleListMode.TOP_RIGHT);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.list.clear();
        Stream map = ClientServices.moduleRegistry.getRegistry().stream().map(module -> {
            return new ModuleListEntry(this, module);
        });
        List<ModuleListEntry> list = this.list;
        Objects.requireNonNull(list);
        map.forEach((v1) -> {
            list.add((ModuleListEntry) v1);
        });
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.setting2.getValue() == HUD.ModuleListMode.ALPHABET) {
            this.list.sort(Comparator.comparing(moduleListCryptoHelper -> {
                return moduleListCryptoHelper.getModule().getInfoString();
            }));
        } else {
            this.list.sort(Comparator.comparingDouble(moduleListCryptoHelper2 -> {
                return -ClientTextRenderer.INSTANCE.getStringWidth(moduleListCryptoHelper2.getModule().getInfoString());
            }));
        }
    }
}