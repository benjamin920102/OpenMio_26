package me.mioclient.network.codec;

import me.mioclient.api.ClientServices;
import me.mioclient.network.FriendListSync;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.network.crypto.AesKeyStore;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.api.ClientServices;
import me.mioclient.network.packet.HandshakePacket;
import me.mioclient.network.packet.SelfDestructPacket;
import me.mioclient.util.client.ChatUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import me.mioclient.module.client.IRC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.network.FriendListSync;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SimpleChannelInboundHandler.class */
public class SimpleChannelInboundHandler extends io.netty.channel.SimpleChannelInboundHandler<ByteBuf> {
    public static IRC iRC = (IRC) ClientServices.moduleInjector.getModule(IRC.class);

    public void channelActive(ChannelHandlerContext channelHandlerContext) {
    }

    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        AesKeyStore.reset();
        FriendListSync.resetTrajectoryData();
    }

    /* renamed from: do431, reason: merged with bridge method [inline-methods] */
    public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        if (AesKeyStore.hasKeys()) {
            channelHandlerContext.fireChannelRead(byteBuf.retain());
            return;
        }
        byte[] bArr = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bArr);
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
        byte[] bArr2 = new byte[dataInputStream.readInt()];
        dataInputStream.readFully(bArr2);
        byte[] bArr3 = new byte[16];
        dataInputStream.readFully(bArr3);
        AesKeyStore.setKeys(bArr2, bArr3);
        byte[] byteArray432 = getByteArray432();
        if (byteArray432 == null) {
            new SelfDestructPacket().handle((ChannelHandlerContext) null);
            return;
        }
        HandshakePacket byteToMessageDecoderHelper_13 = new HandshakePacket();
        byteToMessageDecoderHelper_13.setRank(ClientServices.welcomerHelper.getRank());
        byteToMessageDecoderHelper_13.setUsername(MinecraftClient.getInstance().getSession().getUsername());
        byteToMessageDecoderHelper_13.setSecret(byteArray432);
        byteToMessageDecoderHelper_13.setChannel(iRC.getCapeTexture());
        channelHandlerContext.channel().writeAndFlush(byteToMessageDecoderHelper_13);
        ClientServices.tooltipsHelper.schedule(() -> {
            try {
                ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(ClientCommandRegistry.getPrefix()).arg(String.valueOf(Formatting.GREEN)).format("\u0001Connected to the chat server. Type \u0001irc help to view all commands.")), ChatUtils.createSignature(-836156));
            } catch (Exception e) {
            }
        }, 0);
    }

    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable th) {
        ClientServices.nameTagsManager.setConnectionState(true);
    }

    public static byte[] getByteArray432() {
        FileInputStream fileInputStream = null;
        try {
            File file = new File(new StringTemplate().arg(File.separator).arg(File.separator).arg(System.getProperty("user.home")).format("\u0001\u0001Mio\u0001.authtoken"));
            if (!file.exists() || file.length() <= 3) {
                if (0 != 0) {
                    try {
                        ((FileInputStream) null).close();
                    } catch (Exception e) {
                    }
                }
                return null;
            }
            fileInputStream = new FileInputStream(file);
            byte[] readAllBytes = fileInputStream.readAllBytes();
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e2) {
                }
            }
            return readAllBytes;
        } catch (Exception e3) {
            if (fileInputStream == null) {
                return null;
            }
            try {
                fileInputStream.close();
                return null;
            } catch (Exception e4) {
                return null;
            }
        } catch (Throwable th) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e5) {
                }
            }
            throw th;
        }
    }
}
