package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.module.movement.warp.WarpHelperMode;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.module.movement.warp.WarpChargeHandler;

import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.module.movement.warp.WarpHelperMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/Warp.class */
public class Warp extends Module {
    public static FakeLag fakeLag2 = (FakeLag) ClientServices.moduleInjector.getModule(FakeLag.class);
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

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/Warp$WarpMode.class */
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
                minecraftClient.player.networkHandler.sendPacket(this.warpHelper.queue.poll());
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
                boolean isToggled = fakeLag2.isToggled();
                setFakeLagEnabled(minecraftClient.player.isOnGround() || !this.disableGround.getValue().booleanValue());
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
                minecraftClient.player.networkHandler.sendPacket(this.warpHelper.queue.poll());
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
        if (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.flag = true;
        }
    }

    public void setFakeLagEnabled(boolean z) {
        if (!this.fakeLag.getValue().booleanValue() || minecraftClient.player == null) {
            return;
        }
        fakeLag2.setToggled(z);
    }

    public boolean isPlayerMoving() {
        return minecraftClient.player.input.pressingRight || minecraftClient.player.input.pressingLeft || minecraftClient.player.input.pressingBack || minecraftClient.player.input.pressingForward || minecraftClient.player.getX() - minecraftClient.player.prevX != 0.0d || minecraftClient.player.getY() - minecraftClient.player.prevY != 0.0d || minecraftClient.player.getZ() - minecraftClient.player.prevZ != 0.0d;
    }

    public int get2093() {
        return this.mode.getValue() == WarpMode.ALTERNATIVE ? this.warpHelper.queue.size() : this.num;
    }

    public boolean is2181() {
        return minecraftClient.player.getPos().squaredDistanceTo(minecraftClient.player.prevX, minecraftClient.player.prevY, minecraftClient.player.prevZ) > 0.0d;
    }
}
