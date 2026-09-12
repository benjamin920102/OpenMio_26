package me.mioclient.event.events.sound;

import me.mioclient.event.Event;

import net.minecraft.client.sound.SoundInstance;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/PlayEvent.class */
public final class PlayEvent extends Event {
    public final SoundInstance soundInstance;

    public PlayEvent(SoundInstance soundInstance) {
        this.soundInstance = soundInstance;
    }

    public SoundInstance getSoundInstance() {
        return this.soundInstance;
    }
}
