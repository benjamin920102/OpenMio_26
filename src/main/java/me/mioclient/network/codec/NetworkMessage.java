package me.mioclient.network.codec;

import io.netty.channel.ChannelHandlerContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/NetworkMessage.class */
public interface NetworkMessage {

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/ByteToMessageDecoderHelper$DataOutputStream.class */
    public static class DataOutputStream extends java.io.DataOutputStream {
        public final ByteArrayOutputStream byteArrayOutputStream;

        public DataOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
            super(byteArrayOutputStream);
            this.byteArrayOutputStream = byteArrayOutputStream;
        }

        public byte[] toByteArray() {
            return this.byteArrayOutputStream.toByteArray();
        }
    }

    void handle(ChannelHandlerContext channelHandlerContext);

    NetworkMessage fromBytes(byte[] bArr) throws java.io.IOException;

    byte[] toBytes() throws java.io.IOException;

    short getPacketId();

    default DataInputStream openInput(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
        dataInputStream.readShort();
        return dataInputStream;
    }

    default DataOutputStream openOutput() throws java.io.IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new ByteArrayOutputStream());
        dataOutputStream.writeShort(getPacketId());
        return dataOutputStream;
    }
}
