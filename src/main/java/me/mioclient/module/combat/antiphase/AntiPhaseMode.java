package me.mioclient.module.combat.antiphase;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AntiPhaseMode.class */
public enum AntiPhaseMode implements NamedValue {
    LADDER("Ladder", Items.LADDER),
    VINE("Vine", Items.VINE),
    SCAFFOLDING("Scaffolding", Items.SCAFFOLDING),
    ITEMFRAME("ItemFrame", Items.ITEM_FRAME);

    public final String name;
    public final Item item;

    AntiPhaseMode(String str, Item item) {
        this.name = str;
        this.item = item;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public Item getDesiredItem() {
        return this.item;
    }
}
