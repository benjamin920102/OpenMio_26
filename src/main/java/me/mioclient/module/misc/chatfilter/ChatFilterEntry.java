package me.mioclient.module.misc.chatfilter;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Data.class */
public final class ChatFilterEntry {
    public final String string;
    public final String string2;

    public ChatFilterEntry(String str, String str2) {
        this.string = str;
        this.string2 = str2;
    }




    public String getId() {
        return this.string;
    }

    public String getFilter() {
        return this.string2;
    }
}
