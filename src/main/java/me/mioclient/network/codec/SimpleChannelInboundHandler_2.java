package me.mioclient.network.codec;

import me.mioclient.api.ClientServices;
import me.mioclient.network.FriendListSync;

import me.mioclient.network.crypto.AesKeyStore;

import me.mioclient.api.ClientServices;

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import me.mioclient.network.FriendListSync;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SimpleChannelInboundHandler_2.class */
public class SimpleChannelInboundHandler_2 extends io.netty.channel.SimpleChannelInboundHandler<NetworkMessage> {
    public final CopyOnWriteArrayList<ScheduledFuture<?>> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

    /* renamed from: do2236, reason: merged with bridge method [inline-methods] */
    public void channelRead0(ChannelHandlerContext channelHandlerContext, NetworkMessage byteToMessageDecoderHelper) {
        byteToMessageDecoderHelper.handle(channelHandlerContext);
        channelHandlerContext.flush();
    }

    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable th) {
        th.printStackTrace();
        channelHandlerContext.close();
        AesKeyStore.reset();
        FriendListSync.resetTrajectoryData();
        ClientServices.nameTagsManager.disconnectFromServer();
        ClientServices.nameTagsManager.setConnectionState(true);
    }
}
