package me.mioclient.util.inventory;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import java.util.Iterator;
import me.mioclient.module.player.ItemSaver;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import me.mioclient.mixin.ducks.DuckInteractionManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/InventoryUtils.class */
public class InventoryUtils implements ClientAccessor {
    public static ItemSaver itemSaver = (ItemSaver) ClientServices.moduleInjector.getModule(ItemSaver.class);

    public static void do438(int i) {
        if (i < 0 || i > 8) {
            return;
        }
        minecraftClient.player.getInventory().selectedSlot = i;
        ((me.mioclient.mixin.ducks.DuckInteractionManager) minecraftClient.interactionManager).sync();
    }

    public static void swapToHotbar(int i) {
        if (i < 0) {
            return;
        }
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, toNetworkSlot(i), minecraftClient.player.getInventory().selectedSlot, SlotActionType.SWAP, minecraftClient.player);
    }

    public static void swapSlots(int i, int i2) {
        if (i < 0 || i2 < 0) {
            return;
        }
        int i3 = minecraftClient.player.currentScreenHandler.syncId;
        minecraftClient.interactionManager.clickSlot(i3, i, i2, SlotActionType.SWAP, minecraftClient.player);
    }

    public static void swapStacks(int i, int i2) {
        if (i == -1 || i2 == -1) {
            return;
        }
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, minecraftClient.player);
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, i2, 0, SlotActionType.PICKUP, minecraftClient.player);
        if (minecraftClient.player.currentScreenHandler.getCursorStack().isEmpty()) {
            return;
        }
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, minecraftClient.player);
    }

    public static int findSlot(Item item, boolean z) {
        return findSlot(item, itemStack -> {
            return true;
        }, z);
    }

    public static int findSlot(Item item) {
        return findSlot(item, itemStack -> {
            return true;
        }, false);
    }

    public static int findSlot(Item item, java.util.function.Predicate<ItemStack> predicate, boolean z) {
        if (!ClientServices.fireworksHelper.hasPendingTotemPop() && item.equals(minecraftClient.player.getOffHandStack().getItem())) {
            if (predicate.test(minecraftClient.player.getOffHandStack())) {
                return -1;
            }
        }
        int i = 36;
        while (i >= 0) {
            ItemStack stack = minecraftClient.player.getInventory().getStack(i);
            if (stack.getItem() == item && predicate.test(stack)) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
            i--;
        }
        if (!z) {
            return -1;
        }
        for (int i2 = 0; i2 <= 3; i2++) {
            ItemStack itemStack = (ItemStack) minecraftClient.player.playerScreenHandler.getCraftingInput().getHeldStacks().get(i2);
            if (itemStack.getItem() == item && predicate.test(itemStack)) {
                return i2 + 1;
            }
        }
        return -1;
    }

    public static int findInInventory(java.util.function.Predicate<ItemStack> predicate, boolean z) {
        int i = 36;
        while (true) {
            if (i < (z ? 9 : 0)) {
                return -1;
            }
            if (predicate.test(minecraftClient.player.getInventory().getStack(i))) {
                return i;
            }
            i--;
        }
    }

    public static int findInHotbarAny(Item... itemArr) {
        return findInHotbar(itemStack -> {
            for (Item item : itemArr) {
                if (item.equals(itemStack.getItem())) {
                    return true;
                }
            }
            return false;
        });
    }

    public static int findInHotbar(Item item) {
        return findInHotbar(itemStack -> {
            return itemStack.getItem().equals(item);
        });
    }

    public static int findInHotbar(java.util.function.Predicate<ItemStack> predicate) {
        for (int i = 0; i < 9; i++) {
            if (predicate.test(minecraftClient.player.getInventory().getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int countItems(java.util.function.Predicate<ItemStack> predicate) {
        int i = 0;
        for (int i2 = 40; i2 >= 0; i2--) {
            ItemStack stack = minecraftClient.player.getInventory().getStack(i2);
            if (predicate.test(stack)) {
                i += stack.getCount();
            }
        }
        for (int i3 = 0; i3 <= 3; i3++) {
            ItemStack itemStack = (ItemStack) minecraftClient.player.playerScreenHandler.getCraftingInput().getHeldStacks().get(i3);
            if (predicate.test(itemStack)) {
                i += itemStack.getCount();
            }
        }
        return i;
    }

    public static Hand getHandWith(Item item) {
        if (minecraftClient.player.getOffHandStack().getItem() == item) {
            return Hand.OFF_HAND;
        }
        if (minecraftClient.player.getMainHandStack().getItem() == item) {
            return Hand.MAIN_HAND;
        }
        return null;
    }

    public static float getArmorDurability(PlayerEntity playerEntity) {
        float intBitsToFloat = Float.intBitsToFloat(1120403456);
        Iterator it = playerEntity.getInventory().armor.iterator();
        while (it.hasNext()) {
            float f = getDurabilityPercent((ItemStack) it.next());
            if (f < intBitsToFloat) {
                intBitsToFloat = f;
            }
        }
        return intBitsToFloat;
    }

    public static float getDurabilityPercent(ItemStack itemStack) {
        return 100 - ((int) ((Float.intBitsToFloat(1065353216) - ((itemStack.getMaxDamage() - itemStack.getDamage()) / (float) itemStack.getMaxDamage())) * Float.intBitsToFloat(1120403456)));
    }

    public static int toNetworkSlot(int i) {
        return (i <= -1 || i >= 9) ? i : 36 + i;
    }

    public static boolean isInventoryFull() {
        for (int i = 36; i >= 0; i--) {
            if (minecraftClient.player.getInventory().getStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHotbarFull() {
        for (int i = 0; i < 9; i++) {
            if (minecraftClient.player.getInventory().getStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void selectSlot(int i) {
        if (i == -1) {
            return;
        }
        minecraftClient.player.getInventory().selectedSlot = i;
        ((me.mioclient.mixin.ducks.DuckInteractionManager) minecraftClient.interactionManager).sync();
    }

    public static boolean fitsEquipmentSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        if (equipmentSlot == null) {
            return false;
        }
        ArmorItem item = (itemStack.getItem()) instanceof ArmorItem ? (ArmorItem) (itemStack.getItem()) : null;
        return item instanceof ArmorItem ? item.getSlotType() == equipmentSlot : (itemStack.getItem() instanceof ElytraItem) && equipmentSlot == EquipmentSlot.CHEST;
    }

    public static EquipmentSlot getEquipmentSlot(int i) {
        switch (i) {
            case 36:
                return EquipmentSlot.FEET;
            case 37:
                return EquipmentSlot.LEGS;
            case 38:
                return EquipmentSlot.CHEST;
            case 39:
                return EquipmentSlot.HEAD;
            default:
                return null;
        }
    }

    public static int getBestToolSlot(BlockPos blockPos, boolean z) {
        double longBitsToDouble = Double.longBitsToDouble(-4616189618054758400L);
        int i = -1;
        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        int i2 = 0;
        while (true) {
            if (i2 >= (!z ? 36 : 9)) {
                return i;
            }
            ItemStack stack = minecraftClient.player.getInventory().getStack(i2);
            if (itemSaver.isToggled()) {
                ItemSaver itemSaver2 = itemSaver;
                if (!ItemSaver.isToolSafe(stack)) {
                    i2++;
                }
            }
            double miningSpeedMultiplier = stack.getMiningSpeedMultiplier(blockState);
            if (miningSpeedMultiplier > longBitsToDouble) {
                longBitsToDouble = miningSpeedMultiplier;
                i = i2;
            } else if (i2 == minecraftClient.player.getInventory().selectedSlot && miningSpeedMultiplier == longBitsToDouble) {
                i = i2;
            }
            i2++;
        }
    }

    public static boolean isShulkerBox(Item item) {
        return (item instanceof BlockItem) && (((BlockItem) item).getBlock() instanceof ShulkerBoxBlock);
    }
}
