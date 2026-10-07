package me.mioclient.module.player;

import me.mioclient.api.NamedValue;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;

public class NoInteract extends Module {
    public Setting<Mode> mode = add(new EnumSetting<>("Mode", Mode.CANCEL));
    public Setting<Boolean> onlyFood = add(new BooleanSetting("OnlyFood", false));
    public boolean flag;

    
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
        return itemStack.has(DataComponents.FOOD) || (itemStack.getItem() instanceof EnderpearlItem) || (itemStack.getItem() instanceof ProjectileItem) || (itemStack.getItem() instanceof ProjectileWeaponItem);
    }

    public static InteractionHand getUsableHand() {
        return (isUsableItem(minecraftClient.player.getMainHandItem()) || !isUsableItem(minecraftClient.player.getOffhandItem())) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public boolean shouldBlockInteraction(ItemStack itemStack, BlockPos blockPos) {
        if (!isToggled()) {
            return false;
        }
        if (this.onlyFood.getValue().booleanValue() && !itemStack.has(DataComponents.FOOD)) {
            return false;
        }
        BlockState blockState = minecraftClient.level.getBlockState(blockPos);
        if (blockState.is(Blocks.ANVIL)) {
            return true;
        }
        return blockState.hasBlockEntity() && !(blockState.getBlock() instanceof BedBlock);
    }
}