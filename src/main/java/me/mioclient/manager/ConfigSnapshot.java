package me.mioclient.manager;

import me.mioclient.mode.misc.PresetHelperMode;

import com.google.gson.JsonElement;
import me.mioclient.mode.misc.PresetHelperMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ConfigSnapshot.class */
public final class ConfigSnapshot {
    public JsonElement jsonElement;

    public void do41() {
        this.jsonElement = PresetHelperMode.ALL.toJson();
    }

    public JsonElement consumeJsonElement() {
        JsonElement jsonElement = this.jsonElement;
        this.jsonElement = null;
        return jsonElement;
    }
}
