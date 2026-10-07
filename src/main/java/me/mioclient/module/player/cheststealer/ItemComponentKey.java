package me.mioclient.module.player.cheststealer;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;

public final class ItemComponentKey {
    public final Item item;
    public final DataComponentMap componentMap;

    public ItemComponentKey(Item item, DataComponentMap componentMap) {
        this.item = item;
        this.componentMap = componentMap;
    }

    public Item getItem() {
        return this.item;
    }

    public DataComponentMap getComponentMap() {
        return this.componentMap;
    }

    // 26.2: value key for ChestStealer refill grouping — equality on item + components.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemComponentKey other)) {
            return false;
        }
        return this.item.equals(other.item) && this.componentMap.equals(other.componentMap);
    }

    @Override
    public int hashCode() {
        return 31 * this.item.hashCode() + this.componentMap.hashCode();
    }
}