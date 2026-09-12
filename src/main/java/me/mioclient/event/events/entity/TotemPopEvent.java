package me.mioclient.event.events.entity;

import me.mioclient.event.Event;

import net.minecraft.entity.player.PlayerEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PlayerEntityEvent.class */
public class TotemPopEvent extends Event {
    public final PlayerEntity playerEntity;
    public final int num;
    public final LogoutSpotsHelperMode logoutSpotsHelperMode;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/PlayerEntityEvent$LogoutSpotsHelperMode.class */
    public enum LogoutSpotsHelperMode {
        TOTEM_POP,
        DEATH
    }

    public TotemPopEvent(PlayerEntity playerEntity, int i, LogoutSpotsHelperMode logoutSpotsHelperMode) {
        this.playerEntity = playerEntity;
        this.num = i;
        this.logoutSpotsHelperMode = logoutSpotsHelperMode;
    }

    public PlayerEntity getPlayer() {
        return this.playerEntity;
    }

    public int getPops() {
        return this.num;
    }

    public LogoutSpotsHelperMode getType() {
        return this.logoutSpotsHelperMode;
    }

    public String getName() {
        return this.playerEntity.getGameProfile().getName();
    }
}
