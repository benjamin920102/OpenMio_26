package me.mioclient.module.combat.offhand;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientAccessor;

import java.util.Iterator;
import me.mioclient.module.combat.Offhand;
import me.mioclient.module.exploit.XCarry;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.Slot;

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
            Offhand offhand_lcl = OffhandMode.getOffhand();
            if (offhand_lcl == null) {
                return null;
            }
            return offhand_lcl.custom.getValue().stream().findAny().orElse(null);
        }
    };

    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving xCarry/offhand null. */
    private static XCarry xCarry;
    private static Offhand offhand;

    public static XCarry getXCarry() {
        if (xCarry == null && ClientServices.moduleInjector != null) {
            try {
                xCarry = (XCarry) ClientServices.moduleInjector.getModule(XCarry.class);
            } catch (Exception e) {
            }
        }
        return xCarry;
    }

    public static Offhand getOffhand() {
        if (offhand == null && ClientServices.moduleInjector != null) {
            try {
                offhand = (Offhand) ClientServices.moduleInjector.getModule(Offhand.class);
            } catch (Exception e) {
            }
        }
        return offhand;
    }
    public final String name;
    public final Item item;

    OffhandMode(String str, Item item) {
        this.name = str;
        this.item = item;
    }

    public int findOffhandItemSlot(boolean z) {
        Item item1241 = getOffhandItem(z);
        if (item1241 == null || minecraftClient.player.getOffhandItem().getItem() == item1241) {
            return -1;
        }
        if ((minecraftClient.gui.screen() instanceof ShulkerBoxScreen) || (minecraftClient.gui.screen() instanceof ContainerScreen)) {
            Iterator it = ClientAccessor.minecraftClient.player.containerMenu.slots.iterator();
            while (it.hasNext()) {
                Slot slot = (Slot) it.next();
                if (slot.getItem().getItem() == item1241) {
                    return slot.getContainerSlot();
                }
            }
        }
        XCarry xCarry_lcl = getXCarry();
        return InventoryUtils.findSlot(item1241, xCarry_lcl != null && xCarry_lcl.isToggled());
    }

    public Item getOffhandItem(boolean z) {
        return this.item;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}