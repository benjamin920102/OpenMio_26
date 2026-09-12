package me.mioclient.command;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.event.EventBus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/StopwatchFeature.class */
public final class StopwatchFeature extends CommandFeature {
    public final Stopwatch stopwatch;
    public boolean flag;

    public StopwatchFeature() {
        super("plugins");
        this.stopwatch = new Stopwatch();
        eventBus.register(this);
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            minecraftClient.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));
            ChatUtils.sendMessage(Text.literal("Fetching plugins..."), ChatUtils.createSignature(-3));
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
                List list = minecraftClient.player.networkHandler.getCommandDispatcher().getRoot().getChildren().stream().map((v0) -> {
                    return v0.getName();
                }).toList();
                if (list.isEmpty()) {
                    ChatUtils.sendMessage(Text.literal("Couldn't get plugins"), ChatUtils.createSignature(-3));
                } else {
                    ChatUtils.sendMessage(formatPluginList(list), ChatUtils.createSignature(-3));
                }
            }
            CommandSuggestionsS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof CommandSuggestionsS2CPacket ? (CommandSuggestionsS2CPacket) (channelRead0Event.getPacket()) : null;
            if (packet904 instanceof CommandSuggestionsS2CPacket) {
                this.flag = false;
                ChatUtils.sendMessage(formatPluginList(packet904.getSuggestions().getList().stream().map((v0) -> {
                    return v0.getText();
                }).toList()), ChatUtils.createSignature(-3));
            }
        }
    }

    public Text formatPluginList(Iterable<String> iterable) {
        HashSet<String> hashSet = new HashSet<>();
        Iterator<String> it = iterable.iterator();
        while (it.hasNext()) {
            String[] split = it.next().split(":");
            if (split.length > 1 && !split[0].isEmpty()) {
                hashSet.add(split[0]);
            }
        }
        return Text.empty().append("Found plugins [").append(Text.literal(String.valueOf(hashSet.size())).formatted(Formatting.GRAY)).append("]: ").append(Texts.join(hashSet, str -> {
            return Text.literal(str).styled(style -> {
                return ChatUtils.withDynamicColor(style, () -> {
                    return Integer.valueOf(ColorUtils.getThemeColor().hashCode());
                });
            });
        }));
    }
}
