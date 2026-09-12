package me.mioclient.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.mioclient.api.Setting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ObjectSetting.class */
public abstract class ObjectSetting<T> extends Setting<Set<T>> {
    public T singleValue;

    @SafeVarargs
    public ObjectSetting(String str, T... tArr) {
        super(str, new ObjectOpenHashSet());
        ((Set) getDefaultValue()).addAll(List.of((Object[]) tArr));
        ((Set) getValue()).addAll(List.of((Object[]) tArr));
    }

    @SafeVarargs
    public ObjectSetting(String str, java.util.function.Predicate<Set<T>> predicate, T... tArr) {
        super(str, new ObjectOpenHashSet(), predicate);
        ((Set) getDefaultValue()).addAll(List.of((Object[]) tArr));
        ((Set) getValue()).addAll(List.of((Object[]) tArr));
    }

    public abstract T fromStringObj(String str);

    public abstract String toStringObj(T t);

    public abstract Collection<T> getAvailableValues();

    /* JADX WARN: Multi-variable type inference failed */
    public Set<String> getValueStrings() {
        Set objectOpenHashSet = new ObjectOpenHashSet();
        Iterator it = ((Set) getValue()).iterator();
        while (it.hasNext()) {
            objectOpenHashSet.add(toStringObj((T) it.next()));
        }
        return objectOpenHashSet;
    }

    @Override // me.mioclient.api.Setting
    public void loadFromString(String str) {
        T object1070 = fromStringObj(str);
        if (object1070 == null) {
            throw new IllegalArgumentException();
        }
        if (contains(object1070)) {
            removeFromSet(object1070);
        } else {
            addToSet(object1070);
        }
    }

    public Collection<String> getAllValueStrings() {
        HashSet hashSet = new HashSet();
        Iterator<T> it = getAvailableValues().iterator();
        while (it.hasNext()) {
            hashSet.add(toStringObj(it.next()));
        }
        return hashSet;
    }

    public void addToSet(T t) {
        if (t == null) {
            return;
        }
        ((Set) getValue()).add(t);
        this.singleValue = t;
        if (this.runnable != null) {
            this.runnable.run();
        }
    }

    public void addToSetFromString(String str) {
        if (str == null) {
            return;
        }
        addToSet(fromStringObj(str));
    }

    public void removeFromSet(T t) {
        if (t == null) {
            return;
        }
        ((Set) getValue()).remove(t);
        this.singleValue = t;
        if (this.runnable != null) {
            this.runnable.run();
        }
    }

    public void removeFromString(String str) {
        if (str == null) {
            return;
        }
        removeFromSet(fromStringObj(str));
    }

    public boolean contains(T t) {
        if (t == null) {
            return false;
        }
        return ((Set) getValue()).contains(t);
    }

    public boolean containsString(String str) {
        if (str == null) {
            return false;
        }
        return contains(fromStringObj(str));
    }

    public T getLastAdded() {
        return this.singleValue;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonArray jsonArray = new JsonArray();
        Iterator it = ((Set) getValue()).iterator();
        while (it.hasNext()) {
            jsonArray.add(toStringObj((T) it.next()));
        }
        return jsonArray;
    }
}
