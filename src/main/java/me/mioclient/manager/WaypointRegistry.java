package me.mioclient.manager;

import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.CollectionRegistry;

import me.mioclient.util.registry.AbstractCollectionRegistry;
import me.mioclient.util.world.Waypoint;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.world.Waypoint;
import me.mioclient.util.registry.AbstractCollectionRegistry;

import me.mioclient.event.EventBus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.mioclient.util.registry.CollectionRegistry;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WaypointRegistry.class */
public final class WaypointRegistry extends AbstractCollectionRegistry<Waypoint, List<Waypoint>> implements ClientAccessor, JsonConfigurable {
    public WaypointRegistry() {
        super(new ArrayList());
        eventBus.register(this);
    }

    public List<Waypoint> getVisible() {
        if (isNotInGame()) {
            return Collections.emptyList();
        }
        return ((List<Waypoint>) this.registry).stream().filter(waypointsEnumSettingHelper -> {
            return waypointsEnumSettingHelper.getServer().equalsIgnoreCase(minecraftClient.player.networkHandler.getServerInfo() == null ? "singleplayer" : minecraftClient.player.networkHandler.getServerInfo().address);
        }).sorted(Comparator.comparing(waypointsEnumSettingHelper2 -> {
            return Double.valueOf(minecraftClient.player.squaredDistanceTo(waypointsEnumSettingHelper2.getX(), waypointsEnumSettingHelper2.getY(), waypointsEnumSettingHelper2.getZ()));
        })).toList();
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        Iterator it = ((List) this.registry).iterator();
        while (it.hasNext()) {
            jsonArray.add(gson.toJsonTree((Waypoint) it.next()));
        }
        jsonObject.add("waypoints", (JsonElement) jsonArray);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("waypoints")) {
            Iterator it = jsonElement.getAsJsonObject().getAsJsonArray("waypoints").iterator();
            while (it.hasNext()) {
                JsonElement jsonElement2 = (JsonElement) it.next();
                try {
                    ((List) this.registry).add((Waypoint) gson.fromJson(jsonElement2, Waypoint.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "waypoints.json";
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is2267, reason: merged with bridge method [inline-methods] */
    public boolean register(Waypoint waypointsEnumSettingHelper) {
        for (Waypoint waypointsEnumSettingHelper2 : getRegistry()) {
            if (waypointsEnumSettingHelper2.getName().equalsIgnoreCase(waypointsEnumSettingHelper.getName())) {
                if (waypointsEnumSettingHelper2.getServer().equalsIgnoreCase(waypointsEnumSettingHelper.getServer())) {
                    return false;
                }
            }
        }
        return getRegistry().add(waypointsEnumSettingHelper);
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is2268, reason: merged with bridge method [inline-methods] */
    public boolean unregister(Waypoint waypointsEnumSettingHelper) {
        return getRegistry().removeIf(waypointsEnumSettingHelper2 -> {
            if (waypointsEnumSettingHelper2.getName().equalsIgnoreCase(waypointsEnumSettingHelper.getName())) {
                if (waypointsEnumSettingHelper2.getServer().equalsIgnoreCase(waypointsEnumSettingHelper.getName())) {
                    return true;
                }
            }
            return false;
        });
    }
}
