package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.world.InteractionHand;

/* 26.2 port: Yarn InteractionHand -> MojMap InteractionHand. */
public class InteractItemEvent extends Event {
    public final InteractionHand hand;

    public InteractItemEvent(InteractionHand hand) {
        this.hand = hand;
    }

    public InteractionHand getHand() {
        return this.hand;
    }
}
