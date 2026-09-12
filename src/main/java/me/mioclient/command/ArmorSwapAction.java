package me.mioclient.command;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.SlotPair;

import me.mioclient.util.inventory.SlotPair;
import me.mioclient.util.inventory.InventoryUtils;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import me.mioclient.command.ActionCommand;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ArmorSwapAction.class */
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
        if (item instanceof ArmorItem) {
            i = 8 - ((ArmorItem) item).getSlotType().getEntitySlotId();
        }
        return new SlotPair(InventoryUtils.findSlot(item), i);
    }
}
