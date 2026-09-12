package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChannelJoinPacket.class */
public class ChannelJoinPacket implements NetworkMessage {
    public String string;

    public ChannelJoinPacket() {
    }

    public ChannelJoinPacket(String str) {
        this.string = str;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_22524, reason: merged with bridge method [inline-methods] */
    public ChannelJoinPacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        ChannelJoinPacket byteToMessageDecoderHelper_2 = new ChannelJoinPacket();
        byteToMessageDecoderHelper_2.string = dataInputStream2177.readUTF();
        return byteToMessageDecoderHelper_2;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeUTF(this.string);
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 4;
    }
}
