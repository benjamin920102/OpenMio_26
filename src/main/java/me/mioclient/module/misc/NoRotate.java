package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.shapes.VoxelShape;
import me.mioclient.setting.BooleanSetting;

public class NoRotate extends Module {
    public Setting<Boolean> inBlocks = add(new BooleanSetting("InBlocks", false));
    public Setting<Boolean> stopInWebs = add(new BooleanSetting("StopInWebs", false));

    public NoRotate() {
        super("NoRotate", "Cancels the rotations server sets for you.", Category.MISC, new String[0]);
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        // 26.2 port (verified): VoxelShape#getBoundingBox→bounds(), AABB#offset→move(),
        // expand→inflate(); BlockPos.ORIGIN→ZERO; isHoldingOntoLadder→LivingEntity.onClimbable().
        if (!this.inBlocks.getValue().booleanValue() || MovementUtils.is2181() || minecraftClient.player.onClimbable() || !(sendImmediatelyEvent.getPacket() instanceof ServerboundMovePlayerPacket)) {
            return;
        }
        if (this.stopInWebs.getValue().booleanValue() && PlayerUtils.isInCobweb(minecraftClient.player)) {
            return;
        }
        BlockState blockState = minecraftClient.level.getBlockState(minecraftClient.player.blockPosition());
        if (blockState.getBlock() instanceof FallingBlock) {
            return;
        }
        VoxelShape collisionShape = blockState.getCollisionShape(minecraftClient.level, BlockPos.ZERO);
        if (collisionShape.isEmpty() || minecraftClient.player.getY() - Math.floor(minecraftClient.player.getY()) >= collisionShape.bounds().maxY) {
            return;
        }
        if (collisionShape.bounds().move(minecraftClient.player.blockPosition()).intersects(minecraftClient.player.getBoundingBox().inflate(-1.0E-4d))) {
            sendImmediatelyEvent.cancel();
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        // 26.2 port (verified): PlayerPositionLookS2CPacket is now ClientboundPlayerPositionPacket
        // record(id, PositionMoveRotation change, Set<Relative> relatives) — immutable, so cancel
        // + ack + apply position while keeping client rotation (NoRotate intent).
        if (minecraftClient.hasSingleplayerServer() || minecraftClient.player.onClimbable()) {
            return;
        }
        if (!RotationManager.is724() || minecraftClient.player.tickCount > 20) {
            if (!(channelRead0Event.getPacket() instanceof ClientboundPlayerPositionPacket pkt)) {
                return;
            }
            if (isNotInGame() || minecraftClient.player.tickCount <= 5) {
                return;
            }
            float yaw = minecraftClient.player.getYRot();
            float pitch = minecraftClient.player.getXRot();
            if (pkt.relatives().contains(Relative.X_ROT)) {
                yaw = 0.0f;
            }
            if (pkt.relatives().contains(Relative.Y_ROT)) {
                pitch = 0.0f;
            }
            channelRead0Event.cancel();
            minecraftClient.player.connection.send(new ServerboundAcceptTeleportationPacket(pkt.id()));
            PositionMoveRotation absolute = PositionMoveRotation.calculateAbsolute(PositionMoveRotation.of(minecraftClient.player), pkt.change(), pkt.relatives());
            minecraftClient.player.setPos(absolute.position().x, absolute.position().y, absolute.position().z);
            float finalYaw = yaw == 0.0f ? minecraftClient.player.getYRot() + pkt.change().yRot() : yaw;
            float finalPitch = pitch == 0.0f ? minecraftClient.player.getXRot() + pkt.change().xRot() : pitch;
            minecraftClient.player.setYRot(finalYaw);
            minecraftClient.player.setXRot(finalPitch);
        }
    }
}