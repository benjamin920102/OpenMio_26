package me.mioclient.module.combat.anvilaura;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public final class AnvilTarget {
    public final Player playerEntity;
    public final AnvilAuraMode anvilAuraMode;

    @Nullable
    public final Direction direction;

    public AnvilTarget(Player playerEntity, AnvilAuraMode anvilAuraMode, @Nullable Direction direction) {
        this.playerEntity = playerEntity;
        this.anvilAuraMode = anvilAuraMode;
        this.direction = direction;
    }

    public Player getPlayerEntity() {
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