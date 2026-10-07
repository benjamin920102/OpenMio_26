package me.mioclient.gui.screen;

import me.mioclient.api.ClientAccessor;

import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ConfirmGameClose extends ConfirmScreen {

    
    private static final class BooleanConsumer implements it.unimi.dsi.fastutil.booleans.BooleanConsumer, ClientAccessor {
        public void accept(boolean z) {
            // 26.2 port: old disconnecting flag is gone; staying in-game means level+player present.
            if (minecraftClient.level != null && minecraftClient.player != null) {
                minecraftClient.gui.setScreen(null);
            }
            if (z) {
                minecraftClient.stop();
            } else {
                GLFW.glfwSetWindowShouldClose(minecraftClient.getWindow().handle(), false);
            }
        }
    }

    public ConfirmGameClose() {
        super(new BooleanConsumer(), Component.literal("Confirm Game Close"), Component.literal("Are you sure that you want to close minecraft?"));
    }
}
