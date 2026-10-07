package me.mioclient.util.client;

import me.mioclient.util.text.StringTemplate;

import net.minecraft.resources.Identifier;

public final class SoundIdentifier {
    public final Identifier id;
    public final String name;
    public final String string;

    public SoundIdentifier(String str, String str2) {
        this.string = str;
        this.name = str2;
        this.id = Identifier.fromNamespaceAndPath(getString1611(str), new StringTemplate().arg(str2).arg(str).format("sounds/\u0001/\u0001.ogg"));
    }

    public SoundIdentifier(String str) {
        this("system", str);
    }

    public Identifier getId() {
        return this.id;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SoundIdentifier other) {
            return this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
