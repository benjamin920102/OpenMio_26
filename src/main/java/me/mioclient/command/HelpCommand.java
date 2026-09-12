package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import java.io.File;
import java.util.ArrayList;
import me.mioclient.module.client.UI;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_28.class */
public final class HelpCommand extends CommandFeature {
    public HelpCommand() {
        super("help");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        if (!new File("mio-fabric").exists()) {
            ClientServices.tooltipsHelper.schedule(() -> {
                ChatUtils.sendMessage(Text.literal("Welcome to Mio!"), ChatUtils.createSignature(-1001));
                ChatUtils.sendMessage(Text.literal("The UI bind is ").append(themedLiteral(UI.uI.getKeybind().getBindString())), ChatUtils.createSignature(-1002));
                ChatUtils.sendMessage(Text.literal("The command prefix is ").append(themedLiteral(ClientCommandRegistry.getPrefix())), ChatUtils.createSignature(-1003));
            }, 0);
        }
        literalArgumentBuilder.executes(commandContext -> {
            ArrayList arrayList = new ArrayList();
            ClientServices.chatFilterChatHelper.getRegistry().forEach(feature -> {
                arrayList.add(feature.getName());
            });
            Text empty = Text.empty();
            ((MutableText) empty).append("Commands (%d): ".formatted(Integer.valueOf(arrayList.size())));
            ((MutableText) empty).append(Text.of(new StringTemplate().arg(String.join(", ", arrayList)).format("\u0001.")));
            ChatUtils.sendMessage(empty, ChatUtils.createSignature(-1));
            return 1;
        });
    }
}
