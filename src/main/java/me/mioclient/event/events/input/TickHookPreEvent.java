package me.mioclient.event.events.input;

import net.minecraft.client.player.ClientInput;

/* 26.2 port: Yarn mutable Input -> MojMap ClientInput. */
public class TickHookPreEvent extends InputTickEvent {
    public TickHookPreEvent(ClientInput input) {
        super(input);
    }
}
