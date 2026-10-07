package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;

public class KeepAlivePacket implements NetworkMessage {
    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_32547, reason: merged with bridge method [inline-methods] */
    public KeepAlivePacket fromBytes(byte[] bArr) throws java.io.IOException {
        return new KeepAlivePacket();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        return openOutput().toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 2;
    }
}
