package me.mioclient.util.client;

import me.mioclient.util.text.StringTemplate;

import net.minecraft.util.Identifier;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SearchIdentifier.class */
public final class SoundIdentifier extends Identifier {
    public final String name;
    public final String string;

    public SoundIdentifier(String str, String str2) {
        super(getString1611(str), new StringTemplate().arg(str2).arg(str).format("sounds/\u0001/\u0001.ogg"));
        this.name = str2;
        this.string = str;
    }

    public SoundIdentifier(String str) {
        this("system", str);
    }

    public String getName() {
        return this.name;
    }

    public String getSoundSource() {
        return this.string;
    }

    public static String getString1611(String str) {
        return str.equalsIgnoreCase("system") ? "mio" : "mio-mount";
    }
}
