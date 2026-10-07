package me.mioclient.util.registry;

import java.util.Collection;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public interface RegistrySuggestionProvider {
    public static final List<RegistrySuggestionProvider> list = List.of(new BlockCategoryLookup(), new BlockRegistryLookup());
    public static final EmptyRegistryLookup helper_24 = new EmptyRegistryLookup();

    Collection<String> getGroupEntries(String str);

    Collection<String> getGroupNames();

    Identifier getRegistryId();

    static RegistrySuggestionProvider getProvider(Registry<?> registry) {
        for (RegistrySuggestionProvider helper : list) {
            if (registry.key().identifier().equals(helper.getRegistryId())) {
                return helper;
            }
        }
        return helper_24;
    }
}
