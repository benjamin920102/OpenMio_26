package me.mioclient.util.registry;

import me.mioclient.api.NamedValue;

import me.mioclient.util.registry.AbstractCollectionRegistry;

import me.mioclient.util.registry.AbstractCollectionRegistry;

import java.util.ArrayList;
import java.util.List;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/NamedRegistryList.class */
public class NamedRegistryList<E extends NamedValue> extends AbstractCollectionRegistry<E, List<E>> {
    public NamedRegistryList() {
        super(new ArrayList());
    }

    /* JADX WARN: Incorrect return type in method signature: <T:TE;>(Ljava/lang/Class<TT;>;)TT; */
    public NamedValue findFirstByClass(Class cls) {
        return (NamedValue) findFirstMatch(enumSettingHelper -> {
            return enumSettingHelper.getClass().equals(cls);
        });
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is119, reason: merged with bridge method [inline-methods] */
    public boolean register(E e) {
        if (e == null) {
            return false;
        }
        for (NamedValue enumSettingHelper : (List<E>) this.registry) {
            if (enumSettingHelper != null && enumSettingHelper.getName() != null && enumSettingHelper.getName().equals(e.getName())) {
                return false;
            }
        }
        ((List) this.registry).add(e);
        return true;
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is118, reason: merged with bridge method [inline-methods] */
    public boolean unregister(E e) {
        if (e == null) {
            return false;
        }
        ((List) this.registry).remove(e);
        return true;
    }
}
