package me.mioclient.event.events.player;

import me.mioclient.event.Event;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/GetStatusEffectEvent.class */
public class GetStatusEffectEvent extends Event {
    public final RegistryEntry<StatusEffect> registryEntry;

    public GetStatusEffectEvent(RegistryEntry<StatusEffect> registryEntry) {
        this.registryEntry = registryEntry;
    }

    public RegistryEntry<StatusEffect> getEffect() {
        return this.registryEntry;
    }
}
