package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.misc.KeybindMode;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import me.mioclient.api.Keybind;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

public class KeybindArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Keybind> {
    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public Keybind parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String trim = stringReader.readString().toLowerCase(Locale.ROOT).trim();
        if (trim.startsWith("mouse")) {
            return new Keybind(Integer.parseInt(trim.toLowerCase().replace("mouse", "")), Keybind.KeybindMode.TOGGLE, true);
        }
        // 26.2: InputConstants#getKey(String) resolves "key.keyboard.<name>" translation keys.
        return new Keybind(InputConstants.getKey(new StringTemplate().arg(trim.replace("_", ".")).format("key.keyboard.\u0001")).getValue(), Keybind.KeybindMode.TOGGLE, false);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        // 26.2 port (verified): InputConstants.Type.KEYSYM map exposed via DuckInputConstantsType.
        ((me.mioclient.mixin.ducks.DuckInputConstantsType) (Object) InputConstants.Type.KEYSYM).mio$getKeyMap().forEach((num, key) -> {
            String upperCase = key.getName().replaceAll("key.keyboard.", "").replace(".", "_").toUpperCase(Locale.ROOT);
            if (SharedSuggestionProvider.matchesSubStr(suggestionsBuilder.getRemaining().toUpperCase(Locale.ROOT), upperCase)) {
                suggestionsBuilder.suggest(upperCase);
            }
        });
        for (int i = 0; i < 5; i++) {
            String string2921 = new StringTemplate().arg(i).format("MOUSE\u0001");
            if (SharedSuggestionProvider.matchesSubStr(suggestionsBuilder.getRemaining(), string2921)) {
                suggestionsBuilder.suggest(string2921);
            }
        }
        return suggestionsBuilder.buildFuture();
    }
}
