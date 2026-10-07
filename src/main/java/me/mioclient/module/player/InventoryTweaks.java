package me.mioclient.module.player;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import org.lwjgl.glfw.GLFW;
import me.mioclient.mixin.ducks.DuckHandledScreen;
import me.mioclient.setting.BooleanSetting;

public class InventoryTweaks extends Module {
    public Setting<Boolean> fastMove = add(new BooleanSetting("FastMove", true).setVisible());
    public Setting<Boolean> onlyContainer = add(new BooleanSetting("OnlyContainer", false).requiresSettings(this.fastMove));
    public Setting<Boolean> fastArmor = add(new BooleanSetting("FastArmor", false));
    public Setting<Boolean> xCarryTweaks = add(new BooleanSetting("XCarryTweaks", true));
    public int num;

    public InventoryTweaks() {
        super("InventoryTweaks", "Modifies your inventory actions.", Category.PLAYER, new String[0]);
    }

    @Listen
    public void onHudRender(HudRenderEvent matrixStackEvent_2) {
        Slot mio$getFocusedSlot;
        if (this.fastMove.getValue().booleanValue() && (minecraftClient.gui.screen() instanceof AbstractContainerScreen)) {
            if ((this.onlyContainer.getValue().booleanValue() && !(minecraftClient.gui.screen() instanceof ContainerScreen) && !(minecraftClient.gui.screen() instanceof ShulkerBoxScreen)) || !minecraftClient.player.containerMenu.getCarried().isEmpty() || (mio$getFocusedSlot = ((me.mioclient.mixin.ducks.DuckHandledScreen)(Object) minecraftClient.gui.screen()).mio$getFocusedSlot()) == null || mio$getFocusedSlot.index == this.num || mio$getFocusedSlot.getItem().isEmpty()) {
                return;
            }
            int i = -1;
            if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().handle(), 0) == 1) {
                i = 0;
            }
            if (GLFW.glfwGetKey(minecraftClient.getWindow().handle(), 340) != 1 || i == -1) {
                return;
            }
            minecraftClient.gameMode.handleContainerInput(minecraftClient.player.containerMenu.containerId, mio$getFocusedSlot.index, i, ContainerInput.QUICK_MOVE, minecraftClient.player);
            this.num = mio$getFocusedSlot.index;
        }
    }
}