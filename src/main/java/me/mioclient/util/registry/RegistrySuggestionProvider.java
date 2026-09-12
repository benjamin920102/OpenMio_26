package me.mioclient.util.registry;

import me.mioclient.util.registry.BlockRegistryLookup;
import me.mioclient.util.registry.EmptyRegistryLookup;

import me.mioclient.util.registry.BlockRegistryLookup;
import me.mioclient.util.registry.EmptyRegistryLookup;

import java.util.Collection;
import java.util.List;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/RegistrySuggestionProvider.class */
public interface RegistrySuggestionProvider {
    public static final List<RegistrySuggestionProvider> list = List.of(new BlockCategoryLookup(), new BlockRegistryLookup());
    public static final EmptyRegistryLookup helper_24 = new EmptyRegistryLookup();

    Collection<String> getGroupEntries(String str);

    Collection<String> getGroupNames();

    Identifier getRegistryId();

    static RegistrySuggestionProvider getProvider(Registry<?> registry) {
        for (RegistrySuggestionProvider helper : list) {
            if (registry.getKey().getValue().equals(helper.getRegistryId())) {
                return helper;
            }
        }
        return helper_24;
    }
}
