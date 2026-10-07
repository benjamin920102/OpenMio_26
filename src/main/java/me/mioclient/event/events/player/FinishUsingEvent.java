package me.mioclient.event.events.player;

import net.minecraft.world.item.ItemStack;

public final class FinishUsingEvent extends me.mioclient.event.events.player.TickMovementEvent {
    public ItemStack itemStack;

    public FinishUsingEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
