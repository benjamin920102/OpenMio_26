package me.mioclient.module.render.tooltips;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.ItemTypeUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.inventory.ItemTypeUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.mioclient.module.render.Tooltips;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ContainerPreview.class */
public final class ContainerPreview {
    public final ItemStack itemStack;
    public final ItemStack itemStack2;
    public static final Tooltips tooltips = (Tooltips) ClientServices.moduleInjector.getModule(Tooltips.class);

    public ContainerPreview(ItemStack itemStack, ItemStack itemStack2) {
        this.itemStack = itemStack;
        this.itemStack2 = itemStack2;
    }

    public static ContainerPreview fromShulkerBox(ItemStack itemStack) {
        ContainerComponent containerComponent;
        if (!ItemTypeUtils.isShulkerBox(itemStack.getItem()) || (containerComponent = (ContainerComponent) itemStack.get(DataComponentTypes.CONTAINER)) == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        List list = containerComponent.stream().toList();
        for (int i = 0; i < list.size(); i++) {
            Item item = ((ItemStack) list.get(i)).getItem();
            hashMap.put(item, Integer.valueOf(1 + ((Integer) hashMap.getOrDefault(item, 0)).intValue()));
        }
        if (hashMap.isEmpty()) {
            return null;
        }
        Map.Entry entry = (Map.Entry) hashMap.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
        if (entry == null) {
            return null;
        }
        return new ContainerPreview(itemStack, ((Item) entry.getKey()).getDefaultStack());
    }




    public ItemStack getItemStack2242() {
        return this.itemStack;
    }

    public ItemStack getSecondaryItemStack() {
        return this.itemStack2;
    }
}
