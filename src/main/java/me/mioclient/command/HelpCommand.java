package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import java.io.File;
import java.util.ArrayList;
import me.mioclient.module.client.UI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public final class HelpCommand extends CommandFeature {
    public HelpCommand() {
        super("help");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        if (!new File("mio-fabric").exists()) {
            ClientServices.tooltipsHelper.schedule(() -> {
                ChatUtils.sendMessage(Component.literal("Welcome to Mio!"), ChatUtils.createSignature(-1001));
                ChatUtils.sendMessage(Component.literal("The UI bind is ").append(themedLiteral(UI.uI.getKeybind().getBindString())), ChatUtils.createSignature(-1002));
                ChatUtils.sendMessage(Component.literal("The command prefix is ").append(themedLiteral(ClientCommandRegistry.getPrefix())), ChatUtils.createSignature(-1003));
            }, 0);
        }
        literalArgumentBuilder.executes(commandContext -> {
            ArrayList arrayList = new ArrayList();
            ClientServices.chatFilterChatHelper.getRegistry().forEach(feature -> {
                arrayList.add(feature.getName());
            });
            Component empty = Component.empty();
            ((MutableComponent) empty).append("Commands (%d): ".formatted(Integer.valueOf(arrayList.size())));
            ((MutableComponent) empty).append(Component.literal(new StringTemplate().arg(String.join(", ", arrayList)).format("\u0001.")));
            ChatUtils.sendMessage(empty, ChatUtils.createSignature(-1));
            return 1;
        });
    }
}
