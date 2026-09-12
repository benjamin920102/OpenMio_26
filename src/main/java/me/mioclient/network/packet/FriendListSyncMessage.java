package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FriendListSyncMessage.class */
public class FriendListSyncMessage implements NetworkMessage {
    public String username;
    public String[] stringArr;

    public FriendListSyncMessage() {
    }

    public FriendListSyncMessage(String str, String[] strArr) {
        this.username = str;
        this.stringArr = strArr;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_10829, reason: merged with bridge method [inline-methods] */
    public FriendListSyncMessage fromBytes(byte[] bArr) throws java.io.IOException {
        return null;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeUTF(this.username);
        dataOutputStream2178.writeInt(this.stringArr.length);
        for (String str : this.stringArr) {
            dataOutputStream2178.writeUTF(str);
        }
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 7;
    }
}
