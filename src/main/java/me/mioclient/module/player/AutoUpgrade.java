package me.mioclient.module.player;

import me.mioclient.api.NamedValue;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.time.Stopwatch;

import java.util.Iterator;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import me.mioclient.module.Armor;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class AutoUpgrade extends Module {
    public Setting<AutoUpgradeMode> items = add(new EnumSetting<>("Items", AutoUpgradeMode.ANY));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 100, 0, 1000).setSuffix("ms"));
    public Setting<Boolean> safe = add(new BooleanSetting("Safe", true));
    public final Stopwatch stopwatch;
    public static final int num4 = 3;
    public static final int num3 = 2;
    public static final int num2 = 1;
    public static final int num = 0;
    public static final Item item = Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE;

    
    public enum AutoUpgradeMode implements NamedValue {
        ANY("Any"),
        TOOLS("Tools"),
        ARMOR("Armor");

        public final String name;

        AutoUpgradeMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public AutoUpgrade() {
        super("AutoUpgrade", "Automatically upgrades diamond items to netherite.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        AbstractContainerMenu screenHandler = minecraftClient.player.containerMenu;
        if (minecraftClient.gui.screen() instanceof SmithingScreen) {
            if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue())) {
                if (countItemInContainer(screenHandler, item) > 1 || !this.safe.getValue().booleanValue()) {
                    if (!screenHandler.getSlot(3).getItem().isEmpty()) {
                        minecraftClient.gameMode.handleContainerInput(screenHandler.containerId, 3, 0, ContainerInput.QUICK_MOVE, minecraftClient.player);
                        this.stopwatch.reset();
                        return;
                    }
                    ItemStack stack = screenHandler.getSlot(0).getItem();
                    if (stack.isEmpty() || (stack.is(item) && stack.getCount() == 1)) {
                        for (int i = 4; i < 40; i++) {
                            if (screenHandler.getSlot(i).getItem().is(item)) {
                                InventoryUtils.swapStacks(i, 0);
                                this.stopwatch.reset();
                                return;
                            }
                        }
                        return;
                    }
                    if (screenHandler.getSlot(1).getItem().isEmpty()) {
                        int i2 = findUpgradeableSlot(screenHandler);
                        if (i2 == -1) {
                            return;
                        }
                        InventoryUtils.swapStacks(i2, 1);
                        this.stopwatch.reset();
                        return;
                    }
                    if (screenHandler.getSlot(2).getItem().isEmpty()) {
                        for (int i3 = 4; i3 < 40; i3++) {
                            if (screenHandler.getSlot(i3).getItem().is(Items.NETHERITE_INGOT)) {
                                InventoryUtils.swapStacks(i3, 2);
                                this.stopwatch.reset();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public int countItemInContainer(AbstractContainerMenu screenHandler, Item item2) {
        int i = 0;
        Iterator it = screenHandler.slots.iterator();
        while (it.hasNext()) {
            Slot slot = (Slot) it.next();
            if (slot.getItem().is(item2)) {
                i += slot.getItem().getCount();
            }
        }
        return i;
    }

    public int findUpgradeableSlot(AbstractContainerMenu screenHandler) {
        boolean z = this.items.getValue() == AutoUpgradeMode.TOOLS;
        boolean z2 = this.items.getValue() == AutoUpgradeMode.ARMOR;
        for (int i = 4; i < 40; i++) {
            ItemStack stack = screenHandler.getSlot(i).getItem();
            if (!z) {
                // 26.2 port: ArmorItem removed; armor detected via EQUIPPABLE component, diamond via repairable.
                if (stack.has(DataComponents.EQUIPPABLE) && stack.isValidRepairItem(new ItemStack(Items.DIAMOND))) {
                    return i;
                }
            }
            if (!z2) {
                // 26.2 port: ToolItem/DiggerItem removed; tools detected via TOOL component, diamond via repairable.
                if (stack.has(DataComponents.TOOL) && stack.isValidRepairItem(new ItemStack(Items.DIAMOND))) {
                    return i;
                } else if (!stack.has(DataComponents.TOOL) && !stack.has(DataComponents.EQUIPPABLE)) {
                    continue;
                }
            }
        }
        return -1;
    }

    public boolean isDiamondIngredient(Ingredient ingredient) {
        // 26.2 port: Ingredient#getMatchingStacks removed; Predicate#test is equivalent.
        return ingredient.test(new ItemStack(Items.DIAMOND));
    }
}