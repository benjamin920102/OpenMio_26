package me.mioclient.command;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.event.EventBus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import me.mioclient.event.Listen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.ChatFormatting;

public final class StopwatchFeature extends CommandFeature {
    public final Stopwatch stopwatch;
    public boolean flag;

    public StopwatchFeature() {
        super("plugins");
        this.stopwatch = new Stopwatch();
        eventBus.register(this);
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            minecraftClient.player.connection.send(new ServerboundCommandSuggestionPacket(0, "/"));
            ChatUtils.sendMessage(Component.literal("Fetching plugins..."), ChatUtils.createSignature(-3));
            this.stopwatch.reset();
            this.flag = true;
            return 1;
        });
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (this.flag) {
            if (this.stopwatch.hasElapsedMs(3000L)) {
                this.flag = false;
                List<String> list = minecraftClient.player.connection.getCommands().getRoot().getChildren().stream().map((v0) -> {
                    return ((com.mojang.brigadier.tree.CommandNode<?>) v0).getName();
                }).toList();
                if (list.isEmpty()) {
                    ChatUtils.sendMessage(Component.literal("Couldn't get plugins"), ChatUtils.createSignature(-3));
                } else {
                    ChatUtils.sendMessage(formatPluginList(list), ChatUtils.createSignature(-3));
                }
            }
            if (channelRead0Event.getPacket() instanceof ClientboundCommandSuggestionsPacket suggestionPacket) {
                this.flag = false;
                ChatUtils.sendMessage(formatPluginList(suggestionPacket.suggestions().stream().map(ClientboundCommandSuggestionsPacket.Entry::text).toList()), ChatUtils.createSignature(-3));
            }
        }
    }

    public Component formatPluginList(Iterable<String> iterable) {
        HashSet<String> hashSet = new HashSet<>();
        Iterator<String> it = iterable.iterator();
        while (it.hasNext()) {
            String[] split = it.next().split(":");
            if (split.length > 1 && !split[0].isEmpty()) {
                hashSet.add(split[0]);
            }
        }
        return Component.empty().append("Found plugins [").append(Component.literal(String.valueOf(hashSet.size())).withStyle(ChatFormatting.GRAY)).append("]: ").append(ComponentUtils.formatList(hashSet, str -> {
            return Component.literal(str).withStyle(style -> {
                return ChatUtils.withDynamicColor(style, () -> {
                    return Integer.valueOf(ColorUtils.getThemeColor().hashCode());
                });
            });
        }));
    }
}
