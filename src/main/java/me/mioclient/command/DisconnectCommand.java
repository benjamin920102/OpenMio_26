package me.mioclient.command;

import me.mioclient.module.exploit.IllegalDisconnect;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class DisconnectCommand extends CommandFeature {
    public DisconnectCommand() {
        super("disconnect");
        setAliases("dc");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("illegal").executes(commandContext -> {
            if (isNotInGame()) {
                return 1;
            }
            IllegalDisconnect.sendDisconnectPacket();
            return 1;
        })).executes(commandContext2 -> {
            if (isNotInGame()) {
                return 1;
            }
            minecraftClient.player.connection.getConnection().disconnect(Component.literal("Disconnected"));
            return 1;
        });
    }
}
