package me.mioclient.module.player.scaffold;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.NamedValue;
import me.mioclient.util.inventory.InventoryUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ScaffoldMode_3.class */
public enum ScaffoldMode_3 implements ClientAccessor, NamedValue {
    NONE("None"),
    NORMAL("Normal") { // from class: me.mioclient.module.player.scaffold.ScaffoldMode_3.Inner_2
        @Override // me.mioclient.module.player.scaffold.ScaffoldMode_3
        public void selectSlotAndRun(int i, java.lang.Runnable runnable) {
            InventoryUtils.selectSlot(i);
            runnable.run();
        }
    },
    SILENT("Silent") { // from class: me.mioclient.module.player.scaffold.ScaffoldMode_3.Inner
        @Override // me.mioclient.module.player.scaffold.ScaffoldMode_3
        public void selectSlotAndRun(int i, java.lang.Runnable runnable) {
            int i2 = minecraftClient.player.getInventory().selectedSlot;
            InventoryUtils.selectSlot(i);
            runnable.run();
            InventoryUtils.selectSlot(i2);
        }
    };

    public final String name;

    ScaffoldMode_3(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public void selectSlotAndRun(int i, java.lang.Runnable runnable) {
        if (minecraftClient.player.getInventory().selectedSlot != i) {
            return;
        }
        runnable.run();
    }
}
