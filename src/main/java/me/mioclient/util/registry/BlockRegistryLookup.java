package me.mioclient.util.registry;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class BlockRegistryLookup implements RegistrySuggestionProvider {
    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupEntries(String str) {
        Collection<String> collection53 = RegistrySuggestionProvider.getProvider(BuiltInRegistries.BLOCK).getGroupEntries(str);
        return !collection53.isEmpty() ? collection53 : Collections.emptyList();
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupNames() {
        return RegistrySuggestionProvider.getProvider(BuiltInRegistries.BLOCK).getGroupNames();
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Identifier getRegistryId() {
        return Registries.BLOCK.identifier();
    }
}
