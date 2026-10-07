package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import me.mioclient.mixin.ducks.DuckSession;

public final class SessionCommand extends CommandFeature {
    public SessionCommand() {
        super("session");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", StringArgumentType.string()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("name", String.class);
            if (str.length() > 16) {
                return 0;
            }
            if (minecraftClient.getConnection().getServerData() != null) {
                minecraftClient.getConnection().getConnection().disconnect(Component.literal(""));
                ServerData serverInfo = minecraftClient.getConnection().getServerData();
                // 26.2: ConnectScreen#startConnecting(Screen, Minecraft, ServerAddress, ServerData, boolean, TransferState).
                ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), minecraftClient, ServerAddress.parseString(serverInfo.ip), serverInfo, true, null);
            }
            ((me.mioclient.mixin.ducks.DuckSession) minecraftClient.getUser()).setUsername(str);
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(str).format("User's name has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        }));
    }
}
