package me.mioclient.manager;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mioclient.setting.EntityListObjectSetting;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/feature/Items.class */
public class ItemListConfig implements JsonConfigurable {
    public final EntityListObjectSetting<Item> entityListObjectSetting = new EntityListObjectSetting<>("Items", Registries.ITEM, new Item[0]);

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("items", this.entityListObjectSetting.toJson());
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            if (jsonObject.has("items")) {
                this.entityListObjectSetting.fromJson(jsonObject.get("items"));
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "glint.json";
    }

    public EntityListObjectSetting<Item> getGlintItems() {
        return this.entityListObjectSetting;
    }

    public static boolean isConfiguredItem(Item item) {
        if (ClientServices.items == null) {
            return false;
        }
        return ClientServices.items.entityListObjectSetting.getValue().contains(item);
    }
}
