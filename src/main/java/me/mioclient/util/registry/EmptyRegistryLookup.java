package me.mioclient.util.registry;


import java.util.Collection;
import java.util.Collections;
import net.minecraft.util.Identifier;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Helper_24.class */
public class EmptyRegistryLookup implements RegistrySuggestionProvider {
    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupEntries(String str) {
        return Collections.emptyList();
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupNames() {
        return Collections.emptyList();
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Identifier getRegistryId() {
        return null;
    }
}
