package me.mioclient.api;

import com.google.gson.JsonElement;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/JsonConfigurable.class */
public interface JsonConfigurable {
    JsonElement toJson();

    void fromJson(JsonElement jsonElement);

    default String getConfigName() {
        return "";
    }
}
