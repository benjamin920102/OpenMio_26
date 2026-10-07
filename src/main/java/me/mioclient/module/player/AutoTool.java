package me.mioclient.module.player;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.ducks.DuckClientPlayerInteractionManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.state.BlockState;
import me.mioclient.setting.BooleanSetting;

public class AutoTool extends Module {
    public Setting<Boolean> swapBack = add(new BooleanSetting("SwapBack", false));
    public int num;
    public boolean flag;

    public AutoTool() {
        super("AutoTool", "Equips the best tool to use on a block for you.", Category.PLAYER, new String[0]);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (((DuckClientPlayerInteractionManager)(Object) minecraftClient.gameMode).getCurrentBreakingBlock() == null || !minecraftClient.options.keyAttack.isDown()) {
            if (this.swapBack.getValue().booleanValue()) {
                if (!this.flag) {
                    this.num = minecraftClient.player.getInventory().getSelectedSlot();
                    return;
                }
                minecraftClient.player.getInventory().setSelectedSlot(this.num);
                this.flag = false;
                return;
            }
            return;
        }
        this.flag = true;
        int i = minecraftClient.player.getInventory().getSelectedSlot();
        BlockState blockState = minecraftClient.level.getBlockState(((DuckClientPlayerInteractionManager)(Object) minecraftClient.gameMode).getCurrentBreakingBlock());
        double miningSpeedMultiplier = minecraftClient.player.getInventory().getItem(i).getDestroySpeed(blockState);
        for (int i2 = 0; i2 < 9; i2++) {
            double miningSpeedMultiplier2 = minecraftClient.player.getInventory().getItem(i2).getDestroySpeed(blockState);
            if (miningSpeedMultiplier2 > miningSpeedMultiplier) {
                miningSpeedMultiplier = miningSpeedMultiplier2;
                i = i2;
            }
        }
        InventoryUtils.selectSlot(i);
    }
}