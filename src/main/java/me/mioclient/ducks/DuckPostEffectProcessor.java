package me.mioclient.ducks;

import java.util.List;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;

public interface DuckPostEffectProcessor {
    void addFakeTarget(String str, RenderTarget framebuffer);

    List<PostChain> getPasses();
}
