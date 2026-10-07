package me.mioclient.util.io;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class StubResourcePack implements PackResources {
    public PackLocationInfo info = new PackLocationInfo("mio", Component.literal("mio"), PackSource.BUILT_IN, Optional.empty());

    @Nullable
    public IoSupplier<InputStream> getRootResource(String... strArr) {
        return null;
    }

    @Nullable
    public IoSupplier<InputStream> getResource(PackType resourceType, Identifier identifier) {
        return null;
    }

    public void listResources(PackType resourceType, String str, String str2, PackResources.ResourceOutput resultConsumer) {
    }

    public Set<String> getNamespaces(PackType resourceType) {
        return Set.of();
    }

    @Nullable
    public <T> T getMetadataSection(MetadataSectionType<T> resourceMetadataReader) {
        return null;
    }

    public PackLocationInfo location() {
        return this.info;
    }

    public void close() {
    }
}
