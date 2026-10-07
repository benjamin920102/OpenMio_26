package me.mioclient.mixin.ducks;

import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): SuggestionWindow is now SuggestionsList; selection is
   current; typed text is originalContents. */
@Mixin({CommandSuggestions.SuggestionsList.class})
public interface DuckSuggestionWindow {
    @Accessor("current")
    int getSelection();

    @Accessor("originalContents")
    String getTypedText();
}
