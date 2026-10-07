package me.mioclient.setting;

import com.google.gson.JsonElement;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public final class ObjectSetting_2 extends ObjectSetting<String> {
    public ObjectSetting_2(String str, String... strArr) {
        super(str, strArr);
    }

    public ObjectSetting_2(String str, java.util.function.Predicate<Set<String>> predicate, String... strArr) {
        super(str, predicate, strArr);
    }

    @Override // me.mioclient.setting.ObjectSetting
    /* renamed from: getString1066, reason: merged with bridge method [inline-methods] */
    public String fromStringObj(String str) {
        return str.toLowerCase(Locale.ROOT);
    }

    @Override // me.mioclient.setting.ObjectSetting
    /* renamed from: getString1067, reason: merged with bridge method [inline-methods] */
    public String toStringObj(String str) {
        return str.toLowerCase(Locale.ROOT);
    }

    @Override // me.mioclient.setting.ObjectSetting
    public Collection<String> getAvailableValues() {
        return Collections.emptyList();
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            getValue().clear();
            Iterator it = jsonElement.getAsJsonArray().iterator();
            while (it.hasNext()) {
                getValue().add(((JsonElement) it.next()).getAsString());
            }
        }
    }
}
