package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.event.EventBus;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_16.class */
public final class PrefixCommand extends CommandFeature {
    public PrefixCommand() {
        super("prefix");
        eventBus.register(this);
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("prefix", StringArgumentType.greedyString()).executes(commandContext -> {
            ClientCommandRegistry.setPrefix((String) commandContext.getArgument("prefix", String.class));
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(ClientCommandRegistry.getPrefix()).arg(String.valueOf(Formatting.GRAY)).format("\u0001Prefix set to \u0001")), ChatUtils.createSignature(1));
            return 1;
        })).executes(commandContext2 -> {
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(ClientCommandRegistry.getPrefix()).arg(String.valueOf(Formatting.GRAY)).format("\u0001The current prefix is \u0001")), ChatUtils.createSignature(1));
            return 1;
        });
    }
}
