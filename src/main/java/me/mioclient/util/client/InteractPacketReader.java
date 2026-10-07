package me.mioclient.util.client;

import me.mioclient.api.ClientAccessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;

public class InteractPacketReader implements ClientAccessor {

    
    public enum LegacyCrystalMode {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }

    public static Entity resolvePacketEntity(ServerboundInteractPacket playerInteractEntityC2SPacket) {
        return minecraftClient.level.getEntity(playerInteractEntityC2SPacket.entityId());
    }

    public static LegacyCrystalMode getCrystalInteractionType(ServerboundInteractPacket playerInteractEntityC2SPacket) {
        if (playerInteractEntityC2SPacket.location() == null || playerInteractEntityC2SPacket.location().lengthSqr() == 0.0d) {
            return LegacyCrystalMode.INTERACT;
        }
        return LegacyCrystalMode.INTERACT_AT;
    }
}
