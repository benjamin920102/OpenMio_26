package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_19.class */
public class GlintCommand extends CommandFeature {
    public GlintCommand() {
        super("glint");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("clear").executes(commandContext -> {
            ClientServices.items.getGlintItems().getValue().clear();
            return 1;
        })).then(CommandFeature.argument("item", StringArgumentType.string()).suggests((commandContext2, suggestionsBuilder) -> {
            return CommandSource.suggestMatching(ClientServices.items.getGlintItems().getAllValueStrings(), suggestionsBuilder);
        }).executes(commandContext3 -> {
            try {
                ClientServices.items.getGlintItems().loadFromString((String) commandContext3.getArgument("item", String.class));
                return 1;
            } catch (Exception e) {
                return 1;
            }
        }));
    }
}
