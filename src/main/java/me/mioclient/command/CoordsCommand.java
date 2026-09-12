package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.util.client.ChatUtils;

import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_11.class */
public final class CoordsCommand extends CommandFeature {
    public CoordsCommand() {
        super("coords");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", new ExamplesArgumentType_3()).executes(commandContext -> {
            do1387((String) commandContext.getArgument("name", String.class), getFormattedCoordinates());
            return 1;
        })).executes(commandContext2 -> {
            try {
                minecraftClient.keyboard.setClipboard(getFormattedCoordinates());
                ChatUtils.sendMessage(Text.literal("Your position has been copied to the clipboard."), ChatUtils.createSignature(-1));
                return 1;
            } catch (Exception e) {
                return 1;
            }
        });
    }

    public String getFormattedCoordinates() {
        if (minecraftClient.player == null) {
            return "";
        }
        return new StringTemplate().arg((int) Math.floor(Math.random() * Double.longBitsToDouble(4666723172467343360L))).arg(WorldUtils.getDimension().getDisplayName()).arg((int) minecraftClient.player.getZ()).arg((int) minecraftClient.player.getY()).arg((int) minecraftClient.player.getX()).format("X: \u0001, Y: \u0001, Z: \u0001 in The \u0001 [\u0001]");
    }

    public void do1387(String str, String str2) {
        minecraftClient.player.networkHandler.sendChatCommand(new StringTemplate().arg(str2).arg(str).format("w \u0001 \u0001"));
    }
}
