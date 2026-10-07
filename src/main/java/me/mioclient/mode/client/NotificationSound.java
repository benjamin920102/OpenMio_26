package me.mioclient.mode.client;

import me.mioclient.util.client.SoundIdentifier;

public enum NotificationSound {
    KABAN("kaban"),
    NEVERLOSE("neverlose"),
    CSS("css"),
    COD("cod"),
    QUAKE("quake"),
    TOOLBOX("toolbox"),
    WARNING("warning"),
    STEAM("steam"),
    WHATSAPP("whatsapp"),
    VK("vk"),
    ICQ("icq"),
    STALKER("stalker"),
    HOVER("hover"),
    CLICK("click"),
    RCLICK("rclick"),
    BODYSPLAT("bodysplat");

    public final SoundIdentifier searchIdentifier;

    NotificationSound(String str) {
        this.searchIdentifier = new SoundIdentifier(str);
    }

    public SoundIdentifier getSearchIdentifier() {
        return this.searchIdentifier;
    }
}
