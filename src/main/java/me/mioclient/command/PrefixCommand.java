package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.event.EventBus;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public final class PrefixCommand extends CommandFeature {
    public PrefixCommand() {
        super("prefix");
        eventBus.register(this);
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("prefix", StringArgumentType.greedyString()).executes(commandContext -> {
            ClientCommandRegistry.setPrefix((String) commandContext.getArgument("prefix", String.class));
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(ClientCommandRegistry.getPrefix()).arg(String.valueOf(ChatFormatting.GRAY)).format("\u0001Prefix set to \u0001")), ChatUtils.createSignature(1));
            return 1;
        })).executes(commandContext2 -> {
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(ClientCommandRegistry.getPrefix()).arg(String.valueOf(ChatFormatting.GRAY)).format("\u0001The current prefix is \u0001")), ChatUtils.createSignature(1));
            return 1;
        });
    }
}
