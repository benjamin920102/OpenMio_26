package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;

public class FriendRemovePacket implements NetworkMessage {
    public String string;
    public String username;

    public FriendRemovePacket(String str, String str2) {
        this.string = str;
        this.username = str2;
    }

    public FriendRemovePacket() {
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_52795, reason: merged with bridge method [inline-methods] */
    public FriendRemovePacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        FriendRemovePacket byteToMessageDecoderHelper_5 = new FriendRemovePacket();
        byteToMessageDecoderHelper_5.string = dataInputStream2177.readUTF();
        byteToMessageDecoderHelper_5.username = dataInputStream2177.readUTF();
        return byteToMessageDecoderHelper_5;
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
        return (short) 10;
    }
}
