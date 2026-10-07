package me.mioclient.api;

import com.google.gson.JsonElement;

public interface JsonConfigurable {
    JsonElement toJson();

    void fromJson(JsonElement jsonElement);

    default String getConfigName() {
        return "";
    }
}
