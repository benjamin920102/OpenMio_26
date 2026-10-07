package me.mioclient.render.font;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.function.Supplier;

/* 26.2 port (verified via JDK25 javap): DynamicTexture hardcodes a REPEAT/NEAREST
   sampler, which makes downscaled font glyphs look blocky. The atlas needs
   CLAMP/LINEAR sampling like vanilla font textures. */
public class LinearFontTexture extends DynamicTexture {
    public LinearFontTexture(Supplier<String> label, NativeImage image) {
        super(label, image);
        try {
            this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
