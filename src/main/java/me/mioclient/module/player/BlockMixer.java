package me.mioclient.module.player;

import me.mioclient.event.events.network.PacketSendEvent;

import java.util.ArrayList;
import java.util.Random;
import me.mioclient.api.Category;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;

public class BlockMixer extends Module {
    public final Random random;

    public BlockMixer() {
        super("BlockMixer", "Picks a random block from your hotbar as you build.", Category.PLAYER, new String[0]);
        this.random = new Random();
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if (minecraftClient.player.getMainHandItem().getItem() instanceof BlockItem) {
            switchToRandomBlock();
        }
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if ((sendImmediatelyEvent.getPacket() instanceof ServerboundUseItemOnPacket) && (minecraftClient.player.getMainHandItem().getItem() instanceof BlockItem)) {
            switchToRandomBlock();
        }
    }

    public void switchToRandomBlock() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = minecraftClient.player.getInventory().getItem(i);
            if (!stack.isEmpty() && (stack.getItem() instanceof BlockItem)) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        if (arrayList.size() < 2) {
            return;
        }
        minecraftClient.player.getInventory().setSelectedSlot(((Integer) arrayList.get(this.random.nextInt(arrayList.size()))).intValue());
    }
}