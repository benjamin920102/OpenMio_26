package me.mioclient.setting;

import me.mioclient.api.NamedValue;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.lang.Enum;
import me.mioclient.api.Setting;

public final class EnumSetting<T> extends Setting<T> {
    public final EnumJsonConverter enumSettingConverter;

    public EnumSetting(String str, T t, java.util.function.Predicate<T> predicate) {
        super(str, t, predicate);
        this.enumSettingConverter = new EnumJsonConverter(((Enum) getValue()).getDeclaringClass());
        if (!NamedValue.isNamedValue(t)) {
            throw new IllegalArgumentException();
        }
    }

    public EnumSetting(String str, T t) {
        super(str, t);
        this.enumSettingConverter = new EnumJsonConverter(((Enum) getValue()).getDeclaringClass());
        if (!NamedValue.isNamedValue(t)) {
            throw new IllegalArgumentException();
        }
    }

    @Override // me.mioclient.api.Setting
    public void loadFromString(String str) {
        EnumSetting<T> enumSetting = this;
        enumSetting.setValue((T) this.enumSettingConverter.doBackward(new JsonPrimitive(str)));
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        return this.enumSettingConverter.doForward((Enum) getValue());
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        setValue((T) this.enumSettingConverter.doBackward(jsonElement));
    }
}
