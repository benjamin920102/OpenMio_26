package me.mioclient.util.inventory;

import java.util.Iterator;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;

public final class ItemTypeUtils {
    public ItemTypeUtils() {
        throw new AssertionError();
    }

    public static boolean isShulkerBox(Item item) {
        return (item instanceof BlockItem) && (((BlockItem) item).getBlock() instanceof ShulkerBoxBlock);
    }

    public static boolean isBed(Item item) {
        return (item instanceof BlockItem) && (((BlockItem) item).getBlock() instanceof BedBlock);
    }

    public static boolean hasPotionEffect(ItemStack itemStack, Holder<MobEffect> registryEntry) {
        Iterator it = PotionResolver.resolvePotion(itemStack).getEffects().iterator();
        while (it.hasNext()) {
            if (((MobEffectInstance) it.next()).getEffect() == registryEntry) {
                return true;
            }
        }
        return false;
    }
}
