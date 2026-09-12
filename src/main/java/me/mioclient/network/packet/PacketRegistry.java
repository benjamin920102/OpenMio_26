package me.mioclient.network.packet;

import me.mioclient.network.codec.NetworkMessage;

import me.mioclient.network.codec.NetworkMessage;

import java.util.ArrayList;
import java.util.List;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PacketRegistry.class */
public class PacketRegistry {
    public static List<NetworkMessage> list = new ArrayList();

    public static NetworkMessage findByPacketId(short s) {
        for (NetworkMessage byteToMessageDecoderHelper : list) {
            if (byteToMessageDecoderHelper.getPacketId() == s) {
                return byteToMessageDecoderHelper;
            }
        }
        return null;
    }

    static {
        list.add(new HandshakePacket());
        list.add(new KeepAlivePacket());
        list.add(new ChannelJoinPacket());
        list.add(new FriendListSyncMessage());
        list.add(new FriendAddPacket());
        list.add(new FriendRemovePacket());
        list.add(new OnlinePlayersPacket());
        list.add(new IrcChatPacket());
        list.add(new NameTagMapPacket());
        list.add(new SelfDestructPacket());
        list.add(new FriendListPacket());
        list.add(new ServerMessagePacket());
        list.add(new PlayerPositionPacket());
        for (NetworkMessage byteToMessageDecoderHelper : list) {
            for (NetworkMessage byteToMessageDecoderHelper2 : list) {
                if (byteToMessageDecoderHelper != byteToMessageDecoderHelper2 && byteToMessageDecoderHelper.getPacketId() == byteToMessageDecoderHelper2.getPacketId()) {
                    throw new java.lang.RuntimeException("Duplicate packet ID (%s/%s)".formatted(byteToMessageDecoderHelper.getClass().getName(), byteToMessageDecoderHelper2.getClass().getName()));
                }
            }
        }
    }
}
