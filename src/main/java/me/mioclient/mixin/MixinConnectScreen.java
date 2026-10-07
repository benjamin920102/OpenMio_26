package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.network.ConnectEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified via JDK25 javap): ConnectScreen.connect(Minecraft,
   ServerAddress, ServerData, CookieStorage) gone; now
   startConnecting(Screen, Minecraft, ServerAddress, ServerData, boolean, TransferState)
   and connect(Minecraft, ServerAddress, ServerData, TransferState). Hook the
   public entry point, require = 0. */
@Mixin({ConnectScreen.class})

public class MixinConnectScreen {
    @Inject(method = {"startConnecting"}, at = {@At("HEAD")}, require = 0)
    private static void connectHook(net.minecraft.client.gui.screens.Screen screen, Minecraft minecraftClient, ServerAddress serverAddress, ServerData serverInfo, boolean bl, net.minecraft.client.multiplayer.TransferState transferState, CallbackInfo callbackInfo) {
        ServerData serverInfo2 = serverInfo;
        if (serverInfo2 == null) {
            serverInfo2 = new ServerData("mioclient", serverAddress.getHost(), ServerData.Type.OTHER);
        }
        ClientAccessor.eventBus.fireEvent(new ConnectEvent(serverInfo2.ip));
        ClientServices.holeSnapRenderHelper.setServerInfo(serverInfo2);
    }
}
