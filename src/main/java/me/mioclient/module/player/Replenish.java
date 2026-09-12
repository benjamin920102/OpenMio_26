package me.mioclient.module.player;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.MovementUtils;

import java.util.List;
import java.util.Set;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.exploit.XCarry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/Replenish.class */
public class Replenish extends Module {
    public static XCarry xCarry = (XCarry) ClientServices.moduleInjector.getModule(XCarry.class);
    public static AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public Setting<Set<Item>> items = add(new EntityListObjectSetting<>("Items", Registries.ITEM));
    public Setting<ScaffoldMode_2> selection = add(new EnumSetting<>("Selection", ScaffoldMode_2.ANY));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 50, 0, 500).setSuffix("ms"));
    public Setting<Integer> threshold = add(new NumberSetting<>("Threshold", 32, 16, 63));
    public Setting<Boolean> sort = add(new BooleanSetting("Sort", false));
    public Setting<Boolean> unstackable = add(new BooleanSetting("Unstackable", false));
    public Setting<Boolean> limit = add(new BooleanSetting("Limit", true).setDependent());
    public Setting<Boolean> motion = add(new BooleanSetting("Motion", false, v -> this.limit.isShown()));
    public Setting<Boolean> falling = add(new BooleanSetting("Falling", false, v -> this.limit.isShown()));
    public final List<Item> list;
    public final Stopwatch stopwatch;

    public Replenish() {
        super("Replenish", "Replenishes your hotbar.", Category.PLAYER, new String[0]);
        this.list = DefaultedList.ofSize(9, Items.AIR);
        this.stopwatch = new Stopwatch();
        setDrawn(false);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue()) && minecraftClient.player.currentScreenHandler.getCursorStack().isEmpty()) {
            if (MovementUtils.is2181() && this.motion.getValue().booleanValue()) {
                return;
            }
            if (minecraftClient.player.getVelocity().getY() <= 0.0d || !this.falling.getValue().booleanValue()) {
                boolean z = minecraftClient.currentScreen instanceof HandledScreen;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = minecraftClient.player.getInventory().getStack(i);
                    Item item = this.list.get(i);
                    if (this.unstackable.getValue().booleanValue() && stack.isEmpty() && item != Items.AIR && !z) {
                        if (shouldReplenishItem(item, i)) {
                            this.stopwatch.reset();
                            return;
                        }
                    }
                    this.list.set(i, stack.getItem());
                }
                if (!z || (minecraftClient.currentScreen instanceof InventoryScreen)) {
                    for (int i2 = 0; i2 < 9; i2++) {
                        if (is2852(i2)) {
                            this.stopwatch.reset();
                            return;
                        }
                    }
                }
            }
        }
    }

    public boolean shouldReplenishItem(Item item, int i) {
        if (!this.selection.getValue().isAllowed(item, this.items.getValue())) {
            return false;
        }
        boolean z = autoCrystal.isToggled() && autoCrystal.forceSuicide.getValue().booleanValue();
        int i2 = InventoryUtils.findSlot(item, itemStack -> {
            return (itemStack == minecraftClient.player.getOffHandStack() || (z && itemStack.getItem() == Items.TOTEM_OF_UNDYING)) ? false : true;
        }, true);
        if (i2 == -1 && item == Items.TOTEM_OF_UNDYING && !z) {
            i2 = InventoryUtils.findInInventory(itemStack2 -> {
                return is2851(itemStack2.getItem()) && itemStack2 != minecraftClient.player.getOffHandStack();
            }, true);
        }
        if (i2 >= 36 || i2 == -1) {
            return false;
        }
        if (!is2850(i)) {
            InventoryUtils.swapStacks(i2, InventoryUtils.toNetworkSlot(i));
            return true;
        }
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, i2, 0, SlotActionType.QUICK_MOVE, minecraftClient.player);
        return true;
    }

    public boolean is2850(int i) {
        for (int i2 = 0; i2 < 9; i2++) {
            if (i2 != i) {
                if (minecraftClient.player.getInventory().getStack(i2).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean is2851(Item item) {
        return item == Items.ENCHANTED_GOLDEN_APPLE || item == Items.GOLDEN_APPLE;
    }

    public boolean is2852(int i) {
        ItemStack stack = minecraftClient.player.getInventory().getStack(i);
        if (!this.selection.getValue().isAllowed(stack.getItem(), this.items.getValue())) {
            return false;
        }
        float intValue = this.threshold.getValue().intValue() / Float.intBitsToFloat(1115684864);
        if (stack.isEmpty() || stack.getItem() == Items.AIR || !stack.isStackable() || stack.getCount() / stack.getMaxCount() >= intValue) {
            return false;
        }
        int i2 = -1;
        int i3 = 0;
        for (int i4 = 9; i4 < 36; i4++) {
            ItemStack stack2 = minecraftClient.player.getInventory().getStack(i4);
            boolean areItemsAndComponentsEqual = ItemStack.areItemsAndComponentsEqual(stack2, stack);
            if (!stack2.isEmpty() && areItemsAndComponentsEqual && stack2.getCount() > i3) {
                i2 = i4;
                i3 = stack2.getCount();
            }
        }
        if (xCarry.isToggled() && !(minecraftClient.currentScreen instanceof InventoryScreen)) {
            for (int i5 = 0; i5 <= 3; i5++) {
                ItemStack itemStack = (ItemStack) minecraftClient.player.playerScreenHandler.getCraftingInput().getHeldStacks().get(i5);
                boolean areItemsAndComponentsEqual2 = ItemStack.areItemsAndComponentsEqual(itemStack, stack);
                if (!itemStack.isEmpty() && areItemsAndComponentsEqual2 && itemStack.getCount() > i3) {
                    i2 = i5 + 1;
                    i3 = itemStack.getCount();
                }
            }
        }
        if (i2 == -1) {
            return false;
        }
        int i6 = minecraftClient.player.currentScreenHandler.syncId;
        if (!this.sort.getValue().booleanValue() || i3 == 1 || stack.getMaxCount() - stack.getCount() <= i3) {
            if (!InventoryUtils.isHotbarFull()) {
                InventoryUtils.swapStacks(i2, InventoryUtils.toNetworkSlot(i));
                return true;
            }
            minecraftClient.interactionManager.clickSlot(i6, i2, 0, SlotActionType.QUICK_MOVE, minecraftClient.player);
            return true;
        }
        ClientServices.fireworksHelper.setSyncBatching(true);
        minecraftClient.interactionManager.clickSlot(i6, i2, 0, SlotActionType.PICKUP, minecraftClient.player);
        minecraftClient.interactionManager.clickSlot(i6, i2, 1, SlotActionType.PICKUP, minecraftClient.player);
        minecraftClient.interactionManager.clickSlot(i6, InventoryUtils.toNetworkSlot(i), 0, SlotActionType.PICKUP, minecraftClient.player);
        ClientServices.fireworksHelper.setSyncBatching(false);
        return true;
    }
}
