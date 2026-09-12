package me.mioclient.command;

import me.mioclient.manager.ConfigManager;

import java.awt.Desktop;
import java.io.IOException;
import net.minecraft.command.CommandSource;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_17.class */
public final class OpenFolderCommand extends CommandFeature {
    public OpenFolderCommand() {
        super("folder");
        setAliases("openfolder");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            try {
                Desktop.getDesktop().open(ConfigManager.path.toFile());
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
        });
    }
}
