package me.mioclient.command;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.SlotPair;

import me.mioclient.util.inventory.SlotPair;
import me.mioclient.util.inventory.InventoryUtils;

import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/OffhandSwapAction.class */
public class OffhandSwapAction implements ItemSwapHandler {
    @Override // me.mioclient.command.ItemSwapHandler
    public void swapForward(SlotPair data_4) {
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, data_4.getSecondSlot(), 40, SlotActionType.SWAP, minecraftClient.player);
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public void swapBackward(SlotPair data_4) {
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, data_4.getSecondSlot(), 40, SlotActionType.SWAP, minecraftClient.player);
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public SlotPair getSlotPair(Item item) {
        return new SlotPair(InventoryUtils.findSlot(item), 40);
    }
}
