package me.mioclient.network.packet;

import me.mioclient.api.ClientServices;
import me.mioclient.network.FriendListSync;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;

public class FriendListPacket implements NetworkMessage {
    public String[] stringArr;
    public boolean flag;

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
        ClientServices.nameTagsManager.setSharedSecret(this.stringArr);
        FriendListSync.scheduleFriendSync(channelHandlerContext.channel());
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_63066, reason: merged with bridge method [inline-methods] */
    public FriendListPacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        FriendListPacket byteToMessageDecoderHelper_6 = new FriendListPacket();
        byteToMessageDecoderHelper_6.flag = dataInputStream2177.readBoolean();
        int readInt = dataInputStream2177.readInt();
        byteToMessageDecoderHelper_6.stringArr = new String[readInt];
        for (int i = 0; i < readInt; i++) {
            byteToMessageDecoderHelper_6.stringArr[i] = dataInputStream2177.readUTF();
        }
        return byteToMessageDecoderHelper_6;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeBoolean(this.flag);
        dataOutputStream2178.writeInt(this.stringArr.length);
        for (String str : this.stringArr) {
            dataOutputStream2178.writeUTF(str);
        }
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 12;
    }
}
