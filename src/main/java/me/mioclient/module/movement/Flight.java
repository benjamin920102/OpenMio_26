package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.NamedValue;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientAccessor;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.core.Direction;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class Flight extends Module {
    public Setting<FlightMode_2> mode = add(new EnumSetting<>("Mode", FlightMode_2.flightMode_2));
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.1f, 10.0f));
    public Setting<Boolean> vertical = add(new BooleanSetting("Vertical", false).setVisible());
    public Setting<Float> vSpeed = add(new NumberSetting<>("VSpeed", 1.0f, 0.0f, 5.0f, v -> this.vertical.isShown()));
    public Setting<Float> glide = add(new NumberSetting<>("Glide", 0.0f, 0.0f, 5.0f));
    public Setting<FlightMode> antiKick = add(new EnumSetting<>("AntiKick", FlightMode.NONE));
    public Setting<Boolean> accelerate = add(new BooleanSetting("Accelerate", false).setVisible());
    public Setting<Float> accelMin = add(new NumberSetting<>("AccelMin", 1.0f, 0.0f, 5.0f).requiresSettings(this.accelerate));
    public Setting<Float> accelTime = add(new NumberSetting<>("AccelTime", 1.0f, 0.1f, 10.0f).setSuffix("s").requiresSettings(this.accelerate));
    public long num;

    

    private enum FlightMode implements NamedValue {
        NONE("None"),
        flightMode("Plain") {
            public long num;

            @Override
            public void applyAntiKickMotion(MotionEvent motionEvent) {
                if (ClientAccessor.minecraftClient.player.onGround() || System.currentTimeMillis() - this.num < 500 || ClientAccessor.minecraftClient.player.onGround()) {
                    return;
                }
                this.num = System.currentTimeMillis();
                motionEvent.setY(motionEvent.getY() - 0.04d);
            }
        },
        flightMode2("Alternative") {
            public boolean flag;

            @Override
            public void handleMove(MoveEvent moveEvent) {
                if (ClientAccessor.minecraftClient.player.onGround()) {
                    return;
                }
                double d = 0.04d;
                if (this.flag) {
                    d = -0.04d;
                }
                moveEvent.setY(moveEvent.getY() + d);
                ClientAccessor.minecraftClient.player.setDeltaMovement(ClientAccessor.minecraftClient.player.getDeltaMovement().add(0.0d, d, 0.0d));
                this.flag = !this.flag;
            }
        };

        public final String name;

        FlightMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public void handleMove(MoveEvent moveEvent) {
        }

        public void applyAntiKickMotion(MotionEvent motionEvent) {
        }
    }

    

    public enum FlightMode_2 implements NamedValue {
        flightMode_2("Vanilla") {
            @Override
            public void handleModeMove(Flight flight, MoveEvent moveEvent) {
                ClientAccessor.minecraftClient.player.getAbilities().flying = true;
                ClientAccessor.minecraftClient.player.getAbilities().setFlyingSpeed((float) (flight.getEffectiveSpeed() * 0.05000000074505806d));
            }
        },
        flightMode_22("Static") {
            public int num;

            @Override
            public void handleModeMove(Flight flight, MoveEvent moveEvent) {
                double floatValue = flight.vertical.getValue().booleanValue() ? flight.vSpeed.getValue().floatValue() : 0.0d;
                boolean z = flight.antiKick.getValue() == FlightMode.flightMode2;
                if (ClientAccessor.minecraftClient.player.input.keyPresses.jump()) {
                    this.num++;
                    if (z) {
                        if (this.num >= 18) {
                            floatValue = -0.1d;
                        }
                        if (this.num >= 21) {
                            this.num = 0;
                        }
                    }
                    moveEvent.setY(floatValue);
                    ClientAccessor.minecraftClient.player.setDeltaMovement(ClientAccessor.minecraftClient.player.getDeltaMovement().with(Direction.Axis.Y, floatValue));
                } else if (ClientAccessor.minecraftClient.player.input.keyPresses.shift()) {
                    moveEvent.setY(-floatValue);
                    ClientAccessor.minecraftClient.player.setDeltaMovement(ClientAccessor.minecraftClient.player.getDeltaMovement().with(Direction.Axis.Y, -floatValue));
                } else {
                    moveEvent.setY(0.0d);
                    ClientAccessor.minecraftClient.player.setDeltaMovement(ClientAccessor.minecraftClient.player.getDeltaMovement().with(Direction.Axis.Y, 0.0d));
                    if (!ClientAccessor.minecraftClient.player.verticalCollision && flight.glide.getValue().floatValue() != 0.0f) {
                        ClientAccessor.minecraftClient.player.setDeltaMovement(ClientAccessor.minecraftClient.player.getDeltaMovement().add(0.0d, -flight.glide.getValue().floatValue(), 0.0d));
                        moveEvent.setY(ClientAccessor.minecraftClient.player.getDeltaMovement().y);
                    }
                }
                double[] doubleArray2507 = MovementUtils.applyVelocity(moveEvent, flight.getEffectiveSpeed());
                ClientAccessor.minecraftClient.player.setDeltaMovement(doubleArray2507[0], ClientAccessor.minecraftClient.player.getDeltaMovement().y, doubleArray2507[1]);
            }
        },
        flightMode_23("JetPack") {
            @Override
            public void handleModeMotion(Flight flight, MotionEvent motionEvent) {
                if (ClientAccessor.minecraftClient.options.keyJump.isDown()) {
                    ClientAccessor.minecraftClient.player.setDeltaMovement(ClientAccessor.minecraftClient.player.getDeltaMovement().add(0.0d, flight.getEffectiveSpeed() * 0.1d, 0.0d));
                }
            }
        },
        flightMode_24("Source") {
            @Override
            public void handleModeMove(Flight flight, MoveEvent moveEvent) {
                double d = flight.getEffectiveSpeed();
                MovementUtils.applyVelocity(moveEvent, d);
                if (!MovementUtils.is2181()) {
                    moveEvent.setY(0.0d);
                } else {
                    moveEvent.setY((-Math.signum(((ClientAccessor.minecraftClient.player.input.keyPresses.forward() ? 1.0f : 0.0f) - (ClientAccessor.minecraftClient.player.input.keyPresses.backward() ? 1.0f : 0.0f)))) * Math.sin(Math.toRadians(ClientAccessor.minecraftClient.player.getXRot())) * d);
                }
            }
        };

        public final String name;

        FlightMode_2(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public void handleModeMotion(Flight flight, MotionEvent motionEvent) {
        }

        public void handleModeMove(Flight flight, MoveEvent moveEvent) {
        }
    }

    public Flight() {
        super("Flight", "Allows you to fly using magic.", Category.MOVEMENT, new String[0]);
        this.speed.removeBounds();
        this.vSpeed.removeBounds();
        this.mode.setChangeCallback(() -> {
            if (isToggled()) {
                minecraftClient.player.getAbilities().flying = false;
            }
        });
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        try {
            return ClientTextRenderer.capitalize(this.mode.getValue());
        } catch (Exception e) {
            return null;
        }
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.num = System.currentTimeMillis();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (!isNotInGame() && this.mode.getValue() == FlightMode_2.flightMode_2) {
            minecraftClient.player.getAbilities().flying = false;
        }
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        this.mode.getValue().handleModeMotion(this, motionEvent);
        this.antiKick.getValue().applyAntiKickMotion(motionEvent);
    }

    @Listen
    public void onMove(MoveEvent moveEvent) {
        if (!MovementUtils.is2181()) {
            this.num = System.currentTimeMillis();
        }
        this.mode.getValue().handleModeMove(this, moveEvent);
        this.antiKick.getValue().handleMove(moveEvent);
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof ClientboundTeleportEntityPacket) {
            this.num = System.currentTimeMillis();
        }
    }

    public double getEffectiveSpeed() {
        return this.accelerate.getValue().booleanValue() ? MovementUtils.computeAcceleratedSpeed(this.speed.getValue().floatValue(), this.accelMin.getValue().floatValue(), this.accelTime.getValue().floatValue(), this.num) : this.speed.getValue().floatValue();
    }
}