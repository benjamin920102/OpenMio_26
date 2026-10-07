package me.mioclient.util.inventory;

import me.mioclient.api.NamedValue;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public final class InventoryKit implements NamedValue {
    public final Map<Integer, Record> map = new HashMap();
    public final String name;

    
    public static final class Record {
        public final int num;
        public final String string;

        public Record(int i, String str) {
            this.num = i;
            this.string = str;
        }

        public boolean matchesItem(ItemStack itemStack) {
            return BuiltInRegistries.ITEM.getId(itemStack.getItem()) == this.num && Objects.equals(KitRegistry.buildPotionDescriptor(itemStack), this.string);
        }

        public int getRawItemId() {
            return this.num;
        }

        public String getNbtString() {
            return this.string;
        }
    }

    public InventoryKit(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public Map<Integer, Record> getSlotRecords() {
        return this.map;
    }
}
