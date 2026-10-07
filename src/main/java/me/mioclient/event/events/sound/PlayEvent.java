package me.mioclient.event.events.sound;

import me.mioclient.event.Event;

import net.minecraft.client.resources.sounds.SoundInstance;

public final class PlayEvent extends Event {
    public final SoundInstance soundInstance;

    public PlayEvent(SoundInstance soundInstance) {
        this.soundInstance = soundInstance;
    }

    public SoundInstance getSoundInstance() {
        return this.soundInstance;
    }
}
