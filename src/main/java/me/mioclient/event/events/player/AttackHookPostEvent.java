package me.mioclient.event.events.player;

import me.mioclient.mode.combat.KeyPearlMode;

import me.mioclient.event.Event;

import net.minecraft.world.entity.Entity;

public class AttackHookPostEvent extends Event {
    public final KeyPearlMode keyPearlMode;
    public Entity entity;

    public AttackHookPostEvent(KeyPearlMode keyPearlMode, Entity entity) {
        this.keyPearlMode = keyPearlMode;
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public KeyPearlMode getPhase() {
        return this.keyPearlMode;
    }
}
