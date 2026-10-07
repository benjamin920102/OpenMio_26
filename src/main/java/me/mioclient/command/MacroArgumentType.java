package me.mioclient.command;

import me.mioclient.api.ClientServices;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class MacroArgumentType implements com.mojang.brigadier.arguments.ArgumentType<KeybindFeature> {
    public KeybindFeature parse(StringReader stringReader) throws CommandSyntaxException {
        String readString = stringReader.readString();
        for (KeybindFeature keybindFeature : ClientServices.macroManager.getRegistry()) {
            if (keybindFeature.getName().equalsIgnoreCase(readString)) {
                return keybindFeature;
            }
        }
        throw new DynamicCommandExceptionType(obj -> {
            return Component.literal(String.format("Macro %s doesn't exists", obj));
        }).create(readString);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(ClientServices.macroManager.getRegistry().stream().map((v0) -> {
            return v0.getName();
        }), suggestionsBuilder);
    }
}
