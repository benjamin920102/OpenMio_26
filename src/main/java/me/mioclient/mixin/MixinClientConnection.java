package me.mioclient.mixin;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.network.PacketSendPostEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.network.PacketSendPostEvent;
import me.mioclient.module.misc.NoPacketKick;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({ClientConnection.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinClientConnection.class */
public class MixinClientConnection {
    private static NoPacketKick nopacketkick;

    private static NoPacketKick getNopacketkick() {

        if (nopacketkick == null && ClientServices.moduleInjector != null) {

            try { nopacketkick = (NoPacketKick) ClientServices.moduleInjector.getModule(NoPacketKick.class); } catch (Exception e) {}

        }

        return nopacketkick;

    }

    @Shadow
    private Channel field_11651;

    @Shadow
    @Final
    private NetworkSide field_11643;

    @Inject(method = {"channelRead0*"}, at = {@At("HEAD")}, cancellable = true)
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callbackInfo) {
        if (!this.field_11651.isOpen() || packet == null) {
            return;
        }
        try {
            PacketReceiveEvent channelRead0Event = new PacketReceiveEvent(packet);
            ClientAccessor.eventBus.fireEvent(channelRead0Event);
            if (channelRead0Event.isCancelled()) {
                callbackInfo.cancel();
            }
        } catch (Exception e) {
        }
    }

    @Inject(method = {"sendImmediately"}, at = {@At("HEAD")}, cancellable = true)
    private void sendImmediately(Packet<?> packet, PacketCallbacks packetCallbacks, boolean z, CallbackInfo callbackInfo) {
        if (this.field_11643 != NetworkSide.CLIENTBOUND) {
            return;
        }
        try {
            PacketSendEvent sendImmediatelyEvent = new PacketSendEvent(packet);
            ClientAccessor.eventBus.fireEvent(sendImmediatelyEvent);
            if (sendImmediatelyEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        } catch (Exception e) {
        }
    }

    @Inject(method = {"sendInternal"}, at = {@At("TAIL")})
    private void sendInternal(Packet<?> packet, PacketCallbacks packetCallbacks, boolean z, CallbackInfo callbackInfo) {
        if (this.field_11643 != NetworkSide.CLIENTBOUND) {
            return;
        }
        try {
            ClientAccessor.eventBus.fireEvent(new PacketSendPostEvent(packet));
        } catch (Exception e) {
        }
    }

    @Inject(method = {"exceptionCaught"}, at = {@At("HEAD")}, cancellable = true)
    private void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable th, CallbackInfo callbackInfo) {
        NoPacketKick nopacketkick_lcl = getNopacketkick();

        if (nopacketkick_lcl == null) return;

        if (nopacketkick_lcl.isToggled()) {
            if (nopacketkick_lcl.message.getValue().booleanValue()) {
                try {
                    ChatUtils.sendPrefixedMessage(nopacketkick_lcl.getText1411(th), ChatUtils.signatureOfModule(nopacketkick), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
                } catch (Throwable th2) {
                }
            }
            callbackInfo.cancel();
        }
    }
}
