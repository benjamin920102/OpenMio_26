package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.ClientAccessor;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BiPredicateArgumentType<T> implements com.mojang.brigadier.arguments.ArgumentType<T>, ClientAccessor {
    public final BiPredicate<CommandContext<?>, T> biPredicate;
    public final Registry<T> registry;

    public BiPredicateArgumentType(Registry<T> registry, BiPredicate<CommandContext<?>, T> biPredicate) {
        this.biPredicate = biPredicate;
        this.registry = registry;
    }

    public T parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String readString = stringReader.readString();
        return this.registry.getOptional(Identifier.parse(readString)).orElseThrow(() -> {
            return new DynamicCommandExceptionType(obj -> {
                return Component.literal(new StringTemplate().arg(String.valueOf(obj)).format("Element not found \u0001"));
            }).create(readString);
        });
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(this.registry.keySet().stream().filter(identifier -> {
            return test(commandContext, identifier);
        }).map((v0) -> {
            return v0.toShortLanguageKey();
        }), suggestionsBuilder);
    }

    public boolean test(CommandContext<?> commandContext, Identifier identifier) {
        return this.biPredicate.test(commandContext, this.registry.getValue(identifier));
    }

    public static <T> BiPredicateArgumentType<T> registry(Registry<T> registry) {
        return new BiPredicateArgumentType<>(registry, (commandContext, obj) -> {
            return true;
        });
    }

    public static <T> BiPredicateArgumentType<T> registry(Registry<T> registry, BiPredicate<CommandContext<?>, T> biPredicate) {
        return new BiPredicateArgumentType<>(registry, biPredicate);
    }
}
