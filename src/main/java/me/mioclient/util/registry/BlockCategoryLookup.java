package me.mioclient.util.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BlockCategoryRegistry.class */
public class BlockCategoryLookup implements RegistrySuggestionProvider {
    public static final Map<String, List<String>> map = new HashMap();

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupEntries(String str) {
        return map.getOrDefault(str.toLowerCase(), Collections.emptyList());
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Collection<String> getGroupNames() {
        return map.keySet();
    }

    @Override // me.mioclient.util.registry.RegistrySuggestionProvider
    public Identifier getRegistryId() {
        return RegistryKeys.BLOCK.getValue();
    }

    public static void addCategory(String str, Stream<Block> stream) {
        map.put(str, stream.map(BlockCategoryLookup::getBlockName).toList());
    }

    public static String getBlockName(Block block) {
        return Registries.BLOCK.getId(block).toShortTranslationKey();
    }

    static {
        addCategory("beds", Registries.BLOCK.stream().filter(block -> {
            return block instanceof BedBlock;
        }));
        addCategory("banners", Registries.BLOCK.stream().filter(block2 -> {
            return block2 instanceof BannerBlock;
        }));
        addCategory("doors", Registries.BLOCK.stream().filter(block3 -> {
            return block3 instanceof DoorBlock;
        }));
        addCategory("slabs", Registries.BLOCK.stream().filter(block4 -> {
            return block4 instanceof SlabBlock;
        }));
        addCategory("shulkers", Registries.BLOCK.stream().filter(block5 -> {
            return block5 instanceof ShulkerBoxBlock;
        }));
        addCategory("carpets", Registries.BLOCK.stream().filter(block6 -> {
            return block6 instanceof CarpetBlock;
        }));
        addCategory("glasses", Registries.BLOCK.stream().filter(block7 -> {
            return (block7 instanceof StainedGlassBlock) || block7 == Blocks.GLASS;
        }));
        addCategory("glass_panes", Registries.BLOCK.stream().filter(block8 -> {
            return (block8 instanceof StainedGlassPaneBlock) || block8 == Blocks.GLASS_PANE;
        }));
        addCategory("signs", Registries.BLOCK.stream().filter(block9 -> {
            return block9 instanceof AbstractSignBlock;
        }));
        addCategory("ores", Registries.BLOCK.streamEntries().filter(reference -> {
            return reference.matches(registryKey -> {
                return registryKey.getValue().toTranslationKey().contains("ore");
            });
        }).map((v0) -> {
            return v0.value();
        }));
    }
}
