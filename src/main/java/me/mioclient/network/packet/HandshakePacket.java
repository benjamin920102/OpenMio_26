package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;

public class HandshakePacket implements NetworkMessage {
    public String string;
    public int num;
    public String string2;
    public byte[] byteArr;

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_13971, reason: merged with bridge method [inline-methods] */
    public HandshakePacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        HandshakePacket byteToMessageDecoderHelper_13 = new HandshakePacket();
        byteToMessageDecoderHelper_13.num = dataInputStream2177.readInt();
        byteToMessageDecoderHelper_13.string2 = dataInputStream2177.readUTF();
        byteToMessageDecoderHelper_13.string = dataInputStream2177.readUTF();
        int readInt = dataInputStream2177.readInt();
        if (readInt <= 0 || readInt > 1000) {
            byteToMessageDecoderHelper_13.byteArr = null;
        } else {
            byteToMessageDecoderHelper_13.byteArr = new byte[readInt];
            dataInputStream2177.readFully(byteToMessageDecoderHelper_13.byteArr);
        }
        return byteToMessageDecoderHelper_13;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeInt(this.num);
        dataOutputStream2178.writeUTF(this.string2);
        dataOutputStream2178.writeUTF(this.string);
        if (this.byteArr == null) {
            dataOutputStream2178.writeInt(0);
        } else {
            dataOutputStream2178.writeInt(this.byteArr.length);
            dataOutputStream2178.write(this.byteArr);
        }
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 1;
    }

    public void setRank(int i) {
        this.num = i;
    }

    public void setSecret(byte[] bArr) {
        this.byteArr = bArr;
    }

    public void setChannel(String str) {
        if (str == null) {
            this.string2 = "none";
        } else {
            this.string2 = str;
        }
    }

    public void setUsername(String str) {
        this.string = str;
    }
}
