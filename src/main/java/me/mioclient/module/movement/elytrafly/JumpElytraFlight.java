package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.ConditionCheck;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.movement.ElytraFly;
import net.minecraft.client.input.Input;
import me.mioclient.api.ConditionCheck;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/JumpElytraFlight.class */
public class JumpElytraFlight extends ElytraFlyModeBase implements ConditionCheck {
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);

    public JumpElytraFlight(ElytraFly elytraFly) {
        super(elytraFly);
    }

    @Override // me.mioclient.api.ConditionCheck
    public boolean is328() {
        if (isNotInGame()) {
            return false;
        }
        return !PlayerUtils.isReallyFallFlying() || minecraftClient.player.isOnGround();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTickPost(TickPostEvent tickPostEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (is328()) {
            antiCheat.getYawSpoofHandler().spoofYawOnMovePacket(sendImmediatelyEvent);
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMove(MoveEvent moveEvent) {
        boolean z = !ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(300L);
        if (PlayerUtils.isReallyFallFlying() || z) {
            return;
        }
        float floatValue = this.elytraFly.speed3.getValue().floatValue();
        Input input = minecraftClient.player.input;
        if (input.movementForward == 0.0f && input.movementSideways == 0.0f) {
            input.movementForward = Float.intBitsToFloat(1065353216);
        }
        double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(RenderUtil.getPartialTick()), input, floatValue);
        moveEvent.setHorizontal(doubleArray2508[0], doubleArray2508[1]);
        this.elytraFly.stopwatch4.reset();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleInputTick(InputTickEvent tickEvent_2) {
        if (minecraftClient.player.isOnGround()) {
            return;
        }
        tickEvent_2.getInput().jumping = minecraftClient.player.age % 3 == 0;
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleRotation(RotationEvent event_3) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMotion(MotionEvent motionEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTick(TickEvent tickEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
    }
}
