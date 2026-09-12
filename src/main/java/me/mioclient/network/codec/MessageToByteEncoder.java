package me.mioclient.network.codec;

import me.mioclient.network.crypto.AesPayloadCipher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MessageToByteEncoder.class */
public class MessageToByteEncoder extends io.netty.handler.codec.MessageToByteEncoder<NetworkMessage> {
    /* renamed from: do2794, reason: merged with bridge method [inline-methods] */
    public void encode(ChannelHandlerContext channelHandlerContext, NetworkMessage byteToMessageDecoderHelper, ByteBuf byteBuf) throws Exception {
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(AesPayloadCipher.encrypt(byteToMessageDecoderHelper.toBytes())));
    }
}
