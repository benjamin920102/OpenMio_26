package me.mioclient.module.combat.anchoraura;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import me.mioclient.ducks.DuckLivingEntityServerDead;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AnchorTarget.class */
public final class AnchorTarget implements ClientAccessor {
    public final PlayerEntity playerEntity;
    public final double val;

    public AnchorTarget(PlayerEntity playerEntity, double d) {
        this.playerEntity = playerEntity;
        this.val = d;
    }

    public static boolean isInvalidTarget(PlayerEntity playerEntity, double d) {
        return playerEntity == null || playerEntity == minecraftClient.player || !playerEntity.isAlive() || ClientServices.socialManager.isFriendEntity(playerEntity) || ((DuckLivingEntityServerDead) playerEntity).isServerSideDead() || playerEntity.isSpectator() || playerEntity.getAbilities().creativeMode || ((double) minecraftClient.player.distanceTo((Entity) playerEntity)) > d;
    }




    public PlayerEntity getAnchorPlayer() {
        return this.playerEntity;
    }

    public double getDamage() {
        return this.val;
    }
}
