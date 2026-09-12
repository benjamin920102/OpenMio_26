package me.mioclient.command;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_20.class */
public final class PrintCommand extends CommandFeature {
    public PrintCommand() {
        super("print");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("message", StringArgumentType.greedyString()).executes(commandContext -> {
            minecraftClient.inGameHud.getChatHud().addMessage(Text.of((String) commandContext.getArgument("message", String.class)), ChatUtils.createSignature((int) (Math.random() * Double.longBitsToDouble(4666723172467343360L))), ChatUtils.messageIndicator);
            return 1;
        })).executes(commandContext2 -> {
            ChatUtils.sendPrefixedMessage(Text.literal("Please enter a message to print."), ChatUtils.createSignature(-1), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
            return 1;
        });
    }
}
