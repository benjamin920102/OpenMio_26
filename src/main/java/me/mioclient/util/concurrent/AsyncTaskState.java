package me.mioclient.util.concurrent;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import com.google.gson.annotations.SerializedName;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AsyncTaskState.class */
public final class AsyncTaskState implements NamedValue {

    @SerializedName("sha")
    public final String string;

    @SerializedName("message")
    public final String name;

    @SerializedName("author")
    public final String string2;

    public AsyncTaskState(String str, String str2, String str3) {
        this.string = str;
        this.name = str2;
        this.string2 = str3;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public String getShortSha() {
        return this.string.substring(0, 6);
    }




    @SerializedName("sha")
    public String getSha() {
        return this.string;
    }

    @SerializedName("message")
    public String getCommitMessage() {
        return this.name;
    }

    @SerializedName("author")
    public String getAuthor() {
        return this.string2;
    }
}
