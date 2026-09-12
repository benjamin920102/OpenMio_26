package me.mioclient.command;

import me.mioclient.module.exploit.IllegalDisconnect;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_18.class */
public final class DisconnectCommand extends CommandFeature {
    public DisconnectCommand() {
        super("disconnect");
        setAliases("dc");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
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
            minecraftClient.player.networkHandler.getConnection().disconnect(Text.of("Disconnected"));
            return 1;
        });
    }
}
