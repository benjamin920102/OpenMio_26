package me.mioclient.command;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.SlotPair;

import net.minecraft.world.item.Item;

public class HotbarSwapAction implements ItemSwapHandler {
    @Override // me.mioclient.command.ItemSwapHandler
    public void swapForward(SlotPair data_4) {
        InventoryUtils.swapToHotbar(data_4.getFirstSlot());
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public void swapBackward(SlotPair data_4) {
        InventoryUtils.swapToHotbar(data_4.getFirstSlot());
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public SlotPair getSlotPair(Item item) {
        int i = InventoryUtils.toNetworkSlot(InventoryUtils.findInHotbar(item));
        if (i == -1) {
            i = InventoryUtils.findSlot(item);
        }
        return new SlotPair(i, minecraftClient.player.getInventory().getSelectedSlot());
    }
}
