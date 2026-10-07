package me.mioclient.network.codec;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.network.packet.PacketRegistry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

public class ByteToMessageDecoder extends io.netty.handler.codec.ByteToMessageDecoder {
    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bArr = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bArr);
        short readShort = new DataInputStream(new ByteArrayInputStream(bArr)).readShort();
        NetworkMessage byteToMessageDecoderHelper609 = PacketRegistry.findByPacketId(readShort);
        if (byteToMessageDecoderHelper609 == null) {
            throw new NullPointerException(new StringTemplate().arg(readShort).format("Couldn't find incoming packet with ID \u0001"));
        }
        list.add(byteToMessageDecoderHelper609.fromBytes(bArr));
    }
}
