package me.mioclient.manager;

import me.mioclient.util.player.PlayerMotionPredictor;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.entity.RemoveEntityEvent;
import me.mioclient.event.events.render.RenderEvent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public final class PlayerHitboxTracker implements ClientAccessor {
    public final Map<Integer, PlayerMotionPredictor> map = Collections.synchronizedMap(new HashMap());

    public PlayerHitboxTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onRender(RenderEvent renderEvent) {
        if (isNotInGame()) {
            return;
        }
        for (AbstractClientPlayer abstractClientPlayerEntity : minecraftClient.level.players()) {
            this.map.putIfAbsent(Integer.valueOf(abstractClientPlayerEntity.getId()), new PlayerMotionPredictor(abstractClientPlayerEntity));
            this.map.get(Integer.valueOf(abstractClientPlayerEntity.getId())).recalculateLayout();
        }
    }

    @Listen
    public void onRemoveEntity(RemoveEntityEvent removeEntityEvent) {
        synchronized (this.map) {
            this.map.remove(Integer.valueOf(removeEntityEvent.getId()));
        }
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        this.map.clear();
    }

    public synchronized AABB getPredictedBox(Player playerEntity, int i) {
        if (!this.map.containsKey(Integer.valueOf(playerEntity.getId()))) {
            return playerEntity.getBoundingBox();
        }
        List<AABB> list2712 = this.map.get(Integer.valueOf(playerEntity.getId())).getList2712();
        if (list2712.isEmpty()) {
            return playerEntity.getBoundingBox();
        }
        return list2712.get(Math.min(i, list2712.size() - 1));
    }
}
