package me.mioclient.event.events.network;

import me.mioclient.mode.combat.KeyPearlMode;

import me.mioclient.event.Event;

import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Component;

public class AddMessageEvent extends Event {
    public final KeyPearlMode keyPearlMode;
    public final GuiMessageTag GuiMessageTag;
    public MessageSignature signature;
    public Component text;
    public GuiMessage.Line visible;
    public String string;

    public AddMessageEvent(GuiMessageTag GuiMessageTag, MessageSignature MessageSignature, Component text) {
        this.GuiMessageTag = GuiMessageTag;
        this.signature = MessageSignature;
        this.text = text;
        this.keyPearlMode = KeyPearlMode.Pre;
        this.string = text == null ? null : text.getString();
    }

    public AddMessageEvent(GuiMessage.Line visible) {
        this.visible = visible;
        this.keyPearlMode = KeyPearlMode.Post;
        this.GuiMessageTag = null;
    }

    public MessageSignature getSignature() {
        return this.signature;
    }

    public void setSignature(MessageSignature MessageSignature) {
        this.signature = MessageSignature;
    }

    public Component getText() {
        return this.text;
    }

    public void setText(Component text) {
        this.text = text;
    }

    public GuiMessage.Line getVisible() {
        return this.visible;
    }

    public void setVisible(GuiMessage.Line visible) {
        this.visible = visible;
    }

    public KeyPearlMode getPhase() {
        return this.keyPearlMode;
    }

    public String getString() {
        return this.string;
    }

    public GuiMessageTag getIndicator() {
        return this.GuiMessageTag;
    }
}
