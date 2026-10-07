package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class ExecutorServiceFeature_4 extends ExecutorServiceFeature {
    public ExecutorServiceFeature_4() {
        super("queue");
    }

    @Override // me.mioclient.command.ExecutorServiceFeature, me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            ChatUtils.sendMessage(Component.literal("Fetching data..."), ChatUtils.signatureOf(this));
            fetchApiData("queue", (String) null).whenComplete((jsonObject, th) -> {
                String message;
                if (th != null) {
                    message = th.getMessage();
                } else {
                    try {
                        int i = -1337;
                        int i2 = -1337;
                        if (jsonObject.has("prio")) {
                            i = jsonObject.get("prio").getAsInt();
                        }
                        if (jsonObject.has("regular")) {
                            i2 = jsonObject.get("regular").getAsInt();
                        }
                        StringBuilder sb = new StringBuilder();
                        if (i2 != -1337) {
                            sb.append("%d in normal queue".formatted(Integer.valueOf(i2)));
                        }
                        if (i2 != -1337 && i != -1337) {
                            sb.append(", ");
                        }
                        if (i != -1337) {
                            sb.append("%d in priority queue".formatted(Integer.valueOf(i)));
                        }
                        ChatUtils.sendMessage(Component.literal(sb.toString()), ChatUtils.signatureOf(this));
                        return;
                    } catch (Exception e) {
                        message = e.getMessage();
                    }
                }
                if (message != null) {
                    ChatUtils.sendPrefixedMessage(Component.literal(new StringTemplate().arg(message).arg(String.valueOf(ChatFormatting.RED)).format("\u0001\u0001")), ChatUtils.signatureOf(this), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                }
            });
            return 1;
        });
    }
}
