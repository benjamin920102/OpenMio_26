package me.mioclient.module.effects;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.hud.HudTextLine;

import me.mioclient.api.ClientAccessor;
import me.mioclient.render.hud.HudTextLine;

import java.util.function.Supplier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/StatusEffectHudElement.class */
public class StatusEffectHudElement extends HudTextLine implements ClientAccessor {
    public final RegistryEntry<StatusEffect> registryEntry;

    public StatusEffectHudElement(RegistryEntry<StatusEffect> registryEntry, Supplier<Boolean> supplier) {
        super(() -> {
            StatusEffectInstance statusEffect = minecraftClient.player.getStatusEffect(registryEntry);
            if (statusEffect == null) {
                return Text.empty();
            }
            String string = StatusEffectUtil.getDurationText(statusEffect, Float.intBitsToFloat(1065353216), minecraftClient.world.getTickManager().getTickRate()).getString();
            if (string.startsWith("0")) {
                string = string.substring(1);
            }
            return Text.literal("%s %s%s%s".formatted(((StatusEffect) registryEntry.value()).getName().getString(), statusEffect.getAmplifier() == 0 ? "" : new StringTemplate().arg(statusEffect.getAmplifier() + 1).format("\u0001 "), Formatting.WHITE, string));
        }, supplier);
        this.registryEntry = registryEntry;
    }

    public RegistryEntry<StatusEffect> getEffect() {
        return this.registryEntry;
    }
}
