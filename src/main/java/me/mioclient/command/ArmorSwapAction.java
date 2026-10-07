package me.mioclient.command;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.SlotPair;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import me.mioclient.command.ActionCommand;

public class ArmorSwapAction implements ItemSwapHandler {
    @Override // me.mioclient.command.ItemSwapHandler
    public void swapForward(SlotPair data_4) {
        InventoryUtils.swapStacks(data_4.getFirstSlot(), data_4.getSecondSlot());
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public void swapBackward(SlotPair data_4) {
        InventoryUtils.swapStacks(data_4.getSecondSlot(), data_4.getFirstSlot());
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public SlotPair getSlotPair(Item item) {
        if (!ActionCommand.is1295(item)) {
            return SlotPair.data_4;
        }
        int i = 6;
        var equippable = new ItemStack(item).get(DataComponents.EQUIPPABLE);
        if (equippable != null) {
            i = 8 - equippable.slot().getId();
        }
        return new SlotPair(InventoryUtils.findSlot(item), i);
    }
}
