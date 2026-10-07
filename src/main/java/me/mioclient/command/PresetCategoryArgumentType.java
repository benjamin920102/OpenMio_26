package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.panel.PresetPanel;
import me.mioclient.mode.misc.PresetHelperMode;

import me.mioclient.manager.PresetFileRegistry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class PresetCategoryArgumentType implements com.mojang.brigadier.arguments.ArgumentType<PresetHelperMode> {
    public static PresetFileRegistry getManager(CommandContext<?> commandContext, String str) {
        return ClientServices.presetHelper.getPresetRegistry((PresetHelperMode) commandContext.getArgument(str, PresetHelperMode.class));
    }

    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public PresetHelperMode parse(StringReader stringReader) throws CommandSyntaxException {
        String readString = stringReader.readString();
        PresetHelperMode presetHelperMode3013 = PresetHelperMode.getByName(readString);
        if (presetHelperMode3013 == null) {
            throw new DynamicCommandExceptionType(obj -> {
                return Component.literal(new StringTemplate().arg(String.valueOf(obj)).format("Preset category not found \u0001"));
            }).create(readString);
        }
        return presetHelperMode3013;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(Arrays.stream(PresetHelperMode.values()).map(presetHelperMode -> {
            return presetHelperMode.getName().toLowerCase(Locale.ROOT);
        }), suggestionsBuilder);
    }
}
