package me.mioclient.event.events.player;

import me.mioclient.event.Event;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

/* 26.2 port: Yarn MobEffect/Holder -> MojMap MobEffect/Holder. */
public class GetStatusEffectEvent extends Event {
    public final Holder<MobEffect> registryEntry;

    public GetStatusEffectEvent(Holder<MobEffect> registryEntry) {
        this.registryEntry = registryEntry;
    }

    public Holder<MobEffect> getEffect() {
        return this.registryEntry;
    }
}
