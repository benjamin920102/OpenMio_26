package me.mioclient.manager;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.event.events.entity.TotemPopEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.event.EventBus;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.mioclient.event.events.entity.TotemPopEvent;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ConnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/TotemPopTracker.class */
public final class TotemPopTracker implements ClientAccessor {
    public final Map<String, Integer> map = new ConcurrentHashMap();
    public final Map<String, Long> map2 = new ConcurrentHashMap();

    public TotemPopTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (isNotInGame()) {
            return;
        }
        EntityStatusS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof EntityStatusS2CPacket ? (EntityStatusS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof EntityStatusS2CPacket) {
            EntityStatusS2CPacket entityStatusS2CPacket = packet904;
            if (entityStatusS2CPacket.getStatus() == 35) {
                PlayerEntity entity = (entityStatusS2CPacket.getEntity(minecraftClient.world)) instanceof PlayerEntity ? (PlayerEntity) (entityStatusS2CPacket.getEntity(minecraftClient.world)) : null;
                if (entity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = entity;
                    String name = playerEntity.getGameProfile().getName();
                    this.map.compute(name, (str, num) -> {
                        return Integer.valueOf(num == null ? 1 : num.intValue() + 1);
                    });
                    eventBus.fireEvent(new TotemPopEvent(playerEntity, this.map.get(name).intValue(), TotemPopEvent.LogoutSpotsHelperMode.TOTEM_POP));
                }
            }
        }
        PlayerListS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof PlayerListS2CPacket ? (PlayerListS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof PlayerListS2CPacket) {
            for (PlayerListS2CPacket.Entry entry : packet9042.getPlayerAdditionEntries()) {
                this.map2.remove(entry.profile().getName());
            }
        }
        PlayerRemoveS2CPacket packet9043 = (channelRead0Event.getPacket()) instanceof PlayerRemoveS2CPacket ? (PlayerRemoveS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9043 instanceof PlayerRemoveS2CPacket) {
            Iterator it = packet9043.profileIds().iterator();
            while (it.hasNext()) {
                PlayerListEntry playerListEntry = minecraftClient.player.networkHandler.getPlayerListEntry((UUID) it.next());
                if (playerListEntry != null) {
                    this.map2.put(playerListEntry.getProfile().getName(), Long.valueOf(System.currentTimeMillis()));
                }
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        for (PlayerEntity playerEntity : minecraftClient.world.getPlayers()) {
            if (playerEntity != null && EntityUtil.getEntityHealth((Entity) playerEntity) <= 0.0f) {
                String string = playerEntity.getName().getString();
                if (this.map.containsKey(string)) {
                    int intValue = this.map.get(string).intValue();
                    this.map.remove(string);
                    eventBus.fireEvent(new TotemPopEvent(playerEntity, intValue, TotemPopEvent.LogoutSpotsHelperMode.DEATH));
                }
            }
        }
        String str = null;
        for (Map.Entry<String, Long> entry : this.map2.entrySet()) {
            if (entry.getValue().longValue() + 120000 < System.currentTimeMillis()) {
                str = entry.getKey();
                this.map.remove(entry.getKey());
            }
        }
        if (str != null) {
            this.map2.remove(str);
        }
    }

    @Listen
    public void onConnect(ConnectEvent connectEvent) {
        if (ClientServices.holeSnapRenderHelper.getServerInfo() == null || connectEvent.getServer().equals(ClientServices.holeSnapRenderHelper.getServerInfo().address)) {
            this.map.clear();
        }
    }

    public int getPopCount(PlayerEntity playerEntity) {
        return getPopCountByName(playerEntity.getGameProfile().getName());
    }

    public int getPopCountByName(String str) {
        return this.map.getOrDefault(str, 0).intValue();
    }
}
