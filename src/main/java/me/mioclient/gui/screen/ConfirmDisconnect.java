package me.mioclient.gui.screen;

import me.mioclient.api.ClientAccessor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfirmDisconnect extends ConfirmScreen {

    
    private static final class BooleanConsumer implements it.unimi.dsi.fastutil.booleans.BooleanConsumer, ClientAccessor {
        public final Runnable runnable;

        public BooleanConsumer(Runnable runnable) {
            this.runnable = runnable;
        }

        public void accept(boolean z) {
            minecraftClient.gui.setScreen(null);
            if (z) {
                this.runnable.run();
            }
        }

        public Runnable getDisconnectAction() {
            return this.runnable;
        }
    }

    public ConfirmDisconnect(Runnable runnable) {
        super(new BooleanConsumer(runnable), Component.literal("Confirm Disconnect"), Component.literal("Are you sure that you want to disconnect?"));
    }
}
