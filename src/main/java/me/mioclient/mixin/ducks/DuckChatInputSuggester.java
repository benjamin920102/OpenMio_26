package me.mioclient.mixin.ducks;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): window is now suggestions (SuggestionsList); parse is
   now currentParse typed on ClientSuggestionProvider. */
@Mixin({CommandSuggestions.class})
public interface DuckChatInputSuggester {
    @Accessor("pendingSuggestions")
    CompletableFuture<Suggestions> getSuggestion();

    @Accessor("suggestions")
    CommandSuggestions.SuggestionsList getSuggestions();

    @Accessor("currentParse")
    ParseResults<ClientSuggestionProvider> getParse();
}
