package me.mioclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.mioclient.api.Setting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BooleanSetting.class */
public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String str, Boolean bool, java.util.function.Predicate<Boolean> predicate) {
        super(str, bool, predicate);
    }

    public BooleanSetting(String str, Boolean bool) {
        super(str, bool);
    }

    @Override // me.mioclient.api.Setting
    public void loadFromString(String str) {
        if ("toggle".equalsIgnoreCase(str)) {
            setValue(Boolean.valueOf(!getValue().booleanValue()));
            return;
        }
        if (str.equals("0") || str.equalsIgnoreCase("false")) {
            setValue(false);
        } else if (str.equals("1") || str.equalsIgnoreCase("true")) {
            setValue(true);
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        setValue(Boolean.valueOf(jsonElement.getAsBoolean()));
    }
}
