package me.mioclient.util.inventory;

import java.util.Iterator;
import net.minecraft.block.BedBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ItemTypeUtils.class */
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

    public static boolean hasPotionEffect(ItemStack itemStack, RegistryEntry<StatusEffect> registryEntry) {
        Iterator it = PotionResolver.resolvePotion(itemStack).getEffects().iterator();
        while (it.hasNext()) {
            if (((StatusEffectInstance) it.next()).getEffectType() == registryEntry) {
                return true;
            }
        }
        return false;
    }
}
