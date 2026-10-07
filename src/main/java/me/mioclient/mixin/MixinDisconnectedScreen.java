package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.RenderConstants;
import me.mioclient.module.misc.AutoReconnect;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.mioclient.module.combat.AutoLog;

@Mixin({DisconnectedScreen.class})

public class MixinDisconnectedScreen extends Screen {
    private static AutoReconnect autoreconnect;

    private static AutoReconnect getAutoreconnect() {

        if (autoreconnect == null && ClientServices.moduleInjector != null) {

            try { autoreconnect = (AutoReconnect) ClientServices.moduleInjector.getModule(AutoReconnect.class); } catch (Exception e) {}

        }

        return autoreconnect;

    }

    @Unique
    private long startTime;

    /* 26.2 port (verified via JDK25 javap): DisconnectedScreen.info -> details;
       grid -> layout (LinearLayout, arrangeElements); initTabNavigation gone;
       ConnectScreen.connect -> startConnecting; render -> extractRenderState. */
    @Shadow
    @Final
    private DisconnectionDetails details;

    @Shadow
    @Final
    private LinearLayout layout;

    @Unique
    private Button reconnectButton;

    @Unique
    private Button autoReconnectButton;

    protected MixinDisconnectedScreen(Component text) {
        super(text);
    }

    @Inject(method = {"init"}, at = {@At("TAIL")}, require = 0)
    private void initHook(CallbackInfo callbackInfo) {
        AutoReconnect autoreconnect_lcl = getAutoreconnect();

        if (autoreconnect_lcl == null) return;

        this.startTime = System.currentTimeMillis();
        Button.Builder width = Button.builder(Component.literal("Reconnect"), buttonWidget -> {
            ServerData serverInfo2622 = ClientServices.holeSnapRenderHelper.getServerInfo();
            if (serverInfo2622 != null) {
                ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), this.minecraft, ServerAddress.parseString(serverInfo2622.ip), serverInfo2622, true, null);
            }
        }).width(RenderConstants.num);
        Button.Builder width2 = Button.builder(Component.literal("AutoReconnect"), buttonWidget2 -> {
            if (!autoreconnect_lcl.isToggled()) {
                ClientServices.holeSnapRenderHelper.flag3 = false;
            }
            autoreconnect_lcl.toggle();
            this.startTime = System.currentTimeMillis();
            buttonWidget2.setFocused(false);
        }).width(RenderConstants.num);
        this.reconnectButton = width.build();
        this.autoReconnectButton = width2.build();
        this.layout.arrangeElements();
        addRenderableWidget(this.reconnectButton);
        addRenderableWidget(this.autoReconnectButton);
    }

    @Override
    public void tick() {
        AutoReconnect autoreconnect_lcl = getAutoreconnect();

        if (autoreconnect_lcl == null) return;

        ServerData serverInfo2622;
        String string = this.details.reason().getString();
        if (string.contains("Mio") && string.contains("[AutoLog]")) {
            this.autoReconnectButton.setX(-1000);
            this.autoReconnectButton.setY(-1000);
        } else if (autoreconnect_lcl.isToggled() && getAutoReconnectTime(autoreconnect) <= 0.0f && (serverInfo2622 = ClientServices.holeSnapRenderHelper.getServerInfo()) != null) {
            ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), this.minecraft, ServerAddress.parseString(serverInfo2622.ip), serverInfo2622, true, null);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, int i, int i2, float f) {
        String string = this.details.reason().getString();
        if (string.contains("Mio") && string.contains("[AutoLog]")) {
            this.autoReconnectButton.setX(-1000);
            this.autoReconnectButton.setY(-1000);
        } else {
            this.autoReconnectButton.setMessage(getAutoReconnectText());
            this.autoReconnectButton.setFocused(false);
            super.extractRenderState(drawContext, i, i2, f);
        }
    }

    private Component getAutoReconnectText() {
        AutoReconnect autoreconnect_lcl = getAutoreconnect();

        if (autoreconnect_lcl == null) return null;

        MutableComponent literal = Component.literal("AutoReconnect ");
        String string = this.details.reason().getString();
        return ClientServices.holeSnapRenderHelper.flag3 ? literal.append(String.valueOf(ChatFormatting.RED) + "AutoLogged") : autoreconnect_lcl.isToggled() && (!string.contains("Mio") || !string.contains("[AutoLog]")) ? literal.append(String.valueOf(ChatFormatting.GREEN) + "%.1fs".formatted(Float.valueOf(getAutoReconnectTime(autoreconnect)))) : literal.append(String.valueOf(ChatFormatting.RED) + "OFF");
    }

    private float getAutoReconnectTime(AutoReconnect autoReconnect) {
        if (autoReconnect == null) {
            return 0.1f;
        }
        return Math.max(autoReconnect.delay.getValue().floatValue() - (((float) (System.currentTimeMillis() - this.startTime)) / 1000.0f), 0.0f);
    }
}
