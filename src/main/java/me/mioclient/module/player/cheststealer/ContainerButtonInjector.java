package me.mioclient.module.player.cheststealer;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientAccessor;

import me.mioclient.mixin.ducks.DuckHandledScreen;
import me.mioclient.module.player.ChestStealer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ContainerButtonInjector implements ClientAccessor, ScreenEvents.AfterInit {
    public final ChestStealer chestStealer;

    public ContainerButtonInjector(ChestStealer chestStealer) {
        this.chestStealer = chestStealer;
    }

    public void afterInit(Minecraft minecraftClient, Screen screen, int i, int i2) {
        if (((screen instanceof ContainerScreen) || (screen instanceof ShulkerBoxScreen)) && this.chestStealer.isToggled() && this.chestStealer.onlyButtons.getValue().booleanValue()) {
            this.chestStealer.setButtonMode((ChestStealerMode) null);
            int i3 = MathConstants.num;
            int bgWidth = (((screen.width - ((DuckHandledScreen) screen).getBgWidth()) / 2) - i3) - 2;
            int bgHeight = (screen.height - ((DuckHandledScreen) screen).getBgHeight()) / 2;
            for (ChestStealerMode chestStealerMode : ChestStealerMode.values()) {
                Button.Builder builder = new Button.Builder(Component.literal(chestStealerMode.getName()), buttonWidget -> {
                    if (chestStealerMode != this.chestStealer.getChestStealerMode2362()) {
                        this.chestStealer.setButtonMode(chestStealerMode);
                        return;
                    }
                    ClientServices.tooltipsHelper.schedule(() -> {
                        screen.setFocused((GuiEventListener) null);
                    }, 0);
                    this.chestStealer.setButtonMode((ChestStealerMode) null);
                });
                builder.bounds(bgWidth, bgHeight, i3, 15);
                bgHeight += 17;
                Screens.getWidgets(screen).add(builder.build());
            }
        }
    }
}