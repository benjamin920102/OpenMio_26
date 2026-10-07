package me.mioclient.command;

import me.mioclient.util.inventory.SlotPair;

import me.mioclient.api.ClientAccessor;

import net.minecraft.world.item.Item;

public interface ItemSwapHandler extends ClientAccessor {
    void swapForward(SlotPair data_4);

    void swapBackward(SlotPair data_4);

    SlotPair getSlotPair(Item item);
}
