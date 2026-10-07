package me.mioclient.event.events.player;

import me.mioclient.api.ClientAccessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/* 26.2 port: Yarn Entity/Vec3 -> MojMap world.entity.Entity / world.phys.Vec3. */
public final class TickPreEvent extends MoveEvent {
    public TickPreEvent(Vec3 vec3d) {
        super(vec3d, null);
    }

    public static boolean isPlayerVehicle(Object obj) {
        return ClientAccessor.minecraftClient.player != null && (obj instanceof Entity) && ClientAccessor.minecraftClient.player.getVehicle() == obj;
    }
}
