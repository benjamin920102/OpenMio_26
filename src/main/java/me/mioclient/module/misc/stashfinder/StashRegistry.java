package me.mioclient.module.misc.stashfinder;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.NamedRegistryList;

import me.mioclient.api.ClientAccessor;
import me.mioclient.module.misc.stashfinder.StashEntry;

import me.mioclient.event.EventBus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import me.mioclient.module.misc.StashFinder;

public final class StashRegistry extends NamedRegistryList<StashEntry> implements ClientAccessor, JsonConfigurable {
    public static StashFinder stashFinder = (StashFinder) ClientServices.moduleInjector.getModule(StashFinder.class);

    public StashRegistry() {
        eventBus.register(this);
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        if (!stashFinder.log.getValue().booleanValue()) {
            return new JsonObject();
        }
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        Iterator it = ClientServices.stashFinderDataManager.getRegistry().iterator();
        while (it.hasNext()) {
            jsonArray.add(gson.toJsonTree((StashEntry) it.next()));
        }
        jsonObject.add("stashes", (JsonElement) jsonArray);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("stashes")) {
            Iterator it = jsonElement.getAsJsonObject().getAsJsonArray("stashes").iterator();
            while (it.hasNext()) {
                JsonElement jsonElement2 = (JsonElement) it.next();
                try {
                    ((List) this.registry).add((StashEntry) gson.fromJson(jsonElement2, StashEntry.class));
                } catch (Exception e) {
                }
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "stashfinder.json";
    }
}