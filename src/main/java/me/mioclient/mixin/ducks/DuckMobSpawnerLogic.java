package me.mioclient.mixin.ducks;

import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BaseSpawner.class})

public interface DuckMobSpawnerLogic {
    @Accessor("spawnDelay")
    int getSpawnDelay();
}
