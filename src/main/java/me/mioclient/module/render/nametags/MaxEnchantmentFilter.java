package me.mioclient.module.render.nametags;

import me.mioclient.util.inventory.EnchantmentHelper;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;

public final class MaxEnchantmentFilter {
    public static final List<Record> list = List.of(new Record(Enchantments.PROTECTION, 4), new Record(Enchantments.UNBREAKING, 3), new Record(Enchantments.MENDING, 1));
    public static final List<Record> list2 = List.of(new Record(Enchantments.PROTECTION, 4), new Record(Enchantments.UNBREAKING, 3), new Record(Enchantments.MENDING, 1));
    public static final List<Record> list3 = List.of(new Record(Enchantments.BLAST_PROTECTION, 4), new Record(Enchantments.UNBREAKING, 3), new Record(Enchantments.MENDING, 1));
    public static final List<Record> list4 = List.of(new Record(Enchantments.PROTECTION, 4), new Record(Enchantments.UNBREAKING, 3), new Record(Enchantments.MENDING, 1), new Record(Enchantments.FEATHER_FALLING, 4));
    public static final List<Record> list5 = List.of(new Record(Enchantments.UNBREAKING, 3), new Record(Enchantments.MENDING, 1));
    public static final List<Record> list6 = List.of(new Record(Enchantments.SHARPNESS, 5), new Record(Enchantments.FIRE_ASPECT, 2), new Record(Enchantments.KNOCKBACK, 2), new Record(Enchantments.SWEEPING_EDGE, 3), new Record(Enchantments.UNBREAKING, 3), new Record(Enchantments.MENDING, 1));

    
    static /* synthetic */ class Inner {
        public static final /* synthetic */ int[] intArr = new int[EquipmentSlot.values().length];

        static {
            try {
                intArr[EquipmentSlot.MAINHAND.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                intArr[EquipmentSlot.OFFHAND.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                intArr[EquipmentSlot.FEET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                intArr[EquipmentSlot.LEGS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                intArr[EquipmentSlot.CHEST.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                intArr[EquipmentSlot.HEAD.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                intArr[EquipmentSlot.BODY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    
    
    public static final class Record {
        public final ResourceKey<Enchantment> registryKey;
        public final int num;

        public Record(ResourceKey<Enchantment> registryKey, int i) {
            this.registryKey = registryKey;
            this.num = i;
        }

        public ResourceKey<Enchantment> getEnchantment() {
            return this.registryKey;
        }

        public int getRequiredLevel() {
            return this.num;
        }
    }

    public static boolean isFullyEnchanted(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (itemStack.get(DataComponents.EQUIPPABLE) == null) {
            if (item == Items.WOODEN_SWORD || item == Items.STONE_SWORD || item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD) {
                return matchesEnchantmentRequirements(itemStack, list6);
            }
            if (item == Items.ELYTRA) {
                return matchesEnchantmentRequirements(itemStack, list5);
            }
            return false;
        }
        net.minecraft.world.entity.EquipmentSlot slot = itemStack.get(DataComponents.EQUIPPABLE).slot();
        switch (Inner.intArr[slot.ordinal()]) {
            case 1:
            case 2:
                return false;
            case 3:
                return matchesEnchantmentRequirements(itemStack, list4);
            case 4:
                return matchesEnchantmentRequirements(itemStack, list3);
            case 5:
                return matchesEnchantmentRequirements(itemStack, list2);
            case 6:
                return matchesEnchantmentRequirements(itemStack, list);
            case 7:
                return false;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public static boolean matchesEnchantmentRequirements(ItemStack itemStack, Iterable<Record> iterable) {
        for (Record record : iterable) {
            if (EnchantmentHelper.getEnchantmentLevel(record.getEnchantment(), itemStack) != record.getRequiredLevel()) {
                return false;
            }
        }
        return true;
    }
}