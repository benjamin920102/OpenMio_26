package me.mioclient.module.movement;

import me.mioclient.event.events.player.MoveSpeedEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveSpeedEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/Step.class */
public class Step extends Module {
    public static final HoleSnap holeSnap = (HoleSnap) ClientServices.moduleInjector.getModule(HoleSnap.class);
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Setting<StepMode> mode = add(new EnumSetting<>("Mode", StepMode.VANILLA));
    public Setting<Float> height = add(new NumberSetting<>("Height", 2.0f, 1.0f, 4.0f).setSuffix("m"));
    public Setting<Boolean> safeDisable = add(new BooleanSetting("SafeDisable", false));
    public Setting<Boolean> useTimer = add(new BooleanSetting("UseTimer", true, v -> this.mode.getValue() == Step.StepMode.NORMAL).setVisible());
    public Setting<Float> timerSpeed = add(new NumberSetting<>("TimerSpeed", 0.3f, 0.01f, 1.0f, v -> this.mode.getValue() == Step.StepMode.NORMAL && this.useTimer.isShown()));
    public boolean flag;
    public int num;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/Step$StepMode.class */
    public enum StepMode implements NamedValue {
        VANILLA("Vanilla"),
        NORMAL("Normal");

        public final String name;

        StepMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Step() {
        super("Step", "Allows you to step up blocks without jumping.", Category.MOVEMENT, new String[0]);
        this.num = 0;
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        this.flag = false;
        this.num = 0;
        if (isNotInGame()) {
            return;
        }
        EntityUtil.setStepHeight(minecraftClient.player, Float.intBitsToFloat(1058642330));
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return ClientTextRenderer.capitalize(this.mode.getValue());
    }

    @Listen(get219= RenderConstants.num4)
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (this.num > 0) {
            if (ClientServices.holeSnapOverlayHelper.isHoleAt(PlayerUtils.getRoundedBlockPos()) && this.safeDisable.getValue().booleanValue()) {
                disable();
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.flag = false;
    }

    @Listen
    public void onMoveSpeed(MoveSpeedEvent moveEvent_2) {
        if (holeSnap.shouldPauseStep()) {
            return;
        }
        if (!minecraftClient.player.isOnGround() || minecraftClient.player.fallDistance > 0.0f || !minecraftClient.player.verticalCollision) {
            ClientServices.holeSnapHelper.releaseTickRate(this);
            return;
        }
        if (moveEvent_2.getPhase() == KeyPearlMode.Pre) {
            moveEvent_2.setSpeed(this.height.getValue().floatValue());
        }
        if (moveEvent_2.getPhase() == KeyPearlMode.Post) {
            if (moveEvent_2.getSpeed() <= Double.longBitsToDouble(4603579539098121011L)) {
                ClientServices.holeSnapHelper.releaseTickRate(this);
                return;
            }
            if (this.mode.getValue() == StepMode.VANILLA) {
                this.num++;
                return;
            }
            ArrayList arrayList = new ArrayList();
            if (this.useTimer.getValue().booleanValue() && !PlayerUtils.isInCobweb(minecraftClient.player)) {
                ClientServices.holeSnapHelper.requestTickRate(this, this.timerSpeed.getValue().floatValue());
                this.flag = true;
            }
            this.num++;
            if (moveEvent_2.getSpeed() <= Float.intBitsToFloat(1065353216)) {
                arrayList.addAll(List.of(Double.valueOf(Double.longBitsToDouble(4601237667291888353L) * moveEvent_2.getSpeed()), Double.valueOf(Double.longBitsToDouble(4604930618986332160L) * moveEvent_2.getSpeed())));
            } else if (moveEvent_2.getSpeed() <= Double.longBitsToDouble(4609434218613702656L)) {
                arrayList.addAll(List.of(Double.valueOf(Double.longBitsToDouble(4601237667291888353L)), Double.valueOf(Double.longBitsToDouble(4604930618986332160L)), Double.valueOf(Double.longBitsToDouble(4607182418800017408L)), Double.valueOf(Double.longBitsToDouble(4607902994740396687L)), Double.valueOf(Double.longBitsToDouble(4608218246714312622L)), Double.valueOf(Double.longBitsToDouble(4608083138725491507L))));
            } else if (moveEvent_2.getSpeed() <= this.height.getValue().floatValue()) {
                arrayList.addAll(List.of(Double.valueOf(Double.longBitsToDouble(4601237667291888353L)), Double.valueOf(Double.longBitsToDouble(4605200834963974390L)), Double.valueOf(Double.longBitsToDouble(4603849755075763241L)), Double.valueOf(Double.longBitsToDouble(4602768891165194322L)), Double.valueOf(Double.longBitsToDouble(4606281698874543309L)), Double.valueOf(Double.longBitsToDouble(4608128174721765212L)), Double.valueOf(Double.longBitsToDouble(4609209038632334131L)), Double.valueOf(Double.longBitsToDouble(4609118966639786721L))));
            }
            if (arrayList.isEmpty()) {
                return;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                minecraftClient.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(minecraftClient.player.getX(), minecraftClient.player.getY() + ((Double) it.next()).doubleValue(), minecraftClient.player.getZ(), minecraftClient.player.isOnGround()));
            }
        }
    }

    public boolean isStepping() {
        return this.flag;
    }
}
