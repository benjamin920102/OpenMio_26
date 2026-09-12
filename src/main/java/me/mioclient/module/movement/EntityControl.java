package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.util.client.InputState;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.render.RenderUtil;
import me.mioclient.util.client.InputState;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.TickPreEvent;
import me.mioclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/EntityControl.class */
public class EntityControl extends Module {
    public Setting<Boolean> entitySpeed = add(new BooleanSetting("EntitySpeed", true).setVisible());
    public Setting<Double> speed = add(new NumberSetting<>("Speed", 1.0, 0.1, 20.0, v -> this.entitySpeed.isShown()));
    public Setting<Boolean> noCollision = add(new BooleanSetting("NoCollision", true, v -> this.entitySpeed.isShown()));
    public Setting<Boolean> flight = add(new BooleanSetting("Flight", false, v -> this.entitySpeed.isShown()));
    public Setting<Float> vertical = add(new NumberSetting<>("Vertical", 10.0f, 0.0f, 20.0f).requiresSettings(this.flight, this.entitySpeed));
    public Setting<Boolean> glide = add(new BooleanSetting("Glide", false, v -> this.entitySpeed.isShown()));
    public Setting<Boolean> accelerate = add(new BooleanSetting("Accelerate", false, v -> this.entitySpeed.isShown()));
    public Setting<Boolean> copyYaw = add(new BooleanSetting("CopyYaw", false));
    public Setting<Boolean> horseJump = add(new BooleanSetting("HorseJump", true));
    public boolean flag;
    public long num;

    public EntityControl() {
        super("EntityControl", "Helps with riding entities.", Category.MOVEMENT, new String[0]);
        this.speed.setChangeCallback(() -> {
            this.num = System.currentTimeMillis();
        });
    }

    @Listen
    public void onTickPre(TickPreEvent tickPreEvent) {
        Entity vehicle;
        double d;
        if (isNotInGame()) {
            return;
        }
        if (!MovementUtils.is2181()) {
            this.num = System.currentTimeMillis();
        }
        if (minecraftClient.player.hasVehicle() && (vehicle = minecraftClient.player.getVehicle()) != null) {
            if (this.copyYaw.getValue().booleanValue()) {
                vehicle.setYaw(minecraftClient.player.getYaw(RenderUtil.getPartialTick()));
            }
            if (!(vehicle instanceof Saddleable) || ((Saddleable) vehicle).isSaddled()) {
                double doubleValue = this.speed.getValue().doubleValue();
                double floatValue = this.vertical.getValue().floatValue();
                if (doubleValue >= Double.longBitsToDouble(4626322717216342016L)) {
                    doubleValue = Double.longBitsToDouble(4626319902466574909L);
                }
                if (this.accelerate.getValue().booleanValue()) {
                    doubleValue = MovementUtils.computeAcceleratedSpeed(this.speed.getValue().doubleValue(), Double.longBitsToDouble(4591870180066957722L), this.speed.getValue().doubleValue(), this.num);
                }
                if (!this.entitySpeed.getValue().booleanValue() || isVehicleColliding()) {
                    return;
                }
                double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(RenderUtil.getPartialTick()), minecraftClient.player.input, doubleValue);
                if (this.flight.getValue().booleanValue()) {
                    if (vehicle instanceof BoatEntity) {
                        ((BoatEntity) vehicle).setNoGravity(true);
                    }
                    if (minecraftClient.options.jumpKey.isPressed()) {
                        d = floatValue;
                    } else if (this.glide.getValue().booleanValue() || InputState.isKeyBindingActive(minecraftClient.options.sprintKey)) {
                        d = -floatValue;
                    } else {
                        d = this.flag ? Double.longBitsToDouble(-4637266464074629120L) : Double.longBitsToDouble(4586105572780146688L);
                    }
                } else {
                    d = tickPreEvent.getY();
                }
                tickPreEvent.setMovement(new Vec3d(doubleArray2508[0], d, doubleArray2508[1]));
                this.flag = !this.flag;
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.num = System.currentTimeMillis();
        }
    }

    public boolean isBoatOverride(Object obj) {
        return false;
    }

    public boolean isVehicleColliding() {
        return minecraftClient.player.hasVehicle() && minecraftClient.player.getVehicle().horizontalCollision && this.noCollision.getValue().booleanValue();
    }
}
