package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.module.misc.chatfilter.ChatFilterEntry;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.misc.chatfilter.ChatFilterEntry;
import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Iterator;
import me.mioclient.module.misc.ChatFilter;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import me.mioclient.module.Chat;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_34.class */
public class ChatFilterCommand extends CommandFeature {
    public static ChatFilter chatFilter = (ChatFilter) ClientServices.moduleInjector.getModule(ChatFilter.class);

    public ChatFilterCommand() {
        super("chatfilter");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("add").then(CommandFeature.argument("id", StringArgumentType.string()).then(CommandFeature.argument("filter", StringArgumentType.string()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("id", String.class);
            String str2 = (String) commandContext.getArgument("filter", String.class);
            if (ClientServices.chatFilterHelper.containsId(str)) {
                ChatUtils.sendPrefixedMessage(Text.of("Chat filter \"%s\" already exists.".formatted(str)), ChatUtils.createSignature(-3469856), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                return 1;
            }
            ClientServices.chatFilterHelper.addFilterEntry(str, str2);
            ChatUtils.sendMessage(Text.of("Chat filter \"%s\" has been added.".formatted(str)), ChatUtils.createSignature(-3469857));
            return 1;
        })))).then(CommandFeature.literal("remove").then(CommandFeature.argument("id", new ChatFilterArgumentType()).executes(commandContext2 -> {
            ChatFilterEntry data = (ChatFilterEntry) commandContext2.getArgument("id", ChatFilterEntry.class);
            ClientServices.chatFilterHelper.do2680(data);
            ChatUtils.sendMessage(Text.of("Chat filter \"%s\" has been removed.".formatted(data.getId())), ChatUtils.createSignature(-3469858));
            return 1;
        }))).then(CommandFeature.literal("list").executes(commandContext3 -> {
            ChatUtils.sendMessage(Text.of("%d chat filter(s)".formatted(Integer.valueOf(ClientServices.chatFilterHelper.get2681()))), ChatUtils.createSignature(-3569855));
            int i = -3469852;
            Iterator<String> it = ClientServices.chatFilterHelper.getList2682().iterator();
            while (it.hasNext()) {
                i++;
                ChatUtils.sendMessage(Text.of(it.next()), ChatUtils.createSignature(i));
            }
            return 1;
        }));
    }
}
