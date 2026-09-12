package me.mioclient.network;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import io.netty.buffer.Unpooled;
import me.mioclient.mixin.ducks.DuckClientWorld;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PacketSender.class */
public class PacketSender implements ClientAccessor {
    public static void sendInteractBlock(Hand hand, BlockHitResult blockHitResult) {
        sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult, nextSequence()));
    }

    public static void sendInteractItem(Hand hand) {
        sendInteractItem(hand, minecraftClient.player.getYaw(), minecraftClient.player.getPitch());
    }

    public static void sendInteractItem(Hand hand, float f, float f2) {
        sendPacket(new PlayerInteractItemC2SPacket(hand, nextSequence(), f, f2));
    }

    public static void sendHandSwing(Hand hand) {
        sendPacket(new HandSwingC2SPacket(hand));
    }

    public static void sendLook(float f, float f2, boolean z) {
        sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(f, f2, z));
    }

    public static void sendLook(float[] fArr, boolean z) {
        if (fArr == null || fArr.length < 2) {
            return;
        }
        sendLook(fArr[0], fArr[1], z);
    }

    public static void sendPosition(double d, double d2, double d3, boolean z) {
        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(d, d2, d3, z));
    }

    public static void sendFullMove(double d, double d2, double d3, float f, float f2, boolean z) {
        sendPacket(new PlayerMoveC2SPacket.Full(d, d2, d3, f, f2, z));
    }

    public static void sendInteractEntity(int i) {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeVarInt(i);
        packetByteBuf.writeVarInt(1);
        packetByteBuf.writeBoolean(minecraftClient.player.isSneaking());
        sendPacket(PlayerInteractEntityC2SPacket.CODEC.decode(packetByteBuf));
    }

    public static void sendPlayerAction(PlayerActionC2SPacket.Action action, BlockPos blockPos, Direction direction) {
        sendPacket(new PlayerActionC2SPacket(action, blockPos, direction, nextSequence()));
    }

    public static void sendClientCommand(Entity entity, ClientCommandC2SPacket.Mode mode) {
        sendClientCommand(entity, mode, 0);
    }

    public static void sendClientCommand(Entity entity, ClientCommandC2SPacket.Mode mode, int i) {
        sendPacket(new ClientCommandC2SPacket(entity, mode, i));
    }

    public static void sendStartFallFlying() {
        sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING, 0);
    }

    public static void sendSwapOffhand() {
        sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
    }

    public static void sendSelectedSlot(int i) {
        sendSelectedSlot(i, false);
    }

    public static void sendSelectedSlot(int i, boolean z) {
        if (z || ClientServices.fireworksHelper.getTrackedSelectedSlot() != i) {
            sendPacket(new UpdateSelectedSlotC2SPacket(i));
        }
    }

    public static void sendPacket(Packet<?> packet) {
        minecraftClient.player.networkHandler.sendPacket(packet);
    }

    public static int nextSequence() {
        PendingUpdateManager incrementSequence = ((DuckClientWorld) (Object) minecraftClient.world).getPendingUpdateManager().incrementSequence();
        if (incrementSequence != null) {
            incrementSequence.close();
        }
        return incrementSequence.getSequence();
    }

    public static void sendPacketDirect(Packet<?> packet) {
        if (packet == null) {
            return;
        }
        ClientConnection connection = minecraftClient.player.networkHandler.getConnection();
        if (connection.isOpen()) {
            connection.packetsSentCounter++;
            if (connection.channel.eventLoop().inEventLoop()) {
                connection.channel.writeAndFlush(packet);
                return;
            }
            connection.channel.eventLoop().execute(() -> {
                connection.channel.writeAndFlush(packet);
            });
        }
    }
}
