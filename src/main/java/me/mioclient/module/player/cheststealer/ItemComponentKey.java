package me.mioclient.module.player.cheststealer;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ItemComponentKey.class */
public final class ItemComponentKey {
    public final Item item;
    public final ComponentMap componentMap;

    public ItemComponentKey(Item item, ComponentMap componentMap) {
        this.item = item;
        this.componentMap = componentMap;
    }




    public Item getItem() {
        return this.item;
    }

    public ComponentMap getComponentMap() {
        return this.componentMap;
    }
}
