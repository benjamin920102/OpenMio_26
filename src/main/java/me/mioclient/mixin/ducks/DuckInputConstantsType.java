package me.mioclient.mixin.ducks;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): InputUtil.Type is now InputConstants.Type with
   `private final Int2ObjectMap<Key> map`. Old DuckInputUtilType retargeted here
   so KeybindArgumentType can enumerate keyboard keys for suggestions. */
@Mixin({InputConstants.Type.class})
public interface DuckInputConstantsType {
    @Accessor("map")
    Int2ObjectMap<InputConstants.Key> mio$getKeyMap();
}
