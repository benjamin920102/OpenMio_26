package me.mioclient.command;

import me.mioclient.api.ClientServices;

import java.util.Iterator;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class DefaultsCommand extends CommandFeature {
    public DefaultsCommand() {
        super("defaults");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            ConfirmScreen confirmScreen = new ConfirmScreen(z -> {
                if (z) {
                    for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                        Iterator<Setting<?>> it = module.getRegistry().iterator();
                        while (it.hasNext()) {
                            it.next().reset();
                        }
                        module.setToggled(false);
                    }
                }
                minecraftClient.setScreenAndShow((Screen) null);
            }, Component.literal("Reset to defaults."), Component.literal("Continue?"));
            runLater(() -> {
                minecraftClient.setScreenAndShow((Screen) confirmScreen);
            });
            return 1;
        });
    }
}
