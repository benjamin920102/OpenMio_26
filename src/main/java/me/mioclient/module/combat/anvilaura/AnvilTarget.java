package me.mioclient.module.combat.anvilaura;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AnvilTarget.class */
public final class AnvilTarget {
    public final PlayerEntity playerEntity;
    public final AnvilAuraMode anvilAuraMode;

    @Nullable
    public final Direction direction;

    public AnvilTarget(PlayerEntity playerEntity, AnvilAuraMode anvilAuraMode, @Nullable Direction direction) {
        this.playerEntity = playerEntity;
        this.anvilAuraMode = anvilAuraMode;
        this.direction = direction;
    }




    public PlayerEntity getPlayerEntity() {
        return this.playerEntity;
    }

    public AnvilAuraMode getAuraMode() {
        return this.anvilAuraMode;
    }

    @Nullable
    public Direction getDirection() {
        return this.direction;
    }
}
