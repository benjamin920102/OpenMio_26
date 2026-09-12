package me.mioclient.module.client;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.mode.combat.CrystalRotationMode;
import me.mioclient.module.KeybindModule;

import me.mioclient.mode.client.AntiCheatMode;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.module.movement.velocity.AntiCheatVelocityHandler;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.movement.velocity.AntiCheatVelocityHandler;
import me.mioclient.module.client.anticheat.YawSpoofHandler;
import me.mioclient.module.movement.velocity.VelocityModeHandler;
import me.mioclient.network.PacketSender;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.event.EventBus;

import me.mioclient.mode.combat.CrystalRotationMode;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.module.KeybindModule;
import me.mioclient.mode.client.AntiCheatMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.movement.Velocity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.util.math.MathHelper;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/client/AntiCheat.class */
public class AntiCheat extends KeybindModule {
    public static final Velocity velocity = (Velocity) ClientServices.moduleInjector.getModule(Velocity.class);
    public static final float val = (float) (((Math.pow(Double.longBitsToDouble(4596373779801702400L), Double.longBitsToDouble(4613937818241073152L)) * Double.longBitsToDouble(4620693217682128896L)) * Double.longBitsToDouble(4594572339843380019L)) - Double.longBitsToDouble(4562254508917369340L));
    public Setting<Boolean> f2b2t = add(new BooleanSetting("2b2t", false));
    public Setting<Float> yawStep = add(new NumberSetting<>("YawStep", 1.0f, 0.1f, 1.0f));
    public Setting<Boolean> movementSync = add(new BooleanSetting("MovementSync", false).setVisible());
    public Setting<CrystalRotationMode> rotations = add(new EnumSetting<>("Rotations", CrystalRotationMode.SILENT).requiresSettings(this.movementSync));
    public Setting<AntiCheatMode> strictDirection = add(new EnumSetting<>("StrictDirection", AntiCheatMode.NCP));
    public final YawSpoofHandler baritoneSearchHelper4;
    public boolean flag;
    public boolean flag2;

    public AntiCheat() {
        super("AntiCheat", "Manages the client's behavior on different anti-cheats.", Category.CLIENT, new String[0]);
        this.baritoneSearchHelper4 = new YawSpoofHandler(this);
        eventBus.register(this);
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        this.baritoneSearchHelper4.setYawSpoofActive(false);
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        this.flag2 = motionEvent.getPhase() == KeyPearlMode.Pre;
        if (motionEvent.getPhase() == KeyPearlMode.Pre && this.movementSync.getValue().booleanValue()) {
            if ((isF2b2t() && (minecraftClient.currentScreen instanceof HandledScreen) && !this.baritoneSearchHelper4.isYawSpoofActive()) || PlayerUtils.isReallyFallFlying()) {
                return;
            }
            if (velocity.isToggled()) {
                VelocityModeHandler velocityHelper1894 = velocity.getVelocityHelper1894();
                if ((velocityHelper1894 instanceof AntiCheatVelocityHandler) && ((AntiCheatVelocityHandler) velocityHelper1894).is951()) {
                    return;
                }
            }
            float intBitsToFloat = val * Float.intBitsToFloat(1073741824) * (this.flag ? -1 : 1);
            this.flag = !this.flag;
            motionEvent.setPitch(MathHelper.clamp(motionEvent.getPitch() + intBitsToFloat, -MathConstants.num2, MathConstants.num2));
        }
    }

    @Listen(get219= 999)
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        PlayerActionC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerActionC2SPacket ? (PlayerActionC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerActionC2SPacket) {
            PlayerActionC2SPacket playerActionC2SPacket = packet904;
            if (isF2b2t() && playerActionC2SPacket.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
                PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, playerActionC2SPacket.getPos(), playerActionC2SPacket.getDirection());
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (isF2b2t()) {
            CloseScreenS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof CloseScreenS2CPacket ? (CloseScreenS2CPacket) (channelRead0Event.getPacket()) : null;
            if ((packet904 instanceof CloseScreenS2CPacket) && packet904.getSyncId() == 0) {
                channelRead0Event.cancel();
            }
        }
    }

    public float getYawStep() {
        return this.yawStep.getValue().floatValue();
    }

    public boolean isF2b2t() {
        return this.f2b2t.getValue().booleanValue();
    }

    public YawSpoofHandler getYawSpoofHandler() {
        return this.baritoneSearchHelper4;
    }
}
