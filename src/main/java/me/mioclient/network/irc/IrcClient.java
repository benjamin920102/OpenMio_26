package me.mioclient.network.irc;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.network.codec.ChannelInitializer;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.network.packet.ChannelJoinPacket;
import me.mioclient.network.packet.FriendAddPacket;
import me.mioclient.network.packet.FriendRemovePacket;
import me.mioclient.network.packet.KeepAlivePacket;
import me.mioclient.network.packet.PlayerPositionPacket;

import me.mioclient.event.EventBus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.client.IRC;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

public class IrcClient implements ClientAccessor, JsonConfigurable {
    public String[] stringArr;
    public final Bootstrap bootstrap;
    public Channel channel;
    public boolean flag;
    public long num3;
    public static final int num2 = 65536;
    public static final int num = 48002;
    public static final String string = "auth.me.mioclient.me";
    public CompletableFuture<?> completableFuture = CompletableFuture.completedFuture(null);
    public final Set<String> set = new HashSet();
    public final Map<String, String> map = new ConcurrentHashMap();
    public final List<CoordLogEntry> list = new ArrayList();
    public final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    public IrcClient() {
        eventBus.register(this);
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer());
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (!this.flag || isConnected() || this.num3 + 10000 >= System.currentTimeMillis()) {
            return;
        }
        this.flag = false;
        connectToServer();
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        Iterator<String> it = this.set.iterator();
        while (it.hasNext()) {
            jsonArray.add(it.next());
        }
        jsonObject.add("ignore", (JsonElement) jsonArray);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        if (asJsonObject.has("ignore")) {
            Iterator it = asJsonObject.getAsJsonArray("ignore").iterator();
            while (it.hasNext()) {
                JsonElement jsonElement2 = (JsonElement) it.next();
                this.set.add(jsonElement2.getAsString());
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "irc.json";
    }

    public void connectToServer() {
        if (this.completableFuture.isDone()) {
            this.completableFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    this.channel = this.bootstrap.connect("auth.me.mioclient.me", 48002).sync().channel();
                } catch (InterruptedException e) {
                }
                return this.channel;
            }, executorService);
        }
    }

    public void disconnectFromServer() {
        if (isConnected()) {
            if (this.completableFuture.isDone()) {
                this.channel.disconnect();
            } else {
                this.completableFuture.thenAccept(obj -> {
                    disconnectFromServer();
                });
            }
        }
        this.channel = null;
    }

    public void crashPlayer(String str) {
        if (isConnected()) {
            this.channel.writeAndFlush(new FriendRemovePacket("crash", str));
        }
    }

    public void banPlayer(String str) {
        if (isConnected()) {
            this.channel.writeAndFlush(new FriendRemovePacket("ban", str));
        }
    }

    public void unbanPlayer(String str) {
        if (isConnected()) {
            this.channel.writeAndFlush(new FriendRemovePacket("unban", str));
        }
    }

    public void requestPlayerList() {
        if (isConnected()) {
            this.channel.writeAndFlush(new FriendRemovePacket("players", "asd"));
        }
    }

    public void joinChannel(String str) {
        if (!isConnected()) {
            showNotConnectedMessage();
            return;
        }
        this.channel.writeAndFlush(new ChannelJoinPacket(str));
    }

    public void sendKeepAlive() {
        if (!isConnected()) {
            showNotConnectedMessage();
            return;
        }
        this.channel.writeAndFlush(new KeepAlivePacket());
    }

    public void sendPlayerPosition(String str, String str2, BlockPos blockPos) {
        if (!isConnected()) {
            showNotConnectedMessage();
            return;
        }
        this.channel.writeAndFlush(new PlayerPositionPacket(str, str2, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    public void addFriend(String str) {
        if (!isConnected()) {
            showNotConnectedMessage();
            return;
        }
        this.channel.writeAndFlush(new FriendAddPacket(str, minecraftClient.getUser().getName()));
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isActive();
    }

    public void showNotConnectedMessage() {
        try {
            ChatUtils.sendPrefixedMessage(Component.literal(new StringTemplate().arg(String.valueOf(ChatFormatting.RED)).format("\u0001You are not connected to the chat server")), ChatUtils.createSignature(12482345), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
        } catch (Exception e) {
        }
    }

    public List<CoordLogEntry> getCoordLogEntries() {
        return this.list;
    }

    public Map<String, String> getUserModes() {
        return this.map;
    }

    public Identifier getModeIdentifier(String str) {
        for (IRC.Mode mode : IRC.Mode.values()) {
            if (mode.getName().equalsIgnoreCase(this.map.get(str))) {
                return mode.getIdentifier2642();
            }
        }
        return null;
    }

    public String[] getSharedSecret() {
        return this.stringArr;
    }

    public void setSharedSecret(String[] strArr) {
        this.stringArr = strArr;
    }

    public Set<String> getIgnoredUsers() {
        return this.set;
    }

    public void setConnectionState(boolean z) {
        if (z && !this.flag) {
            String str = "Lost connection to the chat server. Retrying in 10 seconds";
            System.err.println("Lost connection to the chat server. Retrying in 10 seconds");
            ClientServices.tooltipsHelper.schedule(() -> {
                ChatUtils.sendPrefixedMessage(Component.literal(new StringTemplate().arg(str).arg(String.valueOf(ChatFormatting.RED)).format("\u0001\u0001")), ChatUtils.createSignature(8345486), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
            }, 0);
        }
        this.flag = z;
        this.num3 = System.currentTimeMillis();
    }
}
