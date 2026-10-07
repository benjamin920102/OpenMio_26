package me.mioclient.util.inventory;

import me.mioclient.api.ClientAccessor;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;

public final class EnchantmentHelper implements ClientAccessor {
    public EnchantmentHelper() {
        throw new IllegalArgumentException("Illegal constructor call");
    }

    public static int getEnchantmentLevel(ResourceKey<Enchantment> key, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>> entry : itemStack.getEnchantments().entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (holder.is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getEquippedEnchantmentLevel(ResourceKey<Enchantment> key, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
        return getEnchantmentLevel(key, livingEntity.getItemBySlot(equipmentSlot));
    }

    public static int getPlayerEnchantmentLevel(ResourceKey<Enchantment> key, EquipmentSlot equipmentSlot) {
        return getEquippedEnchantmentLevel(key, equipmentSlot, minecraftClient.player);
    }

    public static boolean hasEnchantment(ResourceKey<Enchantment> key, ItemStack itemStack) {
        return getEnchantmentLevel(key, itemStack) > 0;
    }

    public static boolean hasEquippedEnchantment(ResourceKey<Enchantment> key, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
        return getEquippedEnchantmentLevel(key, equipmentSlot, livingEntity) > 0;
    }

    public static boolean hasPlayerEnchantment(ResourceKey<Enchantment> key, EquipmentSlot equipmentSlot) {
        return getPlayerEnchantmentLevel(key, equipmentSlot) > 0;
    }

    public static Map<ResourceKey<Enchantment>, Integer> getEnchantmentMap(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<ResourceKey<Enchantment>, Integer> hashMap = new HashMap<>();
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>> entry : itemStack.getEnchantments().entrySet()) {
            Optional<ResourceKey<Enchantment>> k = ((Holder<Enchantment>) entry.getKey()).unwrapKey();
            if (k.isPresent()) {
                hashMap.put(k.get(), Integer.valueOf(entry.getIntValue()));
            }
        }
        return hashMap;
    }

    public static Holder<Enchantment> getEnchantmentEntry(ResourceKey<Enchantment> key) {
        try {
            return minecraftClient.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
        } catch (Exception e) {
            return null;
        }
    }
}
