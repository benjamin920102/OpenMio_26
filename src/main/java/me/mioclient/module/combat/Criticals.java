package me.mioclient.module.combat;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.module.movement.velocity.AntiCheatVelocityHandler;
import me.mioclient.util.client.InteractPacketReader;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.movement.velocity.AntiCheatVelocityHandler;
import me.mioclient.api.NamedValue;
import me.mioclient.network.PacketSender;
import me.mioclient.util.client.InteractPacketReader;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/Criticals.class */
public class Criticals extends Module {
    public static final Aura aura = (Aura) ClientServices.moduleInjector.getModule(Aura.class);
    public Setting<CriticalsMode> mode = add(new EnumSetting<>("Mode", CriticalsMode.PACKET));
    public Setting<Boolean> pauseOnMove = add(new BooleanSetting("PauseOnMove", false));

    /* JADX INFO: Access modifiers changed from: protected */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/Criticals$CriticalsMode.class */
    public enum CriticalsMode implements NamedValue {
        PACKET("Packet"),
        NCP("NCP"),
        GRIM("Grim"),
        GRIMV3("2b2t");

        public final String name;

        CriticalsMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Criticals() {
        super("Criticals", "Turns your hits into critical ones.", Category.COMBAT, new String[0]);
        this.pauseOnMove.addVisibilityPredicate(bool -> {
            return this.mode.getValue() != CriticalsMode.GRIMV3;
        });
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return this.mode.getValue().getName();
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        Entity entity2610;
        PlayerInteractEntityC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractEntityC2SPacket ? (PlayerInteractEntityC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket = packet904;
            if (!shouldProcessCriticalPacket(playerInteractEntityC2SPacket) || (entity2610 = InteractPacketReader.resolvePacketEntity(playerInteractEntityC2SPacket)) == null) {
                return;
            }
            if (!sendImmediatelyEvent.isCancelled()) {
                do906();
            }
            minecraftClient.execute(() -> {
                minecraftClient.player.addCritParticles(entity2610);
            });
        }
    }

    public void do906() {
        float[] fArr = {minecraftClient.player.getYaw(), minecraftClient.player.getPitch()};
        if (aura.entity != null && aura.isToggled()) {
            fArr = RotationManager.limitYawSpeed(RotationManager.getRotationsTo(aura.entity), ClientServices.RotationManager.getYawStep());
        }
        switch (this.mode.getValue().ordinal()) {
            case 1:
                if (PlayerUtils.isInCobweb(minecraftClient.player)) {
                    sendCriticalOffsets(Double.longBitsToDouble(4589168748072235207L), 0.0d, Double.longBitsToDouble(4578359381184846234L), 0.0d);
                    return;
                } else {
                    sendCriticalOffsets(Double.longBitsToDouble(4592590756007337001L), Double.longBitsToDouble(4592590853854343945L));
                    return;
                }
            case 2:
                PacketSender.sendFullMove(minecraftClient.player.getX(), minecraftClient.player.getY() - Double.longBitsToDouble(4517329193108106637L), minecraftClient.player.getZ(), fArr[0], fArr[1], false);
                return;
            case 3:
                float clamp = Math.clamp(fArr[1], Float.intBitsToFloat(-1028390912) + AntiCheat.val, Float.intBitsToFloat(1119092736) - AntiCheat.val);
                PacketSender.sendFullMove(minecraftClient.player.getX(), minecraftClient.player.getY() + Double.longBitsToDouble(4589175226049939217L), minecraftClient.player.getZ(), fArr[0], clamp + AntiCheat.val, false);
                PacketSender.sendFullMove(minecraftClient.player.getX(), minecraftClient.player.getY() + Double.longBitsToDouble(4586718062093245874L), minecraftClient.player.getZ(), fArr[0], clamp - AntiCheat.val, false);
                return;
            default:
                sendCriticalOffsets(Double.longBitsToDouble(4589168748072235207L), 0.0d);
                return;
        }
    }

    public boolean is907() {
        if ((this.pauseOnMove.getValue().booleanValue() || this.mode.getValue() == CriticalsMode.GRIMV3) && MovementUtils.is2181()) {
            return false;
        }
        if (this.mode.getValue() == CriticalsMode.GRIMV3) {
            return minecraftClient.player.isOnGround() && AntiCheatVelocityHandler.isNearGroundCollision();
        }
        if (this.mode.getValue() != CriticalsMode.GRIM || minecraftClient.player.isOnGround()) {
            return minecraftClient.player.isOnGround() && minecraftClient.player.verticalCollision && !minecraftClient.player.isInLava() && !minecraftClient.player.isTouchingWater();
        }
        return true;
    }

    public void sendCriticalOffsets(double... dArr) {
        for (double d : dArr) {
            PacketSender.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(minecraftClient.player.getX(), minecraftClient.player.getY() + d, minecraftClient.player.getZ(), false));
        }
    }

    public boolean shouldProcessCriticalPacket(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket) {
        if (InteractPacketReader.getCrystalInteractionType(playerInteractEntityC2SPacket) != InteractPacketReader.LegacyCrystalMode.ATTACK || !is907()) {
            return false;
        }
        Entity entity2610 = InteractPacketReader.resolvePacketEntity(playerInteractEntityC2SPacket);
        return ((aura.isToggled() && aura.flag2 && this.mode.getValue() != CriticalsMode.GRIM) || entity2610 == null || !entity2610.isAlive() || (entity2610 instanceof EndCrystalEntity)) ? false : true;
    }
}
