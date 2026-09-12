package me.mioclient.event.events.render;

import me.mioclient.event.Event;

import net.minecraft.client.particle.Particle;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/AddParticleEvent.class */
public class AddParticleEvent extends Event {
    public Particle particle;

    public AddParticleEvent(Particle particle) {
        this.particle = particle;
    }

    public Particle getParticle() {
        return this.particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }
}
