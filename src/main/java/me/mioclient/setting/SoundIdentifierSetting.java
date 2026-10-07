package me.mioclient.setting;

import me.mioclient.util.client.SoundIdentifier;

import me.mioclient.util.io.JsonValidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mioclient.api.Setting;

public final class SoundIdentifierSetting extends Setting<SoundIdentifier> {
    public SoundIdentifierSetting(String str, SoundIdentifier searchIdentifier) {
        super(str, searchIdentifier);
    }

    public SoundIdentifierSetting(String str, SoundIdentifier searchIdentifier, java.util.function.Predicate<SoundIdentifier> predicate) {
        super(str, searchIdentifier, predicate);
    }

    @Override // me.mioclient.api.Setting
    public void loadFromString(String str) {
        String[] split = str.split(":");
        if (split.length == 1) {
            setValue(new SoundIdentifier(str));
        } else if (split.length == 2) {
            setValue(new SoundIdentifier(split[0], split[1]));
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", getValue().getName());
        jsonObject.addProperty("category", getValue().getSoundSource());
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        if (JsonValidator.hasAllFields(asJsonObject, "name", "category")) {
            setValue(new SoundIdentifier(asJsonObject.get("category").getAsString(), asJsonObject.get("name").getAsString()));
        }
    }
}
