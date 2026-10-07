package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.panel.PresetPanel;
import me.mioclient.manager.PresetEntry;
import me.mioclient.mode.misc.PresetHelperMode;

import me.mioclient.api.ClientAccessor;
import me.mioclient.manager.PresetFileRegistry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class PresetArgumentType implements com.mojang.brigadier.arguments.ArgumentType<String> {
    public static PresetEntry getPreset(CommandContext<?> commandContext, String str, String str2) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        PresetHelperMode presetHelperMode = (PresetHelperMode) commandContext.getArgument(str, PresetHelperMode.class);
        String str3 = (String) commandContext.getArgument(str2, String.class);
        Optional optional2404 = ClientServices.presetHelper.getPresetRegistry(presetHelperMode).findFirst(presetSearchHelper4 -> {
            return presetSearchHelper4.getName().equalsIgnoreCase(str3);
        });
        if (optional2404.isEmpty()) {
            throw new DynamicCommandExceptionType(obj -> {
                return Component.literal(new StringTemplate().arg(String.valueOf(obj)).format("Preset not found \u0001"));
            }).create(str3);
        }
        return (PresetEntry) optional2404.get();
    }

    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public String parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return stringReader.readString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return CompletableFuture.supplyAsync(() -> {
            PresetFileRegistry presetHelperSearchHelper4_273 = ClientServices.presetHelper.getPresetRegistry((PresetHelperMode) commandContext.getArgument("category", PresetHelperMode.class));
            try {
                presetHelperSearchHelper4_273.do34();
            } catch (Throwable th) {
            }
            try {
                return (Suggestions) SharedSuggestionProvider.suggest(presetHelperSearchHelper4_273.getRegistry().stream().map((v0) -> {
                    return v0.getName();
                }), suggestionsBuilder).get();
            } catch (Exception e) {
                return null;
            }
        }, ClientAccessor.executorService);
    }
}
