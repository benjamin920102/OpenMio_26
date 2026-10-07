package me.mioclient.util.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.Holder;

public class PotionResolver {
    public static final Potion potion = new Potion("empty", new MobEffectInstance[0]);

    public static Potion resolvePotion(ItemStack itemStack) {
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null) {
            Holder<Potion> registryEntry = potionContents.potion().orElse(null);
            if (registryEntry != null) {
                return registryEntry.value();
            }
        }
        return potion;
    }
}
