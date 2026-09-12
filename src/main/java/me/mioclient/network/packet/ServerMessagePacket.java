package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.network.codec.NetworkMessage;
import me.mioclient.util.client.ChatUtils;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ServerMessagePacket.class */
public class ServerMessagePacket implements NetworkMessage {
    public String string;

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
        ChatUtils.sendMessage(Text.literal(this.string), ChatUtils.createSignature(this.string.hashCode()));
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_12962, reason: merged with bridge method [inline-methods] */
    public ServerMessagePacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        ServerMessagePacket byteToMessageDecoderHelper_12 = new ServerMessagePacket();
        byteToMessageDecoderHelper_12.string = dataInputStream2177.readUTF();
        return byteToMessageDecoderHelper_12;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeUTF(this.string);
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 13;
    }
}
