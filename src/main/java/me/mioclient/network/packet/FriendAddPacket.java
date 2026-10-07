package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;

public class FriendAddPacket implements NetworkMessage {
    public String string;
    public String username;

    public FriendAddPacket() {
    }

    public FriendAddPacket(String str, String str2) {
        this.string = str;
        this.username = str2;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_42720, reason: merged with bridge method [inline-methods] */
    public FriendAddPacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        FriendAddPacket byteToMessageDecoderHelper_4 = new FriendAddPacket();
        byteToMessageDecoderHelper_4.string = dataInputStream2177.readUTF();
        byteToMessageDecoderHelper_4.username = dataInputStream2177.readUTF();
        return byteToMessageDecoderHelper_4;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeUTF(this.string);
        dataOutputStream2178.writeUTF(this.username);
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 9;
    }
}
