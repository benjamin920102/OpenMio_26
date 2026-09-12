package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.module.misc.chatfilter.ChatFilterEntry;

import me.mioclient.api.ClientServices;
import me.mioclient.module.misc.chatfilter.ChatFilterEntry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChatFilterArgumentType.class */
public class ChatFilterArgumentType implements com.mojang.brigadier.arguments.ArgumentType<ChatFilterEntry> {
    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public ChatFilterEntry parse(StringReader stringReader) throws CommandSyntaxException {
        String readString = stringReader.readString();
        Optional<ChatFilterEntry> optional2679 = ClientServices.chatFilterHelper.getOptional2679(readString);
        if (optional2679.isEmpty()) {
            throw new DynamicCommandExceptionType(obj -> {
                return Text.of(new StringTemplate().arg(String.valueOf(obj)).format("Filter not found \u0001"));
            }).create(readString);
        }
        return optional2679.get();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(ClientServices.chatFilterHelper.getStream2678(), suggestionsBuilder);
    }
}
