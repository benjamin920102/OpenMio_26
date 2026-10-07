package me.mioclient.network.codec;

import me.mioclient.network.crypto.AesPayloadCipher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class ByteArrayDecoder extends io.netty.handler.codec.bytes.ByteArrayDecoder {
    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int readableBytes = byteBuf.readableBytes();
        if (readableBytes <= 0) {
            return;
        }
        byte[] bArr = new byte[readableBytes];
        byteBuf.readBytes(bArr);
        list.add(Unpooled.copiedBuffer(AesPayloadCipher.decrypt(bArr)));
    }
}
