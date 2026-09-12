package me.mioclient.event.events.player;

import net.minecraft.item.ItemStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/FinishUsingEvent.class */
public final class FinishUsingEvent extends me.mioclient.event.events.player.TickMovementEvent {
    public ItemStack itemStack;

    public FinishUsingEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
