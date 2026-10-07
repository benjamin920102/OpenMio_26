package me.mioclient.module;

import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.module.effects.StatusEffectHudElement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;

public class Effects extends me.mioclient.module.HudModule {
    public Setting<Boolean> setting;
    public Setting<ScaffoldMode_2> setting2;
    public Setting<Set<MobEffect>> setting3;
    public final List<StatusEffectHudElement> list;

    
    class Inner extends TextHudElement {
        public Inner(me.mioclient.module.HudModule moduleList, List list) {
            super(moduleList, (List<? extends HudTextLine>) list);
        }

        @Override // me.mioclient.render.hud.TextHudElement
        public Color getLineColor(float f, HudTextLine cryptoHelper) {
            return Effects.this.setting.getValue().booleanValue() ? new Color(((MobEffect) ((StatusEffectHudElement) cryptoHelper).getEffect().value()).getColor(), false) : super.getLineColor(f, cryptoHelper);
        }
    }

    public Effects() {
        super("Effects", "potions");
        this.setting = add(new BooleanSetting("Vanilla", false));
        this.setting2 = add(new EnumSetting("Selection", ScaffoldMode_2.ANY));
        this.setting3 = add(new EntityListObjectSetting("WhiteList", BuiltInRegistries.MOB_EFFECT, new MobEffect[0]));
        this.list = new ArrayList();
        setElement(new Inner(this, this.list));
        getElement().setAnchor(ModuleListMode.BOTTOM_RIGHT);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.list.clear();
        BuiltInRegistries.MOB_EFFECT.listElements().forEach(reference -> {
            this.list.add(new StatusEffectHudElement(reference, () -> {
                boolean z;
                if (minecraftClient.player.hasEffect(reference)) {
                    if (this.setting2.getValue().isAllowedBySetting(reference.value(), this.setting3)) {
                        z = true;
                        return Boolean.valueOf(z);
                    }
                }
                z = false;
                return Boolean.valueOf(z);
            }));
        });
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.list.sort(Comparator.comparing(effectsSearchHelper4 -> {
            return effectsSearchHelper4.getEffect().value().getDisplayName().getString();
        }));
    }
}