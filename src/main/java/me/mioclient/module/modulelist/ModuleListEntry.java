package me.mioclient.module.modulelist;

import me.mioclient.module.HudModule;

import me.mioclient.render.hud.HudTextLine;

import me.mioclient.module.Module;
import net.minecraft.network.chat.Component;

public class ModuleListEntry extends HudTextLine {
    public final Module module;

    public ModuleListEntry(me.mioclient.module.ModuleList moduleList, Module module) {
        super(() -> {
            return Component.literal(module.getInfoString());
        }, () -> {
            return Boolean.valueOf(module.isToggled() && module.isDrawn() && !(module.getKeybind().isUnbound() && moduleList.setting.getValue().booleanValue()));
        });
        this.module = module;
    }

    public Module getModule() {
        return this.module;
    }
}