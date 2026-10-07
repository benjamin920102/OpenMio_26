package me.mioclient.command;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.SlotPair;

import net.minecraft.world.item.Item;

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
        return new SlotPair(InventoryUtils.findInHotbar(item), minecraftClient.player.getInventory().getSelectedSlot());
    }
}
