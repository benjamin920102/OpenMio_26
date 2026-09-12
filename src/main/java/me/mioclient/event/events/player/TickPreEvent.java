package me.mioclient.event.events.player;

import me.mioclient.api.ClientAccessor;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/TickPreEvent.class */
public final class TickPreEvent extends MoveEvent {
    public TickPreEvent(Vec3d vec3d) {
        super(vec3d, null);
    }

    public static boolean isPlayerVehicle(Object obj) {
        return ClientAccessor.minecraftClient.player != null && (obj instanceof Entity) && ClientAccessor.minecraftClient.player.getVehicle() == obj;
    }
}
