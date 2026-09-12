package me.mioclient.module.player;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.util.time.Stopwatch;

import java.util.concurrent.TimeUnit;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/AutoFish.class */
public class AutoFish extends Module {
    public Setting<Float> castDelay = add(new NumberSetting<>("CastDelay", 2.0f, 1.0f, 5.0f).setSuffix("s"));
    public final Stopwatch stopwatch;

    public AutoFish() {
        super("AutoFish", "Fishes.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.stopwatch.reset();
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        PlaySoundS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof PlaySoundS2CPacket ? (PlaySoundS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof PlaySoundS2CPacket) {
            PlaySoundS2CPacket playSoundS2CPacket = packet904;
            if (!playSoundS2CPacket.getSound().matches(registryKey -> {
                return registryKey.getValue().toString().equalsIgnoreCase("minecraft:entity.fishing_bobber.splash");
            }) || minecraftClient.player.fishHook == null) {
                return;
            }
            if (new Vec3d(playSoundS2CPacket.getX(), playSoundS2CPacket.getY(), playSoundS2CPacket.getZ()).distanceTo(minecraftClient.player.fishHook.getPos()) <= Double.longBitsToDouble(4616189618054758400L)) {
                minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.MAIN_HAND);
                minecraftClient.player.swingHand(Hand.MAIN_HAND);
                this.stopwatch.reset();
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (minecraftClient.player.getMainHandStack().isOf(Items.FISHING_ROD)) {
            if (!this.stopwatch.hasElapsed(Double.longBitsToDouble(4607182418800017408L), TimeUnit.MINUTES)) {
                if (minecraftClient.player.fishHook != null) {
                    return;
                }
                if (!this.stopwatch.hasElapsed(this.castDelay.getValue().floatValue(), TimeUnit.SECONDS)) {
                    return;
                }
            }
            minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.MAIN_HAND);
            minecraftClient.player.swingHand(Hand.MAIN_HAND);
            this.stopwatch.reset();
        }
    }
}
