package me.mioclient.api;



import me.mioclient.module.client.HUD;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/api/Category.class */
public enum Category implements NamedValue {
    COMBAT("Combat"),
    MISC("Misc"),
    RENDER("Render"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    EXPLOIT("Exploit"),
    CLIENT("Client"),
    HUD("HUD");

    public final String name;

    Category(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
