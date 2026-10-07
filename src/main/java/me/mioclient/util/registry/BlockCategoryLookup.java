package me.mioclient.util.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

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
        return Registries.BLOCK.identifier();
    }

    public static void addCategory(String str, Stream<Block> stream) {
        map.put(str, stream.map(BlockCategoryLookup::getBlockName).toList());
    }

    public static String getBlockName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toShortLanguageKey();
    }

    static {
        addCategory("beds", BuiltInRegistries.BLOCK.stream().filter(block -> {
            return block instanceof BedBlock;
        }));
        addCategory("banners", BuiltInRegistries.BLOCK.stream().filter(block2 -> {
            return block2 instanceof BannerBlock;
        }));
        addCategory("doors", BuiltInRegistries.BLOCK.stream().filter(block3 -> {
            return block3 instanceof DoorBlock;
        }));
        addCategory("slabs", BuiltInRegistries.BLOCK.stream().filter(block4 -> {
            return block4 instanceof SlabBlock;
        }));
        addCategory("shulkers", BuiltInRegistries.BLOCK.stream().filter(block5 -> {
            return block5 instanceof ShulkerBoxBlock;
        }));
        addCategory("carpets", BuiltInRegistries.BLOCK.stream().filter(block6 -> {
            return block6 instanceof CarpetBlock;
        }));
        addCategory("glasses", BuiltInRegistries.BLOCK.stream().filter(block7 -> {
            return (block7 instanceof StainedGlassBlock) || block7 == Blocks.GLASS;
        }));
        addCategory("glass_panes", BuiltInRegistries.BLOCK.stream().filter(block8 -> {
            return (block8 instanceof StainedGlassPaneBlock) || block8 == Blocks.GLASS_PANE;
        }));
        addCategory("signs", BuiltInRegistries.BLOCK.stream().filter(block9 -> {
            return block9 instanceof SignBlock;
        }));
        addCategory("ores", BuiltInRegistries.BLOCK.stream().filter(oreBlock -> {
            return BuiltInRegistries.BLOCK.getKey(oreBlock).getPath().contains("ore");
        }));
    }
}
