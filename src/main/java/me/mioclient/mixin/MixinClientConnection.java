package me.mioclient.mixin;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.network.PacketSendPostEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientAccessor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import me.mioclient.module.misc.NoPacketKick;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified): channel field survives; protocol side check is now
   getReceiving() vs PacketFlow.CLIENTBOUND; sendImmediately/sendInternal and
   PacketCallbacks are gone — send hooks moved to send(Packet, ChannelFutureListener,
   boolean); channelRead0(ChannelHandlerContext, Packet) survives. */
@Mixin({Connection.class})
public abstract class MixinClientConnection {
    private static NoPacketKick nopacketkick;

    private static NoPacketKick getNopacketkick() {

        if (nopacketkick == null && ClientServices.moduleInjector != null) {

            try { nopacketkick = (NoPacketKick) ClientServices.moduleInjector.getModule(NoPacketKick.class); } catch (Exception e) {}

        }

        return nopacketkick;

    }

    @Shadow
    private Channel channel;

    @Inject(method = {"channelRead0*"}, at = {@At("HEAD")}, cancellable = true)
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callbackInfo) {
        if (!this.channel.isOpen() || packet == null) {
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

    /* 26.2 port: Connection.send has 3 overloads now — pin the 3-arg one
       (Packet, ChannelFutureListener, boolean) matching these handlers. */
    private static final ThreadLocal<Boolean> firingPacketSend = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Inject(method = {"send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V"}, at = {@At("HEAD")}, cancellable = true)
    private void sendImmediately(Packet<?> packet, ChannelFutureListener packetCallbacks, boolean z, CallbackInfo callbackInfo) {
        if (this.getReceiving() != PacketFlow.CLIENTBOUND) {
            return;
        }
        /* Re-entrant sends (a PacketSendEvent handler sending another packet) bypass
           the event: the inner packet is already final. Without this the handlers
           recurse until StackOverflowError. Thread-local because sends happen on
           several threads. */
        if (firingPacketSend.get().booleanValue()) {
            return;
        }
        firingPacketSend.set(Boolean.TRUE);
        try {
            PacketSendEvent sendImmediatelyEvent = new PacketSendEvent(packet);
            ClientAccessor.eventBus.fireEvent(sendImmediatelyEvent);
            if (sendImmediatelyEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        } catch (Exception e) {
        } finally {
            firingPacketSend.set(Boolean.FALSE);
        }
    }

    @Inject(method = {"send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V"}, at = {@At("TAIL")})
    private void sendInternal(Packet<?> packet, ChannelFutureListener packetCallbacks, boolean z, CallbackInfo callbackInfo) {
        if (this.getReceiving() != PacketFlow.CLIENTBOUND) {
            return;
        }
        try {
            ClientAccessor.eventBus.fireEvent(new PacketSendPostEvent(packet));
        } catch (Exception e) {
        }
    }

    @Shadow
    public abstract PacketFlow getReceiving();

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
