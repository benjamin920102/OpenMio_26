package me.mioclient.event.events.network;

import me.mioclient.mode.combat.KeyPearlMode;

import me.mioclient.event.Event;

import me.mioclient.mode.combat.KeyPearlMode;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/AddMessageEvent.class */
public class AddMessageEvent extends Event {
    public final KeyPearlMode keyPearlMode;
    public final MessageIndicator messageIndicator;
    public MessageSignatureData signature;
    public Text text;
    public ChatHudLine.Visible visible;
    public String string;

    public AddMessageEvent(MessageIndicator messageIndicator, MessageSignatureData messageSignatureData, Text text) {
        this.messageIndicator = messageIndicator;
        this.signature = messageSignatureData;
        this.text = text;
        this.keyPearlMode = KeyPearlMode.Pre;
        this.string = text == null ? null : text.getString();
    }

    public AddMessageEvent(ChatHudLine.Visible visible) {
        this.visible = visible;
        this.keyPearlMode = KeyPearlMode.Post;
        this.messageIndicator = null;
    }

    public MessageSignatureData getSignature() {
        return this.signature;
    }

    public void setSignature(MessageSignatureData messageSignatureData) {
        this.signature = messageSignatureData;
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public ChatHudLine.Visible getVisible() {
        return this.visible;
    }

    public void setVisible(ChatHudLine.Visible visible) {
        this.visible = visible;
    }

    public KeyPearlMode getPhase() {
        return this.keyPearlMode;
    }

    public String getString() {
        return this.string;
    }

    public MessageIndicator getIndicator() {
        return this.messageIndicator;
    }
}
