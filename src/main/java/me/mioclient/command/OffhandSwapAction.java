package me.mioclient.command;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.SlotPair;

import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.ContainerInput;

public class OffhandSwapAction implements ItemSwapHandler {
    @Override // me.mioclient.command.ItemSwapHandler
    public void swapForward(SlotPair data_4) {
        minecraftClient.gameMode.handleContainerInput(minecraftClient.player.containerMenu.containerId, data_4.getSecondSlot(), 40, ContainerInput.SWAP, minecraftClient.player);
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public void swapBackward(SlotPair data_4) {
        minecraftClient.gameMode.handleContainerInput(minecraftClient.player.containerMenu.containerId, data_4.getSecondSlot(), 40, ContainerInput.SWAP, minecraftClient.player);
    }

    @Override // me.mioclient.command.ItemSwapHandler
    public SlotPair getSlotPair(Item item) {
        return new SlotPair(InventoryUtils.findSlot(item), 40);
    }
}
