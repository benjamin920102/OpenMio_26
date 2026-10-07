package me.mioclient.module.render.tooltips;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import me.mioclient.api.ClientAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class ShulkerPreviewScreen extends ShulkerBoxScreen implements ClientAccessor {
    public final Screen screen;

    public ShulkerPreviewScreen(ShulkerBoxMenu shulkerBoxScreenHandler, Inventory playerInventory, Component text, Screen screen) {
        super(shulkerBoxScreenHandler, playerInventory, text);
        this.screen = screen;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, int i, int i2, float f) {
        super.extractRenderState(drawContext, i, i2, f);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() != 256) {
            return super.keyPressed(event);
        }
        minecraftClient.gui.setScreen(this.screen);
        return true;
    }
}