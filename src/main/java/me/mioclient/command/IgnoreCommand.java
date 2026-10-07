package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public final class IgnoreCommand extends CommandFeature {
    public IgnoreCommand() {
        super("ignore");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("add").then(CommandFeature.argument("name", StringArgumentType.word()).executes(commandContext -> {
            String lowerCase = ((String) commandContext.getArgument("name", String.class)).toLowerCase();
            if (ClientServices.nameTagsManager.getIgnoredUsers().contains(lowerCase)) {
                ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(lowerCase).format("You are already ignoring \u0001")), ChatUtils.createSignature(-1));
                return 1;
            }
            ClientServices.nameTagsManager.getIgnoredUsers().add(lowerCase);
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(lowerCase).format("Player \u0001 has been ignored")), ChatUtils.createSignature(-1));
            return 1;
        }))).then(CommandFeature.literal("remove").then(CommandFeature.argument("name", StringArgumentType.word()).suggests((commandContext2, suggestionsBuilder) -> {
            return SharedSuggestionProvider.suggest(ClientServices.nameTagsManager.getIgnoredUsers(), suggestionsBuilder);
        }).executes(commandContext3 -> {
            String lowerCase = ((String) commandContext3.getArgument("name", String.class)).toLowerCase();
            if (!ClientServices.nameTagsManager.getIgnoredUsers().contains(lowerCase)) {
                ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(lowerCase).format("You are not ignoring \u0001")), ChatUtils.createSignature(-1));
                return 1;
            }
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(lowerCase).format("\u0001 is no longer ignored")), ChatUtils.createSignature(-1));
            ClientServices.nameTagsManager.getIgnoredUsers().remove(lowerCase);
            return 1;
        })));
    }
}
