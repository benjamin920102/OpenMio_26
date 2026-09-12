package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryKit;

import me.mioclient.api.ClientServices;
import me.mioclient.util.inventory.InventoryKit;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChestStealerKitArgumentType.class */
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
            return Text.literal(String.format("Kit %s doesn't exist", obj));
        }).create(readString);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(ClientServices.chestStealerHelper.getRegistry().stream().map((v0) -> {
            return v0.getName();
        }), suggestionsBuilder);
    }
}
