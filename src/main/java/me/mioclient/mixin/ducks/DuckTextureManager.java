package me.mioclient.mixin.ducks;

import java.util.Map;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): texture map field is now byPath; ResourceLocation is now Identifier. */
@Mixin({TextureManager.class})
public interface DuckTextureManager {
    @Accessor("byPath")
    Map<Identifier, AbstractTexture> getAllTextures();
}
