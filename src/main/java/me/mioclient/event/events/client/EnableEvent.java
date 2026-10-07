package me.mioclient.event.events.client;

import me.mioclient.module.Module;

public final class EnableEvent {
    public final Module module;

    public EnableEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return this.module;
    }
}
