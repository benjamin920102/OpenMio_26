package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.module.movement.warp.WarpHelperMode;

import me.mioclient.module.movement.warp.WarpChargeHandler;

import me.mioclient.render.RenderConstants;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class Warp extends Module {
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving fakeLag2 null (NPE). */
    private static FakeLag fakeLag2;

    public static FakeLag getFakeLag2() {
        if (fakeLag2 == null && ClientServices.moduleInjector != null) {
            try {
                fakeLag2 = (FakeLag) ClientServices.moduleInjector.getModule(FakeLag.class);
            } catch (Exception e) {
            }
        }
        return fakeLag2;
    }
    public Setting<WarpMode> mode = add(new EnumSetting<>("Mode", WarpMode.PLAIN));
    public Setting<Integer> boost = add(new NumberSetting<>("Boost", 2, 1, 50));
    public Setting<Integer> charge = add(new NumberSetting<>("Charge", 20, 1, 100));
    public Setting<Integer> chargeSpeed = add(new NumberSetting<>("ChargeSpeed", 1, 1, 4, v -> this.mode.getValue() == Warp.WarpMode.PLAIN));
    public Setting<WarpHelperMode> recharge = add(new EnumSetting<>("Recharge", WarpHelperMode.NONE, v -> this.mode.getValue() == Warp.WarpMode.PLAIN));
    public Setting<Boolean> fakeLag = add(new BooleanSetting("FakeLag", false).setVisible());
    public Setting<Boolean> disableGround = add(new BooleanSetting("DisableGround", false).requiresSettings(this.fakeLag));
    public Setting<Boolean> limitPackets = add(new BooleanSetting("LimitPackets", false).requiresSettings(this.fakeLag));
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", false));
    public final WarpChargeHandler warpHelper;
    public boolean flag;
    public volatile int num;

    
    public enum WarpMode implements NamedValue {
        PLAIN("Plain"),
        ALTERNATIVE("Alternative");

        public final String name;

        WarpMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Warp() {
        super("Warp", "Allows you to dash forward after standing still for a certain amount of time.", Category.MOVEMENT, "tickshift");
        this.warpHelper = new WarpChargeHandler(this);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return this.mode.getValue() == WarpMode.ALTERNATIVE ? String.valueOf(this.warpHelper.queue.size()) : String.valueOf(this.num);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (this.recharge.getValue() == WarpHelperMode.INSTANT) {
            this.num = this.charge.getValue().intValue();
        }
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        setFakeLagEnabled(false);
        if (this.mode.getValue() != WarpMode.ALTERNATIVE || isNotInGame()) {
            return;
        }
        while (!this.warpHelper.queue.isEmpty()) {
            try {
                minecraftClient.player.connection.send(this.warpHelper.queue.poll());
            } catch (Exception e) {
            }
        }
        this.warpHelper.queue.clear();
    }

    @Listen(get219= RenderConstants.num5)
    public void onMove(MoveEvent moveEvent) {
        if (this.mode.getValue() != WarpMode.PLAIN) {
            return;
        }
        if (!isPlayerMoving()) {
            ClientServices.inner.releaseTickRate(this);
            setFakeLagEnabled(false);
            this.flag = false;
            return;
        }
        if (this.num == 0 || this.flag) {
            ClientServices.inner.releaseTickRate(this);
            if (this.autoDisable.getValue().booleanValue()) {
                toggle();
            }
            setFakeLagEnabled(false);
            return;
        }
        if (this.num > 0) {
            if (this.fakeLag.getValue().booleanValue()) {
                FakeLag fakeLag2_lcl = getFakeLag2();
                boolean isToggled = fakeLag2_lcl != null && fakeLag2_lcl.isToggled();
                setFakeLagEnabled(minecraftClient.player.onGround() || !this.disableGround.getValue().booleanValue());
                if (this.boost.getValue().intValue() > 1 && this.limitPackets.getValue().booleanValue()) {
                    ClientServices.holeSnapRenderHelper.getTeleportStopwatch().reset();
                }
                if (!isToggled) {
                    return;
                }
            }
            ClientServices.inner.requestTickRate(this, this.boost.getValue().intValue());
        }
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        if (this.mode.getValue() == WarpMode.ALTERNATIVE && motionEvent.getPhase() == KeyPearlMode.Pre) {
            if (!this.warpHelper.queue.isEmpty() && isPlayerMoving()) {
                ClientServices.inner.requestTickRate(this, this.boost.getValue().intValue());
                minecraftClient.player.connection.send(this.warpHelper.queue.poll());
                return;
            }
            if (!this.warpHelper.queue.isEmpty() && !this.flag) {
                ClientServices.inner.releaseTickRate(this);
                return;
            }
            ClientServices.inner.releaseTickRate(this);
            if (this.autoDisable.getValue().booleanValue()) {
                toggle();
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof ClientboundTeleportEntityPacket) {
            this.flag = true;
        }
    }

    public void setFakeLagEnabled(boolean z) {
        if (!this.fakeLag.getValue().booleanValue() || minecraftClient.player == null) {
            return;
        }
        FakeLag fakeLag2_lcl = getFakeLag2();
        if (fakeLag2_lcl != null) {
            fakeLag2_lcl.setToggled(z);
        }
    }

    public boolean isPlayerMoving() {
        return minecraftClient.player.input.keyPresses.right() || minecraftClient.player.input.keyPresses.left() || minecraftClient.player.input.keyPresses.backward() || minecraftClient.player.input.keyPresses.forward() || minecraftClient.player.getX() - minecraftClient.player.xo != 0.0d || minecraftClient.player.getY() - minecraftClient.player.yo != 0.0d || minecraftClient.player.getZ() - minecraftClient.player.zo != 0.0d;
    }

    public int get2093() {
        return this.mode.getValue() == WarpMode.ALTERNATIVE ? this.warpHelper.queue.size() : this.num;
    }

    public boolean is2181() {
        return minecraftClient.player.position().distanceToSqr(minecraftClient.player.xo, minecraftClient.player.yo, minecraftClient.player.zo) > 0.0d;
    }
}