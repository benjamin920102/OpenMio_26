package me.mioclient.manager;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.event.EventBus;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ConnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.exploit.FastLatency;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import me.mioclient.render.RenderConstants;
import me.mioclient.render.RenderConstants;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ServerStateTracker.class */
public final class ServerStateTracker implements ClientAccessor {
    public static final FastLatency fastLatency = (FastLatency) ClientServices.moduleInjector.getModule(FastLatency.class);
    public static final Pattern pattern = Pattern.compile("\\d+ ping");
    public final ArrayDeque<Float> arrayDeque = new ArrayDeque<>(20);
    public final Stopwatch stopwatch = new Stopwatch();
    public final Stopwatch stopwatch2 = new Stopwatch();
    public float val;
    public float val2;
    public long num;
    public long num2;
    public int num3;
    public boolean flag;
    public boolean flag2;
    public ServerInfo serverInfo;
    public int num4;
    public boolean flag3;

    public ServerStateTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        this.num = System.currentTimeMillis();
        PlayerPositionLookS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof PlayerPositionLookS2CPacket ? (PlayerPositionLookS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof PlayerPositionLookS2CPacket) {
            this.num3 = packet904.getTeleportId();
            if (this.stopwatch2.hasElapsedMs(50L)) {
                this.stopwatch.reset();
            }
            this.flag = minecraftClient.player.isSprinting();
        }
        if (channelRead0Event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            if (this.num2 != 0) {
                if (this.arrayDeque.size() > 20) {
                    this.arrayDeque.poll();
                }
                this.val = Math.max(0.0f, Math.min(Float.intBitsToFloat(1101004800), Float.intBitsToFloat(1101004800) * (Float.intBitsToFloat(1148846080) / ((float) (System.currentTimeMillis() - this.num2)))));
                this.arrayDeque.add(Float.valueOf(this.val));
                float f = 0.0f;
                Iterator<Float> it = this.arrayDeque.iterator();
                while (it.hasNext()) {
                    f += Math.max(0.0f, Math.min(Float.intBitsToFloat(1101004800), it.next().floatValue()));
                }
                if (this.arrayDeque.size() > 0) {
                    f /= this.arrayDeque.size();
                }
                this.val2 = f;
            }
            this.num2 = System.currentTimeMillis();
        }
        BundleS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof BundleS2CPacket ? (BundleS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof BundleS2CPacket) {
            Iterator it2 = packet9042.getPackets().iterator();
            while (it2.hasNext()) {
                eventBus.fireEvent(new PacketReceiveEvent((Packet) it2.next()));
            }
        }
        PlayerListHeaderS2CPacket packet9043 = (channelRead0Event.getPacket()) instanceof PlayerListHeaderS2CPacket ? (PlayerListHeaderS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9043 instanceof PlayerListHeaderS2CPacket) {
            PlayerListHeaderS2CPacket playerListHeaderS2CPacket = packet9043;
            if (minecraftClient.player.networkHandler.getServerInfo() == null) {
                return;
            }
            Matcher matcher = pattern.matcher(playerListHeaderS2CPacket.footer().getString());
            if (matcher.find()) {
                String group = matcher.group();
                try {
                    this.num4 = Integer.parseInt(group.substring(0, group.length() - 5));
                } catch (Throwable th) {
                }
            }
        }
    }

    @Listen(get219= RenderConstants.num)
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        ClientCommandC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof ClientCommandC2SPacket ? (ClientCommandC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket clientCommandC2SPacket = packet904;
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                if (this.flag) {
                    sendImmediatelyEvent.cancel();
                }
                this.flag = true;
            }
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                if (!this.flag) {
                    sendImmediatelyEvent.cancel();
                }
                this.flag = false;
            }
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                this.flag2 = true;
            }
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY) {
                this.flag2 = false;
            }
        }
        if (sendImmediatelyEvent.getPacket() instanceof TeleportConfirmC2SPacket) {
            this.stopwatch2.reset();
        }
    }

    @Listen
    public void onConnect(ConnectEvent connectEvent) {
        this.flag3 = false;
        this.num4 = 0;
    }

    public int getPing() {
        if (minecraftClient.player == null) {
            return 0;
        }
        if (fastLatency.isToggled()) {
            return fastLatency.getPing();
        }
        PlayerListEntry playerListEntry = minecraftClient.player.networkHandler.getPlayerListEntry(minecraftClient.player.getGameProfile().getId());
        int latency = playerListEntry == null ? 0 : playerListEntry.getLatency();
        return latency == 0 ? this.num4 : latency;
    }

    public Stopwatch getSetbackStopwatch() {
        return this.stopwatch;
    }

    public Stopwatch getTeleportStopwatch() {
        return this.stopwatch2;
    }

    public int getPendingTeleportId() {
        return this.num3;
    }

    public long getLastPacketTime() {
        return this.num;
    }

    public float getCurrentTickRate() {
        return this.val;
    }

    public float getSmoothedTickRate() {
        return this.val2;
    }

    public float getTickRateFactor() {
        return this.val2 / Float.intBitsToFloat(1101004800);
    }

    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public boolean isSprinting() {
        return this.flag;
    }

    public boolean isSneaking() {
        return this.flag2;
    }
}
