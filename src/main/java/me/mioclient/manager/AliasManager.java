package me.mioclient.manager;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;

import me.mioclient.api.ClientAccessor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.mioclient.module.Module;

public final class AliasManager implements ClientAccessor, JsonConfigurable {
    public final Map<Module, String> map = new HashMap();
    public final Map<String, String> map2 = new HashMap();

    public void setModuleAlias(Module module, String str) {
        this.map.compute(module, (module2, str2) -> {
            return str;
        });
    }

    public void setPlayerAlias(String str, String str2) {
        this.map2.compute(str, (str3, str4) -> {
            return str2;
        });
    }

    public void removeModuleAlias(Module module) {
        this.map.remove(module);
    }

    public void removePlayerAlias(String str) {
        this.map2.remove(str);
    }

    public String getModuleDisplayName(Module module) {
        return this.map.getOrDefault(module, module.getName());
    }

    public String getPlayerDisplayName(String str) {
        return this.map2.getOrDefault(str, str);
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonObject jsonObject2 = new JsonObject();
        JsonObject jsonObject3 = new JsonObject();
        this.map.forEach((module, str) -> {
            jsonObject2.addProperty(module.getName(), str);
        });
        Map<String, String> map = this.map2;
        Objects.requireNonNull(jsonObject3);
        map.forEach(jsonObject3::addProperty);
        jsonObject.add("module", jsonObject2);
        jsonObject.add("players", jsonObject3);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        if (asJsonObject.has("module")) {
            asJsonObject.getAsJsonObject("module").asMap().forEach((str, jsonElement2) -> {
                ClientServices.moduleRegistry.findFirst(module -> {
                    return module.getName().equalsIgnoreCase(str);
                }).ifPresent(module2 -> {
                    setModuleAlias(module2, jsonElement2.getAsString());
                });
            });
        }
        if (asJsonObject.has("players")) {
            asJsonObject.getAsJsonObject("players").asMap().forEach((str2, jsonElement3) -> {
                setPlayerAlias(str2, jsonElement3.getAsString());
            });
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "aliases.json";
    }
}
