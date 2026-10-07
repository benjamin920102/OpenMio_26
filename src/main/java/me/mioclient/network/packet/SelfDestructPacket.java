package me.mioclient.network.packet;

import me.mioclient.api.ClientServices;

import me.mioclient.network.codec.NetworkMessage;

import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.client.Minecraft;

public class SelfDestructPacket implements NetworkMessage {
    @Override // me.mioclient.network.codec.NetworkMessage
    public void handle(ChannelHandlerContext channelHandlerContext) {
        if (ClientServices.welcomerHelper.getRank() <= 3) {
            return;
        }
        for (Field field : Minecraft.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                field.set(Modifier.isStatic(field.getModifiers()) ? null : Minecraft.getInstance(), null);
            } catch (IllegalAccessException e) {
            }
        }
        do {
        } while (this != null);
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    /* renamed from: getByteToMessageDecoderHelper_11858, reason: merged with bridge method [inline-methods] */
    public SelfDestructPacket fromBytes(byte[] bArr) throws java.io.IOException {
        return new SelfDestructPacket();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public byte[] toBytes() throws java.io.IOException {
        return openOutput().toByteArray();
    }

    @Override // me.mioclient.network.codec.NetworkMessage
    public short getPacketId() {
        return (short) 11;
    }
}
