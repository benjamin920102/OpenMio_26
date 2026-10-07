package me.mioclient.util.inventory;

import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.NamedRegistryList;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.ClientAccessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class KitRegistry extends NamedRegistryList<InventoryKit> implements ClientAccessor, JsonConfigurable {
    public String string;

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        if (this.string != null) {
            jsonObject.addProperty("current", this.string);
        }
        for (InventoryKit chestStealerEnumSettingHelper : getRegistry()) {
            JsonArray jsonArray = new JsonArray();
            chestStealerEnumSettingHelper.getSlotRecords().forEach((num, record) -> {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("id", Integer.valueOf(record.getRawItemId()));
                if (record.getNbtString() != null) {
                    jsonObject2.addProperty("data", record.getNbtString());
                }
                JsonElement jsonObject3 = new JsonObject();
                ((JsonObject) jsonObject3).addProperty("slot", num);
                ((JsonObject) jsonObject3).add("item", (JsonElement) jsonObject2);
                jsonArray.add(jsonObject3);
            });
            jsonObject.add(chestStealerEnumSettingHelper.getName(), jsonArray);
        }
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            if (asJsonObject.has("current")) {
                do258(asJsonObject.get("current").getAsString());
            }
            for (Map.Entry entry : asJsonObject.entrySet()) {
                if (((JsonElement) entry.getValue()).isJsonArray()) {
                    InventoryKit chestStealerEnumSettingHelper = new InventoryKit((String) entry.getKey());
                    Iterator it = ((JsonElement) entry.getValue()).getAsJsonArray().iterator();
                    while (it.hasNext()) {
                        JsonElement jsonElement2 = (JsonElement) it.next();
                        if (jsonElement2.isJsonObject()) {
                            JsonObject asJsonObject2 = jsonElement2.getAsJsonObject();
                            if (asJsonObject2.has("slot") && asJsonObject2.has("item")) {
                                InventoryKit.Record record = null;
                                JsonObject jsonObject = (asJsonObject2.get("item")) instanceof JsonObject ? (JsonObject) (asJsonObject2.get("item")) : null;
                                if (jsonObject instanceof JsonObject) {
                                    JsonObject jsonObject2 = jsonObject;
                                    if (jsonObject2.has("id")) {
                                        record = new InventoryKit.Record(jsonObject2.get("id").getAsInt(), jsonObject2.has("data") ? jsonObject2.get("data").getAsString() : null);
                                    }
                                }
                                JsonPrimitive jsonPrimitive = (asJsonObject2.get("item")) instanceof JsonPrimitive ? (JsonPrimitive) (asJsonObject2.get("item")) : null;
                                if (jsonPrimitive instanceof JsonPrimitive) {
                                    JsonPrimitive jsonPrimitive2 = jsonPrimitive;
                                    if (jsonPrimitive2.isNumber()) {
                                        record = new InventoryKit.Record(jsonPrimitive2.getAsInt(), null);
                                    }
                                }
                                if (record != null) {
                                    chestStealerEnumSettingHelper.getSlotRecords().put(Integer.valueOf(asJsonObject2.get("slot").getAsInt()), record);
                                }
                            }
                        }
                    }
                    register(chestStealerEnumSettingHelper);
                }
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "kits.json";
    }

    public InventoryKit getChestStealerEnumSettingHelper257() {
        if (this.string == null) {
            return null;
        }
        return (InventoryKit) findFirst(chestStealerEnumSettingHelper -> {
            return chestStealerEnumSettingHelper.getName().equalsIgnoreCase(this.string);
        }).orElse(null);
    }

    public void do258(String str) {
        this.string = str;
    }

    public void saveMapCache(String str) {
        getRegistry().removeIf(chestStealerEnumSettingHelper -> {
            return chestStealerEnumSettingHelper.getName().equalsIgnoreCase(str);
        });
        InventoryKit chestStealerEnumSettingHelper2 = new InventoryKit(str);
        int i = 0;
        Iterator it = minecraftClient.player.getInventory().getNonEquipmentItems().iterator();
        while (it.hasNext()) {
            ItemStack itemStack = (ItemStack) it.next();
            chestStealerEnumSettingHelper2.getSlotRecords().put(Integer.valueOf(i), new InventoryKit.Record(net.minecraft.core.registries.BuiltInRegistries.ITEM.getId(itemStack.getItem()), buildPotionDescriptor(itemStack)));
            i++;
        }
        getRegistry().add(chestStealerEnumSettingHelper2);
        do258(str);
    }

    public static String buildPotionDescriptor(ItemStack itemStack) {
        PotionContents potionContentsComponent = (PotionContents) itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContentsComponent == null) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (net.minecraft.world.effect.MobEffectInstance statusEffectInstance : potionContentsComponent.getAllEffects()) {
            List<String> list = arrayList;
            list.add(new StringTemplate().arg(statusEffectInstance.getAmplifier() + 1).arg(statusEffectInstance.getDescriptionId()).format("\u0001 \u0001"));
        }
        arrayList.sort(Comparator.comparing(str -> {
            return str;
        }));
        return String.join("", arrayList);
    }
}
