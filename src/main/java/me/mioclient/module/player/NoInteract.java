package me.mioclient.module.player;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/NoInteract.class */
public class NoInteract extends Module {
    public Setting<Mode> mode = add(new EnumSetting<>("Mode", Mode.CANCEL));
    public Setting<Boolean> onlyFood = add(new BooleanSetting("OnlyFood", false));
    public boolean flag;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/player/NoInteract$Mode.class */
    public enum Mode implements NamedValue {
        CANCEL("Cancel"),
        SHIFT("Shift");

        public final String name;

        Mode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public NoInteract() {
        super("NoInteract", "Prevents you from interacting with tile entities.", Category.PLAYER, new String[0]);
    }

    public static boolean isUsableItem(ItemStack itemStack) {
        return itemStack.contains(DataComponentTypes.FOOD) || (itemStack.getItem() instanceof EnderPearlItem) || (itemStack.getItem() instanceof ProjectileItem) || (itemStack.getItem() instanceof RangedWeaponItem);
    }

    public static Hand getUsableHand() {
        return (isUsableItem(minecraftClient.player.getMainHandStack()) || !isUsableItem(minecraftClient.player.getOffHandStack())) ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public boolean shouldBlockInteraction(ItemStack itemStack, BlockPos blockPos) {
        if (!isToggled()) {
            return false;
        }
        if (this.onlyFood.getValue().booleanValue() && !itemStack.contains(DataComponentTypes.FOOD)) {
            return false;
        }
        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.ANVIL)) {
            return true;
        }
        return blockState.hasBlockEntity() && !(blockState.getBlock() instanceof BedBlock);
    }
}
