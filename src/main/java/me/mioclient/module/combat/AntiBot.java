package me.mioclient.module.combat;

import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.command.FakePlayerCommand;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import me.mioclient.setting.BooleanSetting;

public class AntiBot extends Module {
    public Setting<Boolean> illegalChars = add(new BooleanSetting("IllegalChars", false));
    public Setting<Boolean> illegalNames = add(new BooleanSetting("IllegalNames", false));

    public AntiBot() {
        super("AntiBot", "Removes the bots that will be spawned by some anti cheats.", Category.COMBAT, new String[0]);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        Player playerEntity = null;
        synchronized (minecraftClient.level.players()) {
            for (Player playerEntity2 : minecraftClient.level.players()) {
                if (isBot(playerEntity2)) {
                    playerEntity = playerEntity2;
                }
            }
        }
        if (playerEntity != null) {
            playerEntity.setRemoved(Entity.RemovalReason.KILLED);
        }
    }

    public boolean isBot(Player playerEntity) {
        if (minecraftClient.player == playerEntity || (playerEntity instanceof FakePlayerCommand.RemotePlayer)) {
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
        PlayerInfo playerListEntry = minecraftClient.player.connection.getPlayerInfo(playerEntity.getGameProfile().id());
        if (!minecraftClient.player.connection.getOnlinePlayers().contains(playerListEntry) || playerListEntry == null) {
            return true;
        }
        return playerListEntry.getProfile() == null && playerEntity.hasCustomName();
    }
}