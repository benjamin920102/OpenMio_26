package me.mioclient.manager;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.event.events.entity.TotemPopEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.mioclient.event.events.network.ConnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;

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
        ClientboundEntityEventPacket packet904 = (channelRead0Event.getPacket()) instanceof ClientboundEntityEventPacket ? (ClientboundEntityEventPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof ClientboundEntityEventPacket) {
            ClientboundEntityEventPacket entityStatusS2CPacket = packet904;
            if (entityStatusS2CPacket.getEventId() == 35) {
                Player entity = (entityStatusS2CPacket.getEntity(minecraftClient.level)) instanceof Player ? (Player) (entityStatusS2CPacket.getEntity(minecraftClient.level)) : null;
                if (entity instanceof Player) {
                    Player playerEntity = entity;
                    String name = playerEntity.getGameProfile().name();
                    this.map.compute(name, (str, num) -> {
                        return Integer.valueOf(num == null ? 1 : num.intValue() + 1);
                    });
                    eventBus.fireEvent(new TotemPopEvent(playerEntity, this.map.get(name).intValue(), TotemPopEvent.LogoutSpotsHelperMode.TOTEM_POP));
                }
            }
        }
        ClientboundPlayerInfoUpdatePacket packet9042 = (channelRead0Event.getPacket()) instanceof ClientboundPlayerInfoUpdatePacket ? (ClientboundPlayerInfoUpdatePacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof ClientboundPlayerInfoUpdatePacket) {
            for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet9042.entries()) {
                if (entry == null || entry.profile() == null) {
                    continue;
                }
                this.map2.remove(entry.profile().name());
            }
        }
        ClientboundPlayerInfoRemovePacket packet9043 = (channelRead0Event.getPacket()) instanceof ClientboundPlayerInfoRemovePacket ? (ClientboundPlayerInfoRemovePacket) (channelRead0Event.getPacket()) : null;
        if (packet9043 instanceof ClientboundPlayerInfoRemovePacket) {
            Iterator it = packet9043.profileIds().iterator();
            while (it.hasNext()) {
                PlayerInfo playerListEntry = minecraftClient.player.connection.getPlayerInfo((UUID) it.next());
                if (playerListEntry != null) {
                    this.map2.put(playerListEntry.getProfile().name(), Long.valueOf(System.currentTimeMillis()));
                }
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        for (Player playerEntity : minecraftClient.level.players()) {
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
        if (ClientServices.holeSnapRenderHelper.getServerInfo() == null || connectEvent.getServer().equals(ClientServices.holeSnapRenderHelper.getServerInfo().ip)) {
            this.map.clear();
        }
    }

    public int getPopCount(Player playerEntity) {
        return getPopCountByName(playerEntity.getGameProfile().name());
    }

    public int getPopCountByName(String str) {
        return this.map.getOrDefault(str, 0).intValue();
    }
}
