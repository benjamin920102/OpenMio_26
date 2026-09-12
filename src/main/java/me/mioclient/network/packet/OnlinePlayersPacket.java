package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.network.codec.NetworkMessage;
import me.mioclient.util.client.ChatUtils;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/OnlinePlayersPacket.class */
public class OnlinePlayersPacket implements NetworkMessage {
    public String[] stringArr;

    public OnlinePlayersPacket() {
    }

    public OnlinePlayersPacket(String[] strArr) {
        this.stringArr = strArr;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
        String formatted = "%d online players: %s".formatted(Integer.valueOf(this.stringArr.length), String.join(", ", this.stringArr));
        ChatUtils.sendMessage(Text.literal(formatted), ChatUtils.createSignature(formatted.hashCode()));
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_749, reason: merged with bridge method [inline-methods] */
    public OnlinePlayersPacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        int readInt = dataInputStream2177.readInt();
        OnlinePlayersPacket byteToMessageDecoderHelper_7 = new OnlinePlayersPacket();
        byteToMessageDecoderHelper_7.stringArr = new String[readInt];
        for (int i = 0; i < readInt; i++) {
            byteToMessageDecoderHelper_7.stringArr[i] = dataInputStream2177.readUTF();
        }
        return byteToMessageDecoderHelper_7;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeInt(this.stringArr.length);
        for (String str : this.stringArr) {
            dataOutputStream2178.writeUTF(str);
        }
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 3;
    }
}
