package me.mioclient.util.inventory;

import me.mioclient.api.ClientAccessor;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/feature/EnchantmentHelper.class */
public final class EnchantmentHelper implements ClientAccessor {
    public EnchantmentHelper() {
        throw new IllegalArgumentException("Illegal constructor call");
    }

    public static int getEnchantmentLevel(RegistryKey<Enchantment> registryKey, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        for (Object2IntMap.Entry entry : itemStack.getEnchantments().getEnchantmentEntries()) {
            if (((RegistryEntry) entry.getKey()).matchesKey(registryKey)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getEquippedEnchantmentLevel(RegistryKey<Enchantment> registryKey, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
        return getEnchantmentLevel(registryKey, livingEntity.getEquippedStack(equipmentSlot));
    }

    public static int getPlayerEnchantmentLevel(RegistryKey<Enchantment> registryKey, EquipmentSlot equipmentSlot) {
        return getEquippedEnchantmentLevel(registryKey, equipmentSlot, minecraftClient.player);
    }

    public static boolean hasEnchantment(RegistryKey<Enchantment> registryKey, ItemStack itemStack) {
        return getEnchantmentLevel(registryKey, itemStack) > 0;
    }

    public static boolean hasEquippedEnchantment(RegistryKey<Enchantment> registryKey, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
        return getEquippedEnchantmentLevel(registryKey, equipmentSlot, livingEntity) > 0;
    }

    public static boolean hasPlayerEnchantment(RegistryKey<Enchantment> registryKey, EquipmentSlot equipmentSlot) {
        return getPlayerEnchantmentLevel(registryKey, equipmentSlot) > 0;
    }

    public static Map<RegistryKey<Enchantment>, Integer> getEnchantmentMap(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap hashMap = new HashMap();
        for (Object2IntMap.Entry entry : itemStack.getEnchantments().getEnchantmentEntries()) {
            Optional key = ((RegistryEntry) entry.getKey()).getKey();
            if (!key.isEmpty()) {
                hashMap.put((RegistryKey) key.get(), Integer.valueOf(entry.getIntValue()));
            }
        }
        return hashMap;
    }

    public static RegistryEntry<Enchantment> getEnchantmentEntry(RegistryKey<Enchantment> registryKey) {
        return (RegistryEntry) minecraftClient.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(registryKey).orElse(null);
    }
}
