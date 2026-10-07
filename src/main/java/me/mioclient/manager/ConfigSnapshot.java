package me.mioclient.manager;

import me.mioclient.mode.misc.PresetHelperMode;

import com.google.gson.JsonElement;

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
