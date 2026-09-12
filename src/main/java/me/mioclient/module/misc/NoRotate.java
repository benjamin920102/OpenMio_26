package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.mixin.ducks.DuckPlayerPositionLookS2CPacket;
import me.mioclient.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/NoRotate.class */
public class NoRotate extends Module {
    public Setting<Boolean> inBlocks = add(new BooleanSetting("InBlocks", false));
    public Setting<Boolean> stopInWebs = add(new BooleanSetting("StopInWebs", false));

    public NoRotate() {
        super("NoRotate", "Cancels the rotations server sets for you.", Category.MISC, new String[0]);
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (!this.inBlocks.getValue().booleanValue() || MovementUtils.is2181() || minecraftClient.player.isHoldingOntoLadder() || !(sendImmediatelyEvent.getPacket() instanceof PlayerMoveC2SPacket)) {
            return;
        }
        if (this.stopInWebs.getValue().booleanValue() && PlayerUtils.isInCobweb(minecraftClient.player)) {
            return;
        }
        BlockState blockState = minecraftClient.world.getBlockState(minecraftClient.player.getBlockPos());
        if (blockState.getBlock() instanceof FallingBlock) {
            return;
        }
        VoxelShape collisionShape = blockState.getCollisionShape(minecraftClient.world, BlockPos.ORIGIN);
        if (collisionShape.isEmpty() || minecraftClient.player.getY() - Math.floor(minecraftClient.player.getY()) >= collisionShape.getBoundingBox().maxY) {
            return;
        }
        if (collisionShape.getBoundingBox().offset(minecraftClient.player.getBlockPos()).intersects(minecraftClient.player.getBoundingBox().expand(Double.longBitsToDouble(-4636005456415188582L)))) {
            sendImmediatelyEvent.cancel();
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (minecraftClient.isInSingleplayer() || minecraftClient.player.isHoldingOntoLadder()) {
            return;
        }
        if (!RotationManager.is724() || minecraftClient.player.age > 20) {
            DuckPlayerPositionLookS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof PlayerPositionLookS2CPacket ? (DuckPlayerPositionLookS2CPacket)(channelRead0Event.getPacket()) : null;
            if (packet904 instanceof PlayerPositionLookS2CPacket) {
                DuckPlayerPositionLookS2CPacket duckPlayerPositionLookS2CPacket = (DuckPlayerPositionLookS2CPacket)((PlayerPositionLookS2CPacket) packet904);
                if (isNotInGame() || minecraftClient.player.age <= 5) {
                    return;
                }
                float yaw = minecraftClient.player.getYaw();
                float pitch = minecraftClient.player.getPitch();
                if (((PlayerPositionLookS2CPacket) duckPlayerPositionLookS2CPacket).getFlags().contains(PositionFlag.X_ROT)) {
                    yaw = 0.0f;
                }
                if (((PlayerPositionLookS2CPacket) duckPlayerPositionLookS2CPacket).getFlags().contains(PositionFlag.Y_ROT)) {
                    pitch = 0.0f;
                }
                duckPlayerPositionLookS2CPacket.setYaw(yaw);
                duckPlayerPositionLookS2CPacket.setPitch(pitch);
            }
        }
    }
}
