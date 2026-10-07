package me.mioclient.mixin.ducks;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port: Yarn KeyMapping -> MojMap KeyMapping, boundKey -> key, InputConstants.Key -> InputConstants.Key. */
@Mixin({KeyMapping.class})
public interface DuckKeyBinding {
    @Accessor("key")
    InputConstants.Key getKey();
}
