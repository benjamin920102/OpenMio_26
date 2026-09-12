package me.mioclient.manager;

import me.mioclient.event.events.client.ClientInitEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.module.HudModule;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.AbstractMap;
import java.util.Comparator;
import me.mioclient.event.Listen;
import me.mioclient.module.Armor;
import me.mioclient.module.Chat;
import me.mioclient.module.Crypto;
import me.mioclient.module.Direction;
import me.mioclient.module.Effects;
import me.mioclient.module.EntityList;
import me.mioclient.module.Graph;
import me.mioclient.module.Inventory;
import me.mioclient.module.Lag;
import me.mioclient.module.Map;
import me.mioclient.module.Metrics;
import me.mioclient.module.Module;
import me.mioclient.module.Music;
import me.mioclient.module.PlayerModel;
import me.mioclient.module.Position;
import me.mioclient.module.TextRadar;
import me.mioclient.module.Totems;
import me.mioclient.module.Watermark;
import me.mioclient.module.Welcomer;
import me.mioclient.event.events.client.ClientInitEvent;
import me.mioclient.module.HudModule;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SearchHelper4_20.class */
public final class HudManager implements ClientAccessor, JsonConfigurable {
    public HudManager() {
        eventBus.register(this);
        ClientServices.moduleRegistry.register(new Watermark());
        ClientServices.moduleRegistry.register(new me.mioclient.module.ModuleList());
        ClientServices.moduleRegistry.register(new Armor());
        ClientServices.moduleRegistry.register(new Graph());
        ClientServices.moduleRegistry.register(new Effects());
        ClientServices.moduleRegistry.register(new Welcomer());
        ClientServices.moduleRegistry.register(new Position());
        ClientServices.moduleRegistry.register(new Direction());
        ClientServices.moduleRegistry.register(new TextRadar());
        ClientServices.moduleRegistry.register(new Metrics());
        ClientServices.moduleRegistry.register(new Totems());
        ClientServices.moduleRegistry.register(new Lag());
        ClientServices.moduleRegistry.register(new Inventory());
        ClientServices.moduleRegistry.register(new Music());
        ClientServices.moduleRegistry.register(new PlayerModel());
        ClientServices.moduleRegistry.register(new EntityList());
        ClientServices.moduleRegistry.register(new Chat());
        ClientServices.moduleRegistry.register(new Map());
        ClientServices.moduleRegistry.register(new Crypto());
    }

    @Listen
    public void onClientInit(ClientInitEvent clientEvent) {
        for (Module module : ClientServices.moduleRegistry.getRegistry()) {
            if (module instanceof HudModule) {
                HudModule moduleList = (HudModule) module;
                moduleList.setAnchor(moduleList.getAnchor());
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        for (Module module : ClientServices.moduleRegistry.getRegistry()) {
            if (module instanceof HudModule) {
                try {
                    jsonObject.add(module.getConfigName(), module.toJson());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        jsonElement.getAsJsonObject().entrySet().stream().sorted(Comparator.comparing(entry -> {
            try {
                return Integer.valueOf(((JsonElement) entry.getValue()).getAsJsonObject().getAsJsonObject("hud").get("index").getAsInt());
            } catch (Throwable th) {
                return 0;
            }
        })).map(entry2 -> {
            return new AbstractMap.SimpleEntry((Module) ClientServices.moduleRegistry.findFirst(module -> {
                return module.getConfigName().equalsIgnoreCase((String) entry2.getKey());
            }).orElse(null), (JsonElement) entry2.getValue());
        }).filter(simpleEntry -> {
            return simpleEntry.getKey() instanceof HudModule;
        }).forEach(simpleEntry2 -> {
            try {
                ((Module) simpleEntry2.getKey()).fromJson((JsonElement) simpleEntry2.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "hud.json";
    }
}
