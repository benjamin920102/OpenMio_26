package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import me.mioclient.module.Module;
import net.minecraft.command.CommandSource;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_8.class */
public final class AliasCommand extends CommandFeature {

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/Feature_8$Mode.class */
    public enum Mode {
        MODULE(new ModuleArgumentType()),
        PLAYER(new ExamplesArgumentType_3());

        public final com.mojang.brigadier.arguments.ArgumentType<?> argumentType;

        Mode(com.mojang.brigadier.arguments.ArgumentType argumentType) {
            this.argumentType = argumentType;
        }
    }

    public AliasCommand() {
        super("alias");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(buildAliasCommand(Mode.MODULE)).then(buildAliasCommand(Mode.PLAYER));
    }

    public ArgumentBuilder<CommandSource, ?> buildAliasCommand(Mode mode) {
        return CommandFeature.literal(mode.name().toLowerCase()).then(CommandFeature.literal("set").then(CommandFeature.argument("target", mode.argumentType).then(CommandFeature.argument("alias", StringArgumentType.string()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("alias", String.class);
            if (mode == Mode.MODULE) {
                ClientServices.notificationsHelper.setModuleAlias((Module) commandContext.getArgument("target", Module.class), str);
                return 1;
            }
            ClientServices.notificationsHelper.setPlayerAlias((String) commandContext.getArgument("target", String.class), str);
            return 1;
        })))).then(CommandFeature.literal("remove").then(CommandFeature.argument("target", mode.argumentType).executes(commandContext2 -> {
            if (mode == Mode.MODULE) {
                ClientServices.notificationsHelper.removeModuleAlias((Module) commandContext2.getArgument("target", Module.class));
                return 1;
            }
            ClientServices.notificationsHelper.removePlayerAlias((String) commandContext2.getArgument("target", String.class));
            return 1;
        })));
    }
}
