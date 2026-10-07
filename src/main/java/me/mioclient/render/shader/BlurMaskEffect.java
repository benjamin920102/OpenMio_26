package me.mioclient.render.shader;

public class BlurMaskEffect {
    public static PostEffectPipeline blurFramebufferHelper = PostEffectPipeline.createBlurPipeline("blur_mask", blurFramebufferHelper2 -> {
    });
}
