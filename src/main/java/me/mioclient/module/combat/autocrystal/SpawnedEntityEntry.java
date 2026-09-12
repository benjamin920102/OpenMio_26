package me.mioclient.module.combat.autocrystal;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpawnedEntityEntry.class */
public final class SpawnedEntityEntry {
    public final int num;
    public final EntityType<?> entityType;

    public SpawnedEntityEntry(int i, EntityType<?> entityType) {
        this.num = i;
        this.entityType = entityType;
    }

    public static SpawnedEntityEntry fromPacket(EntitySpawnS2CPacket entitySpawnS2CPacket) {
        return new SpawnedEntityEntry(entitySpawnS2CPacket.getEntityId(), entitySpawnS2CPacket.getEntityType());
    }




    public int getEntityId() {
        return this.num;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }
}
