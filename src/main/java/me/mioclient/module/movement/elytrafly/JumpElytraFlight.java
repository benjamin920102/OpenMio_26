package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.ConditionCheck;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.render.RenderUtil;

import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.movement.ElytraFly;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;

public class JumpElytraFlight extends ElytraFlyModeBase implements ConditionCheck {
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving refs null (NPE). */
    private static AntiCheat antiCheat;

    public static AntiCheat getAntiCheat() {
        if (antiCheat == null && ClientServices.moduleInjector != null) {
            try {
                antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
            } catch (Exception e) {
            }
        }
        return antiCheat;
    }

    public JumpElytraFlight(ElytraFly elytraFly) {
        super(elytraFly);
    }

    @Override // me.mioclient.api.ConditionCheck
    public boolean is328() {
        if (isNotInGame()) {
            return false;
        }
        return !PlayerUtils.isReallyFallFlying() || minecraftClient.player.onGround();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTickPost(TickPostEvent tickPostEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (is328()) {
            AntiCheat antiCheat_lcl = getAntiCheat();
            if (antiCheat_lcl != null) {
                antiCheat_lcl.getYawSpoofHandler().spoofYawOnMovePacket(sendImmediatelyEvent);
            }
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMove(MoveEvent moveEvent) {
        boolean z = !ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(300L);
        if (PlayerUtils.isReallyFallFlying() || z) {
            return;
        }
        float floatValue = this.elytraFly.speed3.getValue().floatValue();
        ClientInput input = minecraftClient.player.input;
        Input presses = input.keyPresses;
        if (!presses.forward() && !presses.backward() && !presses.left() && !presses.right()) {
            input.keyPresses = new Input(true, presses.backward(), presses.left(), presses.right(), presses.jump(), presses.shift(), presses.sprint());
            presses = input.keyPresses;
        }
        double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYRot(), input, floatValue);
        moveEvent.setHorizontal(doubleArray2508[0], doubleArray2508[1]);
        this.elytraFly.stopwatch4.reset();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleInputTick(InputTickEvent tickEvent_2) {
        if (minecraftClient.player.onGround()) {
            return;
        }
        ClientInput ci = tickEvent_2.getInput();
        Input p = ci.keyPresses;
        boolean jump = minecraftClient.player.tickCount % 3 == 0;
        ci.keyPresses = new Input(p.forward(), p.backward(), p.left(), p.right(), jump, p.shift(), p.sprint());
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