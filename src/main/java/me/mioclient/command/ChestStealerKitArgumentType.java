package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryKit;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ChestStealerKitArgumentType implements com.mojang.brigadier.arguments.ArgumentType<InventoryKit> {
    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public InventoryKit parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String readString = stringReader.readString();
        for (InventoryKit chestStealerEnumSettingHelper : ClientServices.chestStealerHelper.getRegistry()) {
            if (chestStealerEnumSettingHelper.getName().equalsIgnoreCase(readString)) {
                return chestStealerEnumSettingHelper;
            }
        }
        throw new DynamicCommandExceptionType(obj -> {
            return Component.literal(String.format("Kit %s doesn't exist", obj));
        }).create(readString);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(ClientServices.chestStealerHelper.getRegistry().stream().map((v0) -> {
            return v0.getName();
        }), suggestionsBuilder);
    }
}
