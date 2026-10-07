package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import me.mioclient.setting.BooleanSetting;

public class CoordLogger extends Module {
    public Setting<Boolean> log = add(new BooleanSetting("Log", true));
    public Setting<Boolean> death = add(new BooleanSetting("Death", true));
    public String text;

    public CoordLogger() {
        super("CoordLogger", "Copies your coordinates whenever you log/die.", Category.MISC, new String[0]);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.text = "X: %d, Y: %d, Z: %d in The %s".formatted(Integer.valueOf(minecraftClient.player.getBlockX()), Integer.valueOf(minecraftClient.player.getBlockY()), Integer.valueOf(minecraftClient.player.getBlockZ()), WorldUtils.getDimension().getDisplayName());
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if ((channelRead0Event.getPacket() instanceof ClientboundPlayerCombatKillPacket) && this.death.getValue().booleanValue()) {
            do906();
        }
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        if (minecraftClient.player == null || !this.log.getValue().booleanValue()) {
            return;
        }
        do906();
    }

    public void do906() {
        minecraftClient.keyboardHandler.setClipboard(this.text);
    }
}