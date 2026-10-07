package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;
import me.mioclient.command.ClientCommandRegistry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified): suggestion state lives in input/currentParse/
   pendingSuggestions/suggestions; refresh is now updateCommandInfo();
   show(boolean) is showSuggestions(boolean). parse results are typed on
   ClientSuggestionProvider. */
@Mixin({CommandSuggestions.class})
public abstract class MixinChatInputSuggester implements ClientAccessor {

    @Shadow
    private EditBox input;

    @Shadow
    private ParseResults<ClientSuggestionProvider> currentParse;

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    public abstract void showSuggestions(boolean z);

    @Inject(method = {"updateCommandInfo"}, at = {@At("HEAD")}, cancellable = true)
    public void refresh(CallbackInfo callbackInfo) {
        String prefix = ClientCommandRegistry.getPrefix();
        String value = this.input.getValue();
        if (value.startsWith(prefix)) {
            StringReader reader = new StringReader(value);
            reader.setCursor(prefix.length());
            CommandDispatcher commandDispatcher = ClientCommandRegistry.commandDispatcher;
            this.currentParse = commandDispatcher.parse(reader, ClientCommandRegistry.commandSource);
            int cursor = this.input.getCursorPosition();
            this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.currentParse, cursor);
            this.pendingSuggestions.thenRun(() -> {
                if (this.pendingSuggestions.isDone()) {
                    showSuggestions(false);
                }
            });
            callbackInfo.cancel();
        }
    }
}
