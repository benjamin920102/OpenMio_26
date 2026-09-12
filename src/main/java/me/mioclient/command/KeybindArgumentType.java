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
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import me.mioclient.mixin.ducks.DuckInputUtilType;
import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/KeybindArgumentType.class */
public class KeybindArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Keybind> {
    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public Keybind parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String trim = stringReader.readString().toLowerCase(Locale.ROOT).trim();
        if (trim.startsWith("mouse")) {
            return new Keybind(Integer.parseInt(trim.toLowerCase().replace("mouse", "")), Keybind.KeybindMode.TOGGLE, true);
        }
        return new Keybind(InputUtil.fromTranslationKey(new StringTemplate().arg(trim.replace("_", ".")).format("key.keyboard.\u0001")).getCode(), Keybind.KeybindMode.TOGGLE, false);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        ((me.mioclient.mixin.ducks.DuckInputUtilType) (Object) InputUtil.Type.KEYSYM).getKeyMap().forEach((num, key) -> {
            String upperCase = key.getTranslationKey().replaceAll("key.keyboard.", "").replace(".", "_").toUpperCase();
            if (CommandSource.shouldSuggest(suggestionsBuilder.getRemaining().toUpperCase(), upperCase)) {
                suggestionsBuilder.suggest(upperCase);
            }
        });
        for (int i = 0; i < 5; i++) {
            String string2921 = new StringTemplate().arg(i).format("MOUSE\u0001");
            if (CommandSource.shouldSuggest(suggestionsBuilder.getRemaining(), string2921)) {
                suggestionsBuilder.suggest(string2921);
            }
        }
        return suggestionsBuilder.buildFuture();
    }
}
