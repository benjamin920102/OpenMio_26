package me.mioclient.module.misc.stashfinder;

import me.mioclient.api.NamedValue;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import com.google.gson.annotations.SerializedName;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/StashEntry.class */
public class StashEntry implements NamedValue {

    @SerializedName("id")
    public final String name;

    @SerializedName("x")
    public final int num;

    @SerializedName("z")
    public final int num2;

    @SerializedName("dimension")
    public final String string;

    @SerializedName("server")
    public final String string2;

    public StashEntry(String str, int i, int i2, String str2, String str3) {
        this.name = str;
        this.num = i;
        this.num2 = i2;
        this.string = str2;
        this.string2 = str3;
    }

    public String formatCoords() {
        return new StringTemplate().arg(this.num2).arg(this.num).format("\u0001, \u0001.");
    }

    public double getX() {
        return this.num;
    }

    public double getZ() {
        return this.num2;
    }

    public String getDimension() {
        return this.string;
    }

    public String getServer() {
        return this.string2;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
