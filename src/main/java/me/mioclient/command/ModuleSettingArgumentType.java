package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.ClientAccessor;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ModuleSettingArgumentType implements com.mojang.brigadier.arguments.ArgumentType<String>, ClientAccessor {
    public final Module module;

    public ModuleSettingArgumentType(Module module) {
        this.module = module;
    }

    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        return stringReader.readString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(this.module.getRegistry().stream().filter(setting -> {
            return !setting.isDependent();
        }).map((v0) -> {
            return v0.getConfigName();
        }), suggestionsBuilder);
    }

    public static Setting<?> getOption(CommandContext<?> commandContext, Module module, String str) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String str2 = (String) commandContext.getArgument(str, String.class);
        Optional<Setting<?>> findFirst = module.getRegistry().stream().filter(setting -> {
            return setting.getConfigName().equalsIgnoreCase(str2);
        }).findFirst();
        if (findFirst.isEmpty()) {
            throw new DynamicCommandExceptionType(obj -> {
                return Component.literal(new StringTemplate().arg(String.valueOf(obj)).format("Setting not found \u0001"));
            }).create(str2);
        }
        return findFirst.get();
    }
}
