package me.mioclient.module.movement.speed;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.command.FakePlayerCommand;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.movement.Speed;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.mode.combat.KeyPearlMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/GrimSpeedHandler.class */
public class GrimSpeedHandler extends SpeedModeHandler {
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Box box;

    public GrimSpeedHandler(Speed speed) {
        super(speed);
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void applyMovement(MoveEvent moveEvent) {
        if (this.box != null && MovementUtils.is2181()) {
            int i = 0;
            for (BlockEntity blockEntity : WorldUtils.getLoadedBlockEntities()) {
                if (blockEntity instanceof ShulkerBoxBlockEntity) {
                    ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity) blockEntity;
                    if (this.box.expand(Double.longBitsToDouble(4602678819172646912L)).intersects(shulkerBoxBlockEntity.getBoundingBox(Blocks.SHULKER_BOX.getDefaultState()).offset(blockEntity.getPos())) && shulkerBoxBlockEntity.getAnimationStage() != ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                        i += antiCheat.isF2b2t() ? 0 : 4;
                    }
                }
            }
            float intBitsToFloat = antiCheat.isF2b2t() ? 0.0f : Float.intBitsToFloat(1065353216);
            for (Entity entity : minecraftClient.world.getEntities()) {
                if (!(entity instanceof FakePlayerCommand.OtherClientPlayerEntity) && entity != minecraftClient.player && entity.isPushable()) {
                    if (entity.getBoundingBox().intersects(this.box.expand(intBitsToFloat))) {
                        if (entity instanceof BoatEntity) {
                            i += 4;
                        }
                        i++;
                    }
                }
            }
            if (i == 0) {
                return;
            }
            moveEvent.setMovement(moveEvent.getMovement().add(moveEvent.getMovement().normalize().withAxis(Direction.Axis.Y, 0.0d).multiply(Double.longBitsToDouble(4590429028186199163L) * i)));
        }
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void applyAntiKickMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Post) {
            this.box = minecraftClient.player.getBoundingBox();
        }
    }
}
