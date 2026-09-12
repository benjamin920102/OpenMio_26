package me.mioclient.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.mioclient.module.Module;
import net.minecraft.command.CommandSource;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_3.class */
public final class ToggleCommand extends CommandFeature {
    public ToggleCommand() {
        super("toggle");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("module", new ModuleArgumentType()).then(CommandFeature.argument("state", BoolArgumentType.bool()).executes(commandContext -> {
            ((Module) commandContext.getArgument("module", Module.class)).setToggled(((Boolean) commandContext.getArgument("state", Boolean.class)).booleanValue());
            return 1;
        })).executes(commandContext2 -> {
            ((Module) commandContext2.getArgument("module", Module.class)).toggle();
            return 1;
        }));
    }
}
