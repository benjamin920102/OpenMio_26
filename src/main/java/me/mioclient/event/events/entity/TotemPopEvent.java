package me.mioclient.event.events.entity;

import me.mioclient.event.Event;

import net.minecraft.world.entity.player.Player;

public class TotemPopEvent extends Event {
    public final Player playerEntity;
    public final int num;
    public final LogoutSpotsHelperMode logoutSpotsHelperMode;

    
    public enum LogoutSpotsHelperMode {
        TOTEM_POP,
        DEATH
    }

    public TotemPopEvent(Player playerEntity, int i, LogoutSpotsHelperMode logoutSpotsHelperMode) {
        this.playerEntity = playerEntity;
        this.num = i;
        this.logoutSpotsHelperMode = logoutSpotsHelperMode;
    }

    public Player getPlayer() {
        return this.playerEntity;
    }

    public int getPops() {
        return this.num;
    }

    public LogoutSpotsHelperMode getType() {
        return this.logoutSpotsHelperMode;
    }

    public String getName() {
        return this.playerEntity.getGameProfile().name();
    }
}
