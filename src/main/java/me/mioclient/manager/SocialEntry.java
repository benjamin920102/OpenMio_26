package me.mioclient.manager;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.render.NameTagsHelperMode;

import me.mioclient.api.NamedValue;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import me.mioclient.mode.render.NameTagsHelperMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SocialEntry.class */
public final class SocialEntry implements NamedValue {
    public final String name;
    public final NameTagsHelperMode nameTagsHelperMode;

    public SocialEntry(String str, NameTagsHelperMode nameTagsHelperMode) {
        this.name = str;
        this.nameTagsHelperMode = nameTagsHelperMode;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }




    public String getRawName() {
        return this.name;
    }

    public NameTagsHelperMode getHelperMode() {
        return this.nameTagsHelperMode;
    }
}
