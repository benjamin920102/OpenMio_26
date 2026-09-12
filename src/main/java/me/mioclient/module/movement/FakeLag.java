package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.NamedValue;
import me.mioclient.network.PacketSender;
import me.mioclient.render.shape.BoxRenderer;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/FakeLag.class */
public class FakeLag extends Module {
    public final Queue<Packet<?>> queue;
    public Setting<FakeLagMode> mode = add(new EnumSetting<>("Mode", FakeLagMode.BLINK));
    public Setting<Integer> timeout = add(new NumberSetting<>("Timeout", 200, 50, 1000).setSuffix("ms"));
    public Setting<Boolean> packets = add(new BooleanSetting("Packets", false).setDependent());
    public Setting<Boolean> movement = add(new BooleanSetting("Movement", true).requiresSettings(this.packets));
    public Setting<Boolean> interact = add(new BooleanSetting("Interact", true).requiresSettings(this.packets));
    public Setting<Boolean> swing = add(new BooleanSetting("Swing", true).requiresSettings(this.packets));
    public Setting<Boolean> swap = add(new BooleanSetting("Swap", true).requiresSettings(this.packets));
    public Setting<Boolean> interactEntity = add(new BooleanSetting("InteractEntity", false).requiresSettings(this.packets));
    public Setting<Boolean> digging = add(new BooleanSetting("Digging", false).requiresSettings(this.packets));
    public Setting<Boolean> all = add(new BooleanSetting("All", false).requiresSettings(this.packets));
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", false).setVisible());
    public Setting<Float> disableTime = add(new NumberSetting<>("DisableTime", 1.0f, 0.0f, 5.0f).setSuffix("s").requiresSettings(this.autoDisable));
    public Setting<Boolean> render = add(new BooleanSetting("Render", true).setVisible());
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(1354603007, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(1186830847, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 2.0f, v -> this.render.isShown()));
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public Box box;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/FakeLag$FakeLagMode.class */
    public enum FakeLagMode implements NamedValue {
        BLINK("Blink"),
        PULSE("Pulse");

        public final String name;

        FakeLagMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public FakeLag() {
        super("FakeLag", "Cancels movement packets until toggled off.", Category.MOVEMENT, new String[0]);
        this.queue = new ArrayDeque();
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.queue.clear();
        if (isNotInGame()) {
            disable();
            return;
        }
        this.box = minecraftClient.player.getBoundingBox();
        this.stopwatch.reset();
        this.stopwatch2.reset();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (isNotInGame()) {
            return;
        }
        do604();
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.mode.getValue() == FakeLagMode.PULSE) {
            if (this.stopwatch2.hasElapsedMs(this.timeout.getValue().intValue())) {
                do604();
                this.stopwatch2.reset();
            }
        }
        if (this.autoDisable.getValue().booleanValue()) {
            if (this.stopwatch.hasElapsed(this.disableTime.getValue().floatValue(), TimeUnit.SECONDS)) {
                toggle();
            }
        }
    }

    @Listen(get219= RenderConstants.num5)
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (!shouldQueuePacket(sendImmediatelyEvent.getPacket()) || sendImmediatelyEvent.isCancelled()) {
            return;
        }
        this.queue.add(sendImmediatelyEvent.getPacket());
        sendImmediatelyEvent.cancel();
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if ((channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) && this.mode.getValue() == FakeLagMode.PULSE) {
            this.stopwatch2.setTime(-1L);
        }
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (this.render.getValue().booleanValue()) {
            BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), this.box, this.fill.getValue());
            BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), this.box, this.outline.getValue(), this.lineWidth.getValue().floatValue());
        }
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        disable();
    }

    public boolean shouldQueuePacket(Packet<?> packet) {
        return ((packet instanceof PlayerMoveC2SPacket) && this.movement.getValue().booleanValue()) || ((packet instanceof PlayerInteractBlockC2SPacket) && this.interact.getValue().booleanValue()) || (((packet instanceof PlayerInteractEntityC2SPacket) && this.interactEntity.getValue().booleanValue()) || (((packet instanceof PlayerActionC2SPacket) && this.digging.getValue().booleanValue()) || (((packet instanceof HandSwingC2SPacket) && this.swing.getValue().booleanValue()) || (((packet instanceof UpdateSelectedSlotC2SPacket) && this.swap.getValue().booleanValue()) || (packet instanceof CommonPongC2SPacket) || this.all.getValue().booleanValue()))));
    }

    public void do604() {
        while (!this.queue.isEmpty()) {
            PacketSender.sendPacketDirect(this.queue.poll());
        }
        this.box = minecraftClient.player.getBoundingBox();
    }
}
