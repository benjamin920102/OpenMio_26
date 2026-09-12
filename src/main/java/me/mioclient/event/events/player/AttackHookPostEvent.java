package me.mioclient.event.events.player;

import me.mioclient.mode.combat.KeyPearlMode;

import me.mioclient.event.Event;

import me.mioclient.mode.combat.KeyPearlMode;
import net.minecraft.entity.Entity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/AttackHookPostEvent.class */
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
