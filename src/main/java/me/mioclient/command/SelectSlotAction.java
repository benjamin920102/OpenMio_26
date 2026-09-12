package me.mioclient.command;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.SlotPair;

import me.mioclient.util.inventory.SlotPair;
import me.mioclient.util.inventory.InventoryUtils;

import net.minecraft.item.Item;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SelectSlotAction.class */
public class SelectSlotAction implements ItemSwapHandler {
    @Override // me.mioclient.command.ItemSwapHandler
    public void swapForward(SlotPair data_4) {
        InventoryUtils.do438(data_4.getFirstSlot());
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public void swapBackward(SlotPair data_4) {
        InventoryUtils.do438(data_4.getSecondSlot());
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public SlotPair getSlotPair(Item item) {
        return new SlotPair(InventoryUtils.findInHotbar(item), minecraftClient.player.getInventory().selectedSlot);
    }
}
