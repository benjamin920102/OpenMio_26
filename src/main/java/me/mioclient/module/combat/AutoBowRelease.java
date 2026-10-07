package me.mioclient.module.combat;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.network.PacketSender;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.module.Module;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import me.mioclient.setting.NumberSetting;

public class AutoBowRelease extends Module {
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 5, 2, 20));

    public AutoBowRelease() {
        super("AutoBowRelease", "Shoots your bow automatically.", Category.COMBAT, new String[0]);
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (isNotInGame() || InventoryUtils.countItems(itemStack -> {
            return itemStack.getItem() instanceof ArrowItem;
        }) <= 0) {
            return;
        }
        if (minecraftClient.player.getTicksUsingItem() >= this.delay.getValue().intValue()) {
            ItemStack activeStack = minecraftClient.player.getActiveItem();
            Item item = activeStack.getItem();
            if (item instanceof ProjectileWeaponItem) {
                CrossbowItem crossbowItem = item instanceof CrossbowItem ? (CrossbowItem) item : null;
                if (minecraftClient.player.isUsingItem()) {
                    if (!(crossbowItem instanceof CrossbowItem) || CrossbowItem.isCharged(activeStack)) {
                        minecraftClient.gameMode.releaseUsingItem(minecraftClient.player);
                        PacketSender.sendPacket(new ServerboundUseItemPacket(minecraftClient.player.getUsedItemHand(), PacketSender.nextSequence(), minecraftClient.player.getYRot(), minecraftClient.player.getXRot()));
                    }
                }
            }
        }
    }
}