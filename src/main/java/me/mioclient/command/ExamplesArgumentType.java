package me.mioclient.command;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.world.phys.Vec3;

public class ExamplesArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Vec3> {
    public static final Collection<String> collection = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");

    /* 26.2 port: CoordinateArgument/Vec3ArgumentType/CommandDispatcher removed; use Vec3Argument/WorldCoordinates/Commands. */
    public Vec3 parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Coordinates coords = new Vec3Argument(false).parse(stringReader);
        Vec3 pos = Minecraft.getInstance().player.position();
        if (coords instanceof WorldCoordinates wc) {
            return new Vec3(wc.x().get(pos.x), wc.y().get(pos.y), wc.z().get(pos.z));
        }
        return pos;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (!(commandContext.getSource() instanceof SharedSuggestionProvider)) {
            return Suggestions.empty();
        }
        String remaining = suggestionsBuilder.getRemaining();
        Collection<SharedSuggestionProvider.TextCoordinates> suggested;
        if (!remaining.isEmpty() && remaining.charAt(0) == '^') {
            suggested = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
        } else {
            suggested = ((SharedSuggestionProvider) commandContext.getSource()).getAbsoluteCoordinates();
        }
        return SharedSuggestionProvider.suggestCoordinates(remaining, suggested, suggestionsBuilder, Commands.createValidator(this::parse));
    }

    public Collection<String> getExamples() {
        return collection;
    }
}
