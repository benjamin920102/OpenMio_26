package me.mioclient.ducks;

public interface DuckWorldRendererFramebuffer {
    com.mojang.blaze3d.pipeline.RenderTarget getFramebuffer();

    void setFramebuffer(com.mojang.blaze3d.pipeline.RenderTarget framebuffer);
}
