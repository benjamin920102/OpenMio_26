package me.mioclient.setting;

import com.google.gson.JsonElement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class EntityListObjectSetting<T> extends ObjectSetting<T> {
    public final Registry<T> registry;

    @SafeVarargs
    public EntityListObjectSetting(String str, Registry<T> registry, T... tArr) {
        super(str, tArr);
        this.registry = registry;
    }

    @SafeVarargs
    public EntityListObjectSetting(String str, Registry<T> registry, java.util.function.Predicate<Set<T>> predicate, T... tArr) {
        super(str, predicate, tArr);
        this.registry = registry;
    }

    @Override // me.mioclient.setting.ObjectSetting
    public T fromStringObj(String str) {
        Identifier id = Identifier.tryParse(str);
        if (id == null) {
            return null;
        }
        return (T) this.registry.getOptional(id).orElse(null);
    }

    @Override // me.mioclient.setting.ObjectSetting
    public String toStringObj(T t) {
        Identifier id = this.registry.getKey(t);
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    @Override // me.mioclient.setting.ObjectSetting
    public Collection<T> getAvailableValues() {
        return this.registry.stream().toList();
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            ((Set) getValue()).clear();
            Iterator it = jsonElement.getAsJsonArray().iterator();
            while (it.hasNext()) {
                JsonElement jsonElement2 = (JsonElement) it.next();
                Identifier id2 = Identifier.tryParse(jsonElement2.getAsString());
                ((Set) getValue()).add(id2 == null ? null : this.registry.getOptional(id2).orElse(null));
            }
        }
    }

    public Registry<T> getRegistry() {
        return this.registry;
    }
}
