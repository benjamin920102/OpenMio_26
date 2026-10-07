package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

/* 26.2 port: Yarn InteractionHand -> MojMap InteractionHand. */
public class InteractBlockEvent extends Event {
    public final BlockHitResult blockHitResult;
    public final InteractionHand hand;

    public InteractBlockEvent(BlockHitResult blockHitResult, InteractionHand hand) {
        this.blockHitResult = blockHitResult;
        this.hand = hand;
    }

    public BlockHitResult getHitResult() {
        return this.blockHitResult;
    }

    public InteractionHand getHand() {
        return this.hand;
    }
}
