package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import me.mioclient.module.Feature;

public abstract class CommandFeature extends me.mioclient.module.Feature {
    public String[] aliases;

    public CommandFeature(String str) {
        super(str);
        this.aliases = new String[0];
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String str, com.mojang.brigadier.arguments.ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(str, argumentType);
    }

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literal(String str) {
        return com.mojang.brigadier.builder.LiteralArgumentBuilder.literal(str);
    }

    public static RequiredArgumentBuilder<CommandSourceStack, String> literalArgument(String... strArr) {
        return argument(strArr[0], new LiteralArgumentType(strArr));
    }

    public static CompletableFuture<Suggestions> buildSuggestions(SuggestionsBuilder suggestionsBuilder, String... strArr) {
        for (String str : strArr) {
            suggestionsBuilder.suggest(str);
        }
        return suggestionsBuilder.buildFuture();
    }

    public void runLater(java.lang.Runnable runnable) {
        ClientServices.tooltipsHelper.schedule(runnable, 0);
    }

    public MutableComponent themedLiteral(String str) {
        return Component.empty().append(ChatUtils.themedLiteral(str));
    }

    public void setAliases(String... strArr) {
        this.aliases = strArr;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public abstract void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    public void addSubCommand(CommandFeature feature, com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        ArgumentBuilder literal = literal(feature.getName());
        feature.exec((com.mojang.brigadier.builder.LiteralArgumentBuilder) literal);
        literalArgumentBuilder.then(literal);
    }
}
