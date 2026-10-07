package me.mioclient.module.combat.anchoraura;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import me.mioclient.ducks.DuckLivingEntityServerDead;

public final class AnchorTarget implements ClientAccessor {
    public final Player playerEntity;
    public final double val;

    public AnchorTarget(Player playerEntity, double d) {
        this.playerEntity = playerEntity;
        this.val = d;
    }

    public static boolean isInvalidTarget(Player playerEntity, double d) {
        return playerEntity == null || playerEntity == minecraftClient.player || !playerEntity.isAlive() || ClientServices.socialManager.isFriendEntity(playerEntity) || ((DuckLivingEntityServerDead) playerEntity).isServerSideDead() || playerEntity.isSpectator() || playerEntity.isCreative() || ((double) minecraftClient.player.distanceTo((Entity) playerEntity)) > d;
    }

    public Player getAnchorPlayer() {
        return this.playerEntity;
    }

    public double getDamage() {
        return this.val;
    }
}