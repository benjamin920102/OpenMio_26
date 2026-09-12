package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import me.mioclient.mixin.ducks.DuckSession;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_22.class */
public final class SessionCommand extends CommandFeature {
    public SessionCommand() {
        super("session");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", StringArgumentType.string()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("name", String.class);
            if (str.length() > 16) {
                return 0;
            }
            if (minecraftClient.getNetworkHandler().getServerInfo() != null) {
                minecraftClient.getNetworkHandler().getConnection().disconnect(Text.of(""));
                ServerInfo serverInfo = minecraftClient.getNetworkHandler().getServerInfo();
                ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), minecraftClient, ServerAddress.parse(serverInfo.address), serverInfo, true, (CookieStorage) null);
            }
            ((me.mioclient.mixin.ducks.DuckSession) minecraftClient.getSession()).setUsername(str);
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(str).format("Session's name has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        }));
    }
}
