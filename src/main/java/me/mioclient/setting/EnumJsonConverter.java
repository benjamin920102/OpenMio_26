package me.mioclient.setting;

import me.mioclient.api.NamedValue;

import me.mioclient.util.registry.EnumSettingRegistry;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class EnumJsonConverter extends com.google.common.base.Converter<Enum, JsonElement> {
    public final Class<? extends Enum> class_;

    public EnumJsonConverter(Class<? extends Enum<?>> cls) {
        this.class_ = cls;
    }

    public static int getEnumOrdinal(Enum<?> r3) {
        for (int i = 0; i < ((Enum[]) r3.getDeclaringClass().getEnumConstants()).length; i++) {
            Enum r0 = ((Enum[]) r3.getDeclaringClass().getEnumConstants())[i];
            if (!EnumSettingRegistry.isExperimentalEnumValue(r0)) {
                if (r0.name().equalsIgnoreCase(r3.name())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static Enum<?> getNextEnumValue(Enum r3) {
        int i = getEnumOrdinal(r3);
        for (int i2 = 0; i2 < r3.getDeclaringClass().getEnumConstants().length; i2++) {
            Enum<?> r0 = (Enum) r3.getDeclaringClass().getEnumConstants()[i2];
            if (!EnumSettingRegistry.isExperimentalEnumValue(r0) && i2 > i) {
                return r0;
            }
        }
        return (Enum) r3.getDeclaringClass().getEnumConstants()[0];
    }

    public static String getEnumDisplayName(Enum<?> r3) {
        if (r3 instanceof NamedValue) {
            return ((NamedValue) r3).getName();
        }
        throw new IllegalArgumentException();
    }

    /* renamed from: getJsonElement914, reason: merged with bridge method [inline-methods] */
    public JsonElement doForward(Enum r5) {
        if (r5 instanceof NamedValue) {
            return new JsonPrimitive(((NamedValue) r5).getName());
        }
        throw new IllegalArgumentException();
    }

    /* renamed from: getEnum915, reason: merged with bridge method [inline-methods] */
    public Enum doBackward(JsonElement jsonElement) {
        for (Enum named : (Enum[]) this.class_.getEnumConstants()) {
            if (!NamedValue.isNamedValue(named)) {
                throw new IllegalArgumentException();
            }
            if (!EnumSettingRegistry.isExperimentalEnumValue(named) && (named instanceof NamedValue)) {
                if (jsonElement.getAsString().equalsIgnoreCase(((NamedValue) named).getName())) {
                    return named;
                }
            }
        }
        return ((Enum[]) this.class_.getEnumConstants())[0];
    }
}
