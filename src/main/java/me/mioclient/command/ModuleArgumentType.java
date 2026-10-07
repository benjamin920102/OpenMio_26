package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import me.mioclient.module.Module;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ModuleArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Module> {
    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public Module parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String readString = stringReader.readString();
        Optional optional2404 = ClientServices.moduleRegistry.findFirst(module -> {
            for (String str : module.getAliases()) {
                if (str.equalsIgnoreCase(readString)) {
                    return true;
                }
            }
            return false;
        });
        if (optional2404.isEmpty()) {
            throw new DynamicCommandExceptionType(obj -> {
                return Component.literal(new StringTemplate().arg(String.valueOf(obj)).format("Module not found \u0001"));
            }).create(readString);
        }
        return (Module) optional2404.get();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(ClientServices.moduleRegistry.getRegistry().stream().map((v0) -> {
            return v0.getName();
        }), suggestionsBuilder);
    }
}
