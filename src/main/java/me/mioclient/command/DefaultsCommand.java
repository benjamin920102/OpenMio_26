package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import java.util.Iterator;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_21.class */
public class DefaultsCommand extends CommandFeature {
    public DefaultsCommand() {
        super("defaults");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
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
                minecraftClient.setScreen((Screen) null);
            }, Text.of("Reset to defaults."), Text.of("Continue?"));
            runLater(() -> {
                minecraftClient.setScreen((Screen) confirmScreen);
            });
            return 1;
        });
    }
}
