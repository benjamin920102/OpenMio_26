package me.mioclient.module.combat.offhand;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.inventory.InventoryUtils;

import java.util.Iterator;
import me.mioclient.module.combat.Offhand;
import me.mioclient.module.exploit.XCarry;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/OffhandMode.class */
public enum OffhandMode implements ClientAccessor, NamedValue {
    Totem("Totem", Items.TOTEM_OF_UNDYING),
    Crystal("Crystal", Items.END_CRYSTAL),
    Gapple("Gapple", Items.GOLDEN_APPLE) { // from class: me.mioclient.module.combat.offhand.OffhandMode.Inner
        @Override // me.mioclient.module.combat.offhand.OffhandMode
        public Item getOffhandItem(boolean z) {
            return z ? super.getOffhandItem(true) : Items.ENCHANTED_GOLDEN_APPLE;
        }
    },
    Custom("Custom", Items.SHIELD) { // from class: me.mioclient.module.combat.offhand.OffhandMode.Inner_2
        @Override // me.mioclient.module.combat.offhand.OffhandMode
        public Item getOffhandItem(boolean z) {
            return OffhandMode.offhand.custom.getValue().stream().findAny().orElse(null);
        }
    };

    public static final XCarry xCarry = (XCarry) ClientServices.moduleInjector.getModule(XCarry.class);
    public static final Offhand offhand = (Offhand) ClientServices.moduleInjector.getModule(Offhand.class);
    public final String name;
    public final Item item;

    OffhandMode(String str, Item item) {
        this.name = str;
        this.item = item;
    }

    public int findOffhandItemSlot(boolean z) {
        Item item1241 = getOffhandItem(z);
        if (item1241 == null || minecraftClient.player.getOffHandStack().isOf(item1241)) {
            return -1;
        }
        if ((minecraftClient.currentScreen instanceof ShulkerBoxScreen) || (minecraftClient.currentScreen instanceof GenericContainerScreen)) {
            Iterator it = ClientAccessor.minecraftClient.player.currentScreenHandler.slots.iterator();
            while (it.hasNext()) {
                Slot slot = (Slot) it.next();
                if (slot.getStack().isOf(item1241)) {
                    return slot.id;
                }
            }
        }
        return InventoryUtils.findSlot(item1241, xCarry.isToggled());
    }

    public Item getOffhandItem(boolean z) {
        return this.item;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
