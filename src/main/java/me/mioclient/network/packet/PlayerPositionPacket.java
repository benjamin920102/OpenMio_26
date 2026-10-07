package me.mioclient.network.packet;

import me.mioclient.event.events.client.CoordLogReceiveEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.network.codec.NetworkMessage;
import me.mioclient.network.irc.CoordLogEntry;

import me.mioclient.api.ClientAccessor;

import io.netty.channel.ChannelHandlerContext;
import java.io.DataInputStream;

public class PlayerPositionPacket implements NetworkMessage {
    public String username;
    public String string;
    public int num;
    public int num2;
    public int num3;

    public PlayerPositionPacket() {
    }

    public PlayerPositionPacket(String str, int i, int i2, int i3) {
        this.string = str;
        this.num = i;
        this.num2 = i2;
        this.num3 = i3;
    }

    public PlayerPositionPacket(String str, String str2, int i, int i2, int i3) {
        this(str2, i, i2, i3);
        this.username = str;
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
        try {
            CoordLogEntry spawnTimeHelper = new CoordLogEntry(this.username, this.string, this.num, this.num2, this.num3);
            spawnTimeHelper.reset();
            CoordLogReceiveEvent spawnTimeHelperEvent = new CoordLogReceiveEvent(spawnTimeHelper);
            ClientAccessor.eventBus.fireEvent(spawnTimeHelperEvent);
            if (!spawnTimeHelperEvent.isCancelled()) {
                synchronized (ClientServices.nameTagsManager.getCoordLogEntries()) {
                    ClientServices.nameTagsManager.getCoordLogEntries().add(spawnTimeHelper);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_8434, reason: merged with bridge method [inline-methods] */
    public PlayerPositionPacket fromBytes(byte[] bArr) throws java.io.IOException {
        DataInputStream dataInputStream2177 = openInput(bArr);
        return new PlayerPositionPacket(dataInputStream2177.readUTF(), dataInputStream2177.readUTF(), dataInputStream2177.readInt(), dataInputStream2177.readInt(), dataInputStream2177.readInt());
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        NetworkMessage.DataOutputStream dataOutputStream2178 = openOutput();
        dataOutputStream2178.writeUTF(this.username == null ? "" : this.username);
        dataOutputStream2178.writeUTF(this.string);
        dataOutputStream2178.writeInt(this.num);
        dataOutputStream2178.writeInt(this.num2);
        dataOutputStream2178.writeInt(this.num3);
        return dataOutputStream2178.toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 6;
    }
}
