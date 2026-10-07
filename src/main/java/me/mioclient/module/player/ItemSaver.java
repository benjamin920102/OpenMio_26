package me.mioclient.module.player;

import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.ducks.DuckClientPlayerInteractionManager;
import me.mioclient.api.Category;
import me.mioclient.event.events.interaction.AttackBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemSaver extends Module {
    public ItemSaver() {
        super("ItemSaver", "Prevents your tools from breaking.", Category.PLAYER, new String[0]);
    }

    @Listen
    public void onAttackBlock(AttackBlockEvent attackBlockEvent) {
        int i = minecraftClient.player.getInventory().getSelectedSlot();
        if (isToolSafe(minecraftClient.player.getInventory().getItem(i)) || ((DuckClientPlayerInteractionManager) minecraftClient.gameMode).getCurrentBreakingBlock() == null || !minecraftClient.options.keyAttack.isDown()) {
            return;
        }
        InventoryUtils.selectSlot(i == 8 ? i - 1 : i + 1);
        attackBlockEvent.cancel();
    }

    public static boolean isToolSafe(ItemStack itemStack) {
        return itemStack.isEmpty() || !itemStack.isDamageableItem() || itemStack.has(DataComponents.EQUIPPABLE) || itemStack.is(Items.ELYTRA) || ExplosionDamageCalculator.getClampedDurabilityPercent(itemStack) > 10;
    }
}