package me.mioclient.command;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_37.class */
public final class SayCommand extends CommandFeature {
    public SayCommand() {
        super("say");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("message", StringArgumentType.greedyString()).executes(commandContext -> {
            ChatUtils.sendChatOrCommand((String) commandContext.getArgument("message", String.class));
            return 1;
        }));
    }
}
