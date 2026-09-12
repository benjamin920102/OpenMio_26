package me.mioclient.module.misc.chatfilter;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;

import me.mioclient.event.EventBus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.mioclient.module.misc.ChatFilter;
import net.minecraft.util.Formatting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChatFilterRegistry.class */
public class ChatFilterRegistry implements ClientAccessor, JsonConfigurable {
    public static ChatFilter chatFilter = (ChatFilter) ClientServices.moduleInjector.getModule(ChatFilter.class);
    public final List<ChatFilterEntry> list = Collections.synchronizedList(new ArrayList());

    public ChatFilterRegistry() {
        eventBus.register(this);
    }

    public boolean containsId(String str) {
        synchronized (this.list) {
            String lowerCase = str.toLowerCase();
            Iterator<ChatFilterEntry> it = this.list.iterator();
            while (it.hasNext()) {
                if (it.next().getId().toLowerCase().equals(lowerCase)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void addFilterEntry(String str, String str2) {
        synchronized (this.list) {
            this.list.add(new ChatFilterEntry(str, str2));
        }
    }

    public boolean is2677(String str) {
        if (!chatFilter.caseSensitive.getValue().booleanValue()) {
            str = str.toLowerCase();
        }
        synchronized (this.list) {
            Iterator<ChatFilterEntry> it = this.list.iterator();
            while (it.hasNext()) {
                String string1094 = it.next().getFilter();
                if (!chatFilter.caseSensitive.getValue().booleanValue()) {
                    string1094 = string1094.toLowerCase();
                }
                if (str.contains(string1094)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isEmpty() {
        boolean isEmpty;
        synchronized (this.list) {
            isEmpty = this.list.isEmpty();
        }
        return isEmpty;
    }

    public Stream<String> getStream2678() {
        Stream map;
        synchronized (this.list) {
            map = this.list.stream().map((v0) -> {
                return v0.getId();
            });
        }
        return map;
    }

    public Optional<ChatFilterEntry> getOptional2679(String str) {
        Optional<ChatFilterEntry> findAny;
        synchronized (this.list) {
            findAny = this.list.stream().filter(data -> {
                return data.getId().equalsIgnoreCase(str);
            }).findAny();
        }
        return findAny;
    }

    public void do2680(ChatFilterEntry data) {
        synchronized (this.list) {
            this.list.remove(data);
        }
    }

    public int get2681() {
        int size;
        synchronized (this.list) {
            size = this.list.size();
        }
        return size;
    }

    public List<String> getList2682() {
        ArrayList arrayList;
        synchronized (this.list) {
            arrayList = new ArrayList();
            for (ChatFilterEntry data : this.list) {
                arrayList.add("%s%s%s: \"%s\"".formatted(Formatting.GRAY, data.getId(), Formatting.RESET, data.getFilter()));
            }
        }
        return arrayList;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        synchronized (this.list) {
            for (ChatFilterEntry data : this.list) {
                JsonElement jsonObject2 = new JsonObject();
                ((JsonObject) jsonObject2).addProperty("id", data.getId());
                ((JsonObject) jsonObject2).addProperty("filter", data.getFilter());
                jsonArray.add(jsonObject2);
            }
        }
        jsonObject.add("filters", (JsonElement) jsonArray);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            if (jsonObject.has("filters")) {
                JsonArray jsonArray = (jsonObject.get("filters")) instanceof JsonArray ? (JsonArray) (jsonObject.get("filters")) : null;
                if (jsonArray instanceof JsonArray) {
                    JsonArray jsonArray2 = jsonArray;
                    for (int i = 0; i < jsonArray2.size(); i++) {
                        JsonObject asJsonObject = jsonArray2.get(i).getAsJsonObject();
                        if (asJsonObject.has("id") && asJsonObject.has("filter")) {
                            String asString = asJsonObject.get("id").getAsString();
                            String asString2 = asJsonObject.get("filter").getAsString();
                            if (!containsId(asString)) {
                                addFilterEntry(asString, asString2);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "chatfilter.json";
    }
}
