package me.mioclient.command;

import me.mioclient.util.inventory.SlotPair;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.inventory.SlotPair;

import net.minecraft.item.Item;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ItemDataHandler.class */
public interface ItemSwapHandler extends ClientAccessor {
    void swapForward(SlotPair data_4);

    void swapBackward(SlotPair data_4);

    SlotPair getSlotPair(Item item);
}
