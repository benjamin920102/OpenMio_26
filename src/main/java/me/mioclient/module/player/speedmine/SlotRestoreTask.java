package me.mioclient.module.player.speedmine;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.module.player.SpeedMine;
import net.minecraft.item.Item;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpeedMineRunnable.class */
public class SlotRestoreTask implements java.lang.Runnable, ClientAccessor {
    public final SpeedMine speedMine;
    public final long num = System.currentTimeMillis();
    public final Item item;
    public final int num2;
    public final int num3;

    public SlotRestoreTask(SpeedMine speedMine, Item item, int i, int i2) {
        this.item = item;
        this.num2 = i;
        this.num3 = i2;
        this.speedMine = speedMine;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.speedMine.getSlotRestoreTask() == null || this.speedMine.getSlotRestoreTask().equals(this)) {
            if (minecraftClient.player.getInventory().getStack(this.num3).isOf(this.item)) {
                return;
            }
            InventoryUtils.swapToHotbar(this.num2);
        }
    }

    public boolean equals(Object obj) {
        return (obj instanceof SlotRestoreTask) && ((SlotRestoreTask) obj).num == this.num;
    }
}
