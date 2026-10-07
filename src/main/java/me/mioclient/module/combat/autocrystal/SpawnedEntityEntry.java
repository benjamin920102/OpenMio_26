package me.mioclient.module.combat.autocrystal;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;

public final class SpawnedEntityEntry {
    public final int num;
    public final EntityType<?> entityType;

    public SpawnedEntityEntry(int i, EntityType<?> entityType) {
        this.num = i;
        this.entityType = entityType;
    }

    public static SpawnedEntityEntry fromPacket(ClientboundAddEntityPacket entitySpawnS2CPacket) {
        return new SpawnedEntityEntry(entitySpawnS2CPacket.getId(), entitySpawnS2CPacket.getType());
    }

    public int getEntityId() {
        return this.num;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }
}