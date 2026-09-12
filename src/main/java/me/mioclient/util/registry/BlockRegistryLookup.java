package me.mioclient.util.registry;


import java.util.Collection;
import java.util.Collections;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BlockRegistryLookup.class */
public class BlockRegistryLookup implements RegistrySuggestionProvider {
    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupEntries(String str) {
        Collection<String> collection53 = RegistrySuggestionProvider.getProvider(Registries.BLOCK).getGroupEntries(str);
        return !collection53.isEmpty() ? collection53 : Collections.emptyList();
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupNames() {
        return RegistrySuggestionProvider.getProvider(Registries.BLOCK).getGroupNames();
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Identifier getRegistryId() {
        return RegistryKeys.ITEM.getValue();
    }
}
