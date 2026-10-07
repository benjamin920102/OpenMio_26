package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.misc.FriendImporterMode;
import me.mioclient.mode.render.NameTagsHelperMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.mioclient.command.EnumArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.ChatFormatting;

public final class NameTagsCommand extends CommandFeature {
    public final NameTagsHelperMode nameTagsHelperMode;

    public NameTagsCommand(NameTagsHelperMode nameTagsHelperMode) {
        super(nameTagsHelperMode.name().toLowerCase());
        this.nameTagsHelperMode = nameTagsHelperMode;
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("add").then(CommandFeature.argument("name", new ExamplesArgumentType_3()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("name", String.class);
            if (ClientServices.socialManager.getRole(str) == this.nameTagsHelperMode) {
                ChatUtils.sendMessage(themedLiteral(str).append(formatWithRole(" is already in your %s list")), ChatUtils.createSignature(-1));
                return 1;
            }
            if (this.nameTagsHelperMode == NameTagsHelperMode.ENEMY) {
                ClientServices.socialManager.addEnemy(str);
            } else {
                ClientServices.socialManager.addFriend(str);
            }
            ChatUtils.sendMessage(themedLiteral(str).append(formatWithRole(" has been added to your %s list")), ChatUtils.createSignature(-1));
            return 1;
        })));
        literalArgumentBuilder.then(CommandFeature.literalArgument("remove", "delete", "del").then(CommandFeature.argument("name", StringArgumentType.word()).suggests(this::suggestMatchingPlayers).executes(commandContext2 -> {
            String str = (String) commandContext2.getArgument("name", String.class);
            if (ClientServices.socialManager.getRole(str) != this.nameTagsHelperMode) {
                ChatUtils.sendMessage(themedLiteral(str).append(formatWithRole(" is not your %s")), ChatUtils.createSignature(-1));
                return 1;
            }
            ClientServices.socialManager.removePlayer(str);
            ChatUtils.sendMessage(themedLiteral(str).append(formatWithRole(" has been removed from your %s list")), ChatUtils.createSignature(-1));
            return 1;
        })));
        literalArgumentBuilder.then(CommandFeature.literal("list").executes(commandContext3 -> {
            List list = ClientServices.socialManager.getRegistry().stream().filter(mixinPlayerEntityData -> {
                return mixinPlayerEntityData.getHelperMode() == this.nameTagsHelperMode;
            }).map(mixinPlayerEntityData2 -> {
                return Component.literal(mixinPlayerEntityData2.getName());
            }).toList();
            ChatUtils.sendMessage(Component.literal(formatWithRole("%S list: ")).append(ComponentUtils.formatList(list, Component.literal(", "))), ChatUtils.createSignature(-1));
            return 1;
        }));
        literalArgumentBuilder.then(CommandFeature.literal("sync").then(CommandFeature.argument("importer", new EnumArgumentType(FriendImporterMode.class, "importer")).executes(commandContext4 -> {
            FriendImporterMode mode = (FriendImporterMode) commandContext4.getArgument("importer", FriendImporterMode.class);
            try {
                int i = 0;
                for (String str : mode.getImportedFriends()) {
                    if (!ClientServices.socialManager.isFriend(str)) {
                        ClientServices.socialManager.addFriend(str);
                        i++;
                    }
                }
                ChatUtils.sendMessage(Component.literal(String.format("Added %d new friends from %s", Integer.valueOf(i), mode.getImporterName())), ChatUtils.createSignature(-1));
                return 1;
            } catch (Exception e) {
                if (e instanceof FileNotFoundException) {
                    ChatUtils.sendPrefixedMessage(Component.literal(String.format("Couldn't find %s friends file", mode.getImporterName())).withStyle(style -> {
                        return style.withColor(ChatFormatting.RED);
                    }), ChatUtils.createSignature(-1), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
                    return 1;
                }
                e.printStackTrace();
                ChatUtils.sendPrefixedMessage(Component.literal(String.format("Couldn't import friends from %s due to an unknown error, check your log file", mode.getImporterName())).withStyle(style2 -> {
                    return style2.withColor(ChatFormatting.RED);
                }), ChatUtils.createSignature(-1), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
                return 1;
            }
        })));
    }

    public CompletableFuture<Suggestions> suggestMatchingPlayers(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(ClientServices.socialManager.getRegistry().stream().filter(mixinPlayerEntityData -> {
            return mixinPlayerEntityData.getHelperMode() == this.nameTagsHelperMode;
        }).map((v0) -> {
            return v0.getName();
        }), suggestionsBuilder);
    }

    public String formatWithRole(String str) {
        return str.replace("%s", this.nameTagsHelperMode.getPluralName()).replace("%S", ClientTextRenderer.capitalize(this.nameTagsHelperMode.getPluralName()));
    }
}
