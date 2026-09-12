package me.mioclient.network.packet;

import me.mioclient.api.ClientServices;

import me.mioclient.network.codec.NetworkMessage;

import me.mioclient.api.ClientServices;
import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/NameTagMapPacket.class */
public class NameTagMapPacket implements NetworkMessage {
    public HashMap<String, String> hashMap;

    public NameTagMapPacket() {
    }

    public NameTagMapPacket(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
        ClientServices.nameTagsManager.getUserModes().clear();
        for (Map.Entry<String, String> entry : this.hashMap.entrySet()) {
            ClientServices.nameTagsManager.getUserModes().put(entry.getKey(), entry.getValue());
        }
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getHashMapByteToMessageDecoderHelper3060, reason: merged with bridge method [inline-methods] */
    public NameTagMapPacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        HashMap hashMap = new HashMap();
        int readInt = dataInputStream2177.readInt();
        for (int i = 0; i < readInt; i++) {
            hashMap.put(dataInputStream2177.readUTF(), dataInputStream2177.readUTF());
        }
        return new NameTagMapPacket(hashMap);
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeInt(this.hashMap.size());
        for (String str : this.hashMap.keySet()) {
            dataOutputStream2178.writeUTF(str);
            dataOutputStream2178.writeUTF(this.hashMap.get(str));
        }
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 8;
    }
}
