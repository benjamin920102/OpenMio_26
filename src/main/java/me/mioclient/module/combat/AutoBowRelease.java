package me.mioclient.module.combat;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.network.PacketSender;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.module.Module;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/AutoBowRelease.class */
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
        if (minecraftClient.player.getItemUseTime() >= this.delay.getValue().intValue()) {
            Item item = minecraftClient.player.getActiveItem().getItem();
            if (item instanceof RangedWeaponItem) {
                CrossbowItem crossbowItem = item instanceof CrossbowItem ? (CrossbowItem) item : null;
                if (minecraftClient.player.isUsingItem()) {
                    if (!(crossbowItem instanceof CrossbowItem) || crossbowItem.charged) {
                        minecraftClient.interactionManager.stopUsingItem(minecraftClient.player);
                        PacketSender.sendPacket(new PlayerInteractItemC2SPacket(minecraftClient.player.getActiveHand(), PacketSender.nextSequence(), minecraftClient.player.getYaw(), minecraftClient.player.getPitch()));
                    }
                }
            }
        }
    }
}
