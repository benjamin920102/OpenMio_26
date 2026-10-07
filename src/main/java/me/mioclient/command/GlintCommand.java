package me.mioclient.command;

import me.mioclient.api.ClientServices;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

public class GlintCommand extends CommandFeature {
    public GlintCommand() {
        super("glint");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("clear").executes(commandContext -> {
            ClientServices.items.getGlintItems().getValue().clear();
            return 1;
        })).then(CommandFeature.argument("item", StringArgumentType.string()).suggests((commandContext2, suggestionsBuilder) -> {
            return SharedSuggestionProvider.suggest(ClientServices.items.getGlintItems().getAllValueStrings(), suggestionsBuilder);
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
