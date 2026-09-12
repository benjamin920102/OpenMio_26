package me.mioclient.module.misc;

import me.mioclient.event.events.client.ClientTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.event.events.client.ClientTickEvent;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.AddMessageEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.module.client.Discord;
import me.mioclient.module.Position;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/DiscordNotifs.class */
public class DiscordNotifs extends Module {
    public Setting<Float> timeout = add(new NumberSetting<>("Timeout", 1.0f, 1.0f, 10.0f).setSuffix("s"));
    public Setting<Boolean> timestamp = add(new BooleanSetting("Timestamp", false));
    public Setting<Boolean> queue = add(new BooleanSetting("Queue", true));
    public Setting<Boolean> disconnect = add(new BooleanSetting("Disconnect", true));
    public Setting<Boolean> dm = add(new BooleanSetting("DM", true));
    public Setting<Boolean> chatMessages = add(new BooleanSetting("ChatMessages", false));
    public Setting<Boolean> ignoreMio = add(new BooleanSetting("IgnoreMio", true));
    public final DateTimeFormatter dateTimeFormatter;
    public final List<String> list;
    public final List<String> list2;
    public final Stopwatch stopwatch;
    public String string;
    public ServerInfo serverInfo;

    public DiscordNotifs() {
        super("DiscordNotifs", "Sends chat messages to your Discord webhook.", Category.MISC, new String[0]);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        this.list = Collections.synchronizedList(new ArrayList());
        this.list2 = new ArrayList();
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.stopwatch.reset();
        if (ClientServices.discordNotifsHelper.getWebhookUrl() == null || ClientServices.discordNotifsHelper.getWebhookUrl().isEmpty()) {
            ChatUtils.sendPrefixedMessage(Text.literal("You don't have a webhook URL set. Set one by typing \"").append(new StringTemplate().arg(ClientCommandRegistry.getPrefix()).format("\u0001webhook set <url>")).append("\"."), ChatUtils.createSignature(-10395), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
        }
    }

    @Listen
    public void onClientTick(ClientTickEvent fontsEvent) {
        if (minecraftClient.world != null) {
            this.serverInfo = ClientServices.holeSnapRenderHelper.getServerInfo();
        }
        if (ClientServices.discordNotifsHelper.getWebhookUrl() == null || ClientServices.discordNotifsHelper.getWebhookUrl().isEmpty() || !this.stopwatch.hasElapsed(this.timeout.getValue().floatValue(), TimeUnit.SECONDS) || this.list.isEmpty()) {
            return;
        }
        this.stopwatch.reset();
        synchronized (this.list) {
            StringBuilder sb = new StringBuilder();
            String formatted = "[%s] ".formatted(this.dateTimeFormatter.format(LocalDateTime.now()));
            for (String str : this.list) {
                if (sb.length() > 1900) {
                    break;
                }
                if (this.timestamp.getValue().booleanValue()) {
                    sb.append(formatted);
                }
                sb.append(str);
                sb.append("\n");
                this.list2.add(str);
            }
            String sb2 = sb.toString();
            ClientServices.discordNotifsHelper.joinChannel("```%s```".formatted(sb2.substring(0, sb2.length() - 1)));
            Iterator<String> it = this.list2.iterator();
            while (it.hasNext()) {
                this.list.remove(it.next());
            }
            this.list2.clear();
        }
    }

    @Listen
    public void onAddMessage(AddMessageEvent addMessageEvent) {
        if (addMessageEvent.isCancelled() || ClientServices.discordNotifsHelper.getWebhookUrl() == null || ClientServices.discordNotifsHelper.getWebhookUrl().isEmpty() || addMessageEvent.getPhase() != KeyPearlMode.Pre || addMessageEvent.getString() == null) {
            return;
        }
        if (this.ignoreMio.getValue().booleanValue() && addMessageEvent.getIndicator() == ChatUtils.messageIndicator) {
            return;
        }
        String string2283 = addMessageEvent.getString();
        String[] split = string2283.split("\n");
        boolean isIncomingWhisper = ChatUtils.isIncomingWhisper(string2283);
        boolean isOutgoingWhisper = ChatUtils.isOutgoingWhisper(string2283);
        if (isIncomingWhisper || isOutgoingWhisper) {
            if (this.dm.getValue().booleanValue()) {
                queueMessageLines(split);
            }
        } else if (!string2283.contains("Position in queue:") || split.length <= 15) {
            if (this.chatMessages.getValue().booleanValue()) {
                queueMessageLines(split);
            }
        } else if (this.string == null) {
            this.string = string2283;
        } else if (!this.string.equals(string2283) && this.queue.getValue().booleanValue()) {
            queueMessageLines(split);
            this.string = string2283;
        }
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        if (!this.disconnect.getValue().booleanValue() || this.serverInfo == null) {
            return;
        }
        queueMessageLines("You have been disconnected from %s!".formatted(this.serverInfo.address).split("\n"));
        this.serverInfo = null;
    }

    public void queueMessageLines(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            if (!str.isEmpty()) {
                sb.append(Formatting.strip(str));
                sb.append("\n");
            }
        }
        String sb2 = sb.toString();
        if (sb2.length() < 2) {
            return;
        }
        this.list.add(sb2.substring(0, sb2.length() - 1));
    }
}
