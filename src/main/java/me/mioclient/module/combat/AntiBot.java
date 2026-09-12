package me.mioclient.module.combat;

import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.command.FakePlayerCommand;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/AntiBot.class */
public class AntiBot extends Module {
    public Setting<Boolean> illegalChars = add(new BooleanSetting("IllegalChars", false));
    public Setting<Boolean> illegalNames = add(new BooleanSetting("IllegalNames", false));

    public AntiBot() {
        super("AntiBot", "Removes the bots that will be spawned by some anti cheats.", Category.COMBAT, new String[0]);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        PlayerEntity playerEntity = null;
        synchronized (minecraftClient.world.getPlayers()) {
            for (PlayerEntity playerEntity2 : minecraftClient.world.getPlayers()) {
                if (isBot(playerEntity2)) {
                    playerEntity = playerEntity2;
                }
            }
        }
        if (playerEntity != null) {
            playerEntity.setRemoved(Entity.RemovalReason.KILLED);
        }
    }

    public boolean isBot(PlayerEntity playerEntity) {
        if (minecraftClient.player == playerEntity || (playerEntity instanceof FakePlayerCommand.OtherClientPlayerEntity)) {
            return false;
        }
        String string = playerEntity.getName().getString();
        if ((string.length() < 3 || string.length() > 16) && this.illegalNames.getValue().booleanValue()) {
            return true;
        }
        if (this.illegalChars.getValue().booleanValue()) {
            for (char c : string.toLowerCase().toCharArray()) {
                if (!ClientTextRenderer.containsChar("1234567890_qwertyuiopasdfghjklzxcvbnm", c)) {
                    return true;
                }
            }
        }
        PlayerListEntry playerListEntry = minecraftClient.player.networkHandler.getPlayerListEntry(playerEntity.getGameProfile().getId());
        if (!minecraftClient.player.networkHandler.getPlayerList().contains(playerListEntry) || playerListEntry == null) {
            return true;
        }
        return playerListEntry.getProfile() == null && playerEntity.hasCustomName();
    }
}
