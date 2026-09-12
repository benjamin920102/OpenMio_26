package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.network.PacketSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import me.mioclient.setting.StringSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/AutoSign.class */
public class AutoSign extends Module {
    public static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public Setting<String> line1 = add(new StringSetting("Line1", "get mioclient"));
    public Setting<String> line2 = add(new StringSetting("Line2", "at"));
    public Setting<String> line3 = add(new StringSetting("Line3", "www.me.mioclient.me"));
    public Setting<String> line4 = add(new StringSetting("Line4", ":p"));

    public AutoSign() {
        super("AutoSign", "Puts stuff on your signs automatically.", Category.MISC, new String[0]);
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        SignEditorOpenS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof SignEditorOpenS2CPacket ? (SignEditorOpenS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof SignEditorOpenS2CPacket) {
            SignEditorOpenS2CPacket signEditorOpenS2CPacket = packet904;
            PacketSender.sendPacket(new UpdateSignC2SPacket(signEditorOpenS2CPacket.getPos(), signEditorOpenS2CPacket.isFront(), resolveSignText(this.line1), resolveSignText(this.line2), resolveSignText(this.line3), resolveSignText(this.line4)));
            channelRead0Event.cancel();
        }
    }

    public String resolveSignText(Setting<String> setting) {
        String replace = setting.getValue().replace("<date>", dateFormat.format(Calendar.getInstance().getTime())).replace("<name>", minecraftClient.player.getName().getString());
        return replace.substring(0, Math.min(16, replace.length()));
    }
}
