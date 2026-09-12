package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/InteractBlockEvent.class */
public class InteractBlockEvent extends Event {
    public final BlockHitResult blockHitResult;
    public final Hand hand;

    public InteractBlockEvent(BlockHitResult blockHitResult, Hand hand) {
        this.blockHitResult = blockHitResult;
        this.hand = hand;
    }

    public BlockHitResult getHitResult() {
        return this.blockHitResult;
    }

    public Hand getHand() {
        return this.hand;
    }
}
