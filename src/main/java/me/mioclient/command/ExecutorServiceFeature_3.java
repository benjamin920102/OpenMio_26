package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class ExecutorServiceFeature_3 extends ExecutorServiceFeature {
    public ExecutorServiceFeature_3() {
        super("playtime");
        setAliases("pt");
    }

    @Override // me.mioclient.command.ExecutorServiceFeature, me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", StringArgumentType.greedyString()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("name", String.class);
            ChatUtils.sendMessage(Component.literal("Fetching data..."), ChatUtils.signatureOf(this));
            fetchApiData("playtime", str).whenComplete((jsonObject, th) -> {
                String message;
                if (th != null) {
                    message = th.getMessage();
                } else {
                    try {
                        if (jsonObject.has("playtimeSeconds")) {
                            if (jsonObject.get("playtimeSeconds").isJsonNull()) {
                                throw new java.lang.RuntimeException("No data for player.");
                            }
                            ChatUtils.sendMessage(Component.literal(str + " has played for " + ExecutorServiceFeature.formatSecondsDuration(jsonObject.get("playtimeSeconds").getAsInt()) + "."), ChatUtils.signatureOf(this));
                            return;
                        }
                        message = "Response data doesn't have a required field: playtimeSeconds.";
                    } catch (Exception e) {
                        message = e.getMessage();
                    }
                }
                if (message != null) {
                    ChatUtils.sendPrefixedMessage(Component.literal(new StringTemplate().arg(message).arg(String.valueOf(ChatFormatting.RED)).format("\u0001\u0001")), ChatUtils.signatureOf(this), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                }
            });
            return 1;
        }));
    }
}
