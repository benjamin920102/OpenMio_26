package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.net.URL;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class WebhookCommand extends CommandFeature {
    public WebhookCommand() {
        super("webhook");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("get").executes(commandContext -> {
            String string1200 = ClientServices.discordNotifsHelper.getWebhookUrl();
            if (string1200 == null || string1200.isEmpty()) {
                ChatUtils.sendMessage(Component.literal("You don't have a webhook URL set"), ChatUtils.createSignature(-1035361));
                return 1;
            }
            ChatUtils.sendMessage(Component.literal("Your webhook URL is ").append(themedLiteral(string1200)), ChatUtils.createSignature(-1035361));
            return 1;
        })).then(CommandFeature.literal("set").then(CommandFeature.argument("url", StringArgumentType.greedyString()).executes(commandContext2 -> {
            String str = (String) commandContext2.getArgument("url", String.class);
            if (!str.startsWith("http://") && !str.startsWith("https://")) {
                str = new StringTemplate().arg(str).format("https://\u0001");
            }
            try {
                new URL(str);
                ClientServices.discordNotifsHelper.setWebhookUrl(str);
                ChatUtils.sendMessage(Component.literal("Your webhook URL has been updated to ").append(themedLiteral(str)), ChatUtils.createSignature(-1035362));
                return 1;
            } catch (Exception e) {
                ChatUtils.sendMessage(Component.literal("The provided webhook URL is invalid"), ChatUtils.createSignature(-1035362));
                return 1;
            }
        }))).then(CommandFeature.literal("clear").executes(commandContext3 -> {
            ClientServices.discordNotifsHelper.setWebhookUrl("");
            ChatUtils.sendMessage(Component.literal("Your webhook URL has been cleared out"), ChatUtils.createSignature(-1035363));
            return 1;
        }));
    }
}
