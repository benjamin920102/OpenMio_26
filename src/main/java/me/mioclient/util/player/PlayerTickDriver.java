package me.mioclient.util.player;

import me.mioclient.api.ClientAccessor;

import me.mioclient.ducks.DuckLivingEntity;
import me.mioclient.ducks.DuckClientPlayerEntityMovement;

public class PlayerTickDriver implements ClientAccessor {
    public static boolean flag;

    public static void tickOffhand() {
        flag = true;
        int itemUseTimeLeft = minecraftClient.player.getUseItemRemainingTicks();
        float moveDist = minecraftClient.player.moveDist;
        float walkSpeed = minecraftClient.player.walkAnimation.speed();
        float walkPosition = minecraftClient.player.walkAnimation.position();
        int swingTime = minecraftClient.player.swingTime;
        float oAttackAnim = minecraftClient.player.oAttackAnim;
        float attackAnim = minecraftClient.player.attackAnim;
        ((DuckClientPlayerEntityMovement)(Object) minecraftClient.player).superTick();
        ((DuckClientPlayerEntityMovement)(Object) minecraftClient.player).resetEvent();
        ((DuckLivingEntity)(Object) minecraftClient.player).setItemUseTimeLeft(itemUseTimeLeft);
        minecraftClient.player.moveDist = moveDist;
        minecraftClient.player.walkAnimation.speed(walkSpeed);
        minecraftClient.player.walkAnimation.position(walkPosition);
        minecraftClient.player.swingTime = swingTime;
        minecraftClient.player.oAttackAnim = oAttackAnim;
        minecraftClient.player.attackAnim = attackAnim;
        ((DuckClientPlayerEntityMovement)(Object) minecraftClient.player).sendMovementPacketsWrapper();
        ((DuckClientPlayerEntityMovement)(Object) minecraftClient.player).resetRotations();
        flag = false;
    }

    public static boolean is870() {
        return flag;
    }
}
