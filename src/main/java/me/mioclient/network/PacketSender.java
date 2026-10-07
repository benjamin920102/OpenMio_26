package me.mioclient.network;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PacketSender implements ClientAccessor {
    public static void sendInteractBlock(InteractionHand hand, BlockHitResult blockHitResult) {
        sendPacket(new ServerboundUseItemOnPacket(hand, blockHitResult, nextSequence()));
    }

    public static void sendInteractItem(InteractionHand hand) {
        sendInteractItem(hand, minecraftClient.player.getYRot(), minecraftClient.player.getXRot());
    }

    public static void sendInteractItem(InteractionHand hand, float f, float f2) {
        sendPacket(new ServerboundUseItemPacket(hand, nextSequence(), f, f2));
    }

    public static void sendHandSwing(InteractionHand hand) {
        sendPacket(new ServerboundSwingPacket(hand));
    }

    public static void sendLook(float f, float f2, boolean z) {
        sendPacket(new ServerboundMovePlayerPacket.Rot(f, f2, z, false));
    }

    public static void sendLook(float[] fArr, boolean z) {
        if (fArr == null || fArr.length < 2) {
            return;
        }
        sendLook(fArr[0], fArr[1], z);
    }

    public static void sendPosition(double d, double d2, double d3, boolean z) {
        sendPacket(new ServerboundMovePlayerPacket.Pos(d, d2, d3, z, false));
    }

    public static void sendFullMove(double d, double d2, double d3, float f, float f2, boolean z) {
        sendPacket(new ServerboundMovePlayerPacket.PosRot(d, d2, d3, f, f2, z, false));
    }

    public static void sendInteractEntity(int i) {
        sendPacket(new ServerboundAttackPacket(i));
    }

    public static void sendPlayerAction(ServerboundPlayerActionPacket.Action action, BlockPos blockPos, Direction direction) {
        sendPacket(new ServerboundPlayerActionPacket(action, blockPos, direction, nextSequence()));
    }

    public static void sendClientCommand(Entity entity, ServerboundPlayerCommandPacket.Action mode) {
        sendClientCommand(entity, mode, 0);
    }

    public static void sendClientCommand(Entity entity, ServerboundPlayerCommandPacket.Action mode, int i) {
        sendPacket(new ServerboundPlayerCommandPacket(entity, mode, i));
    }

    public static void sendStartFallFlying() {
        sendClientCommand(minecraftClient.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING, 0);
    }

    public static void sendSwapOffhand() {
        sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
    }

    public static void sendSelectedSlot(int i) {
        sendSelectedSlot(i, false);
    }

    public static void sendSelectedSlot(int i, boolean z) {
        if (z || ClientServices.fireworksHelper.getTrackedSelectedSlot() != i) {
            sendPacket(new ServerboundSetCarriedItemPacket(i));
        }
    }

    public static void sendPacket(Packet<?> packet) {
        minecraftClient.player.connection.send(packet);
    }

    public static int nextSequence() {
        try {
            var method = minecraftClient.level.getClass().getDeclaredMethod("getBlockStatePredictionHandler");
            method.setAccessible(true);
            Object handler = method.invoke(minecraftClient.level);
            var start = handler.getClass().getMethod("startPredicting");
            Object pred = start.invoke(handler);
            var cur = pred.getClass().getMethod("currentSequence");
            int seq = (int) cur.invoke(pred);
            var close = pred.getClass().getMethod("close");
            close.invoke(pred);
            return seq;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void sendPacketDirect(Packet<?> packet) {
        if (packet == null || minecraftClient.player == null) {
            return;
        }
        minecraftClient.player.connection.send(packet);
    }
}
