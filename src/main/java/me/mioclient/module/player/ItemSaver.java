package me.mioclient.module.player;

import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.ducks.DuckClientPlayerInteractionManager;
import me.mioclient.api.Category;
import me.mioclient.event.events.interaction.AttackBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/ItemSaver.class */
public class ItemSaver extends Module {
    public ItemSaver() {
        super("ItemSaver", "Prevents your tools from breaking.", Category.PLAYER, new String[0]);
    }

    @Listen
    public void onAttackBlock(AttackBlockEvent attackBlockEvent) {
        int i = minecraftClient.player.getInventory().selectedSlot;
        if (isToolSafe(minecraftClient.player.getInventory().getStack(i)) || ((DuckClientPlayerInteractionManager) minecraftClient.interactionManager).getCurrentBreakingBlock() == null || !minecraftClient.options.attackKey.isPressed()) {
            return;
        }
        InventoryUtils.selectSlot(i == 8 ? i - 1 : i + 1);
        attackBlockEvent.cancel();
    }

    public static boolean isToolSafe(ItemStack itemStack) {
        return itemStack.isEmpty() || !itemStack.isDamageable() || (itemStack.getItem() instanceof ArmorItem) || itemStack.isOf(Items.ELYTRA) || ExplosionDamageCalculator.getClampedDurabilityPercent(itemStack) > 10;
    }
}
