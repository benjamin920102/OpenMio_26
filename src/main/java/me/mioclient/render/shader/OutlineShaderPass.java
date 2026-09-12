package me.mioclient.render.shader;

import me.mioclient.mixin.ducks.DuckWorldRenderer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/OutlineShaderPass.class */
public abstract class OutlineShaderPass extends PostProcessEffect {
    /**
     * Vanilla's entity-outlines framebuffer, held while this pass has its own framebuffer
     * installed in its place. Only valid between pushFramebuffer() and popFramebuffer().
     */
    private net.minecraft.client.gl.Framebuffer savedFramebuffer;
    /** Whether pushFramebuffer() actually performed the swap, so popFramebuffer() knows to restore. */
    private boolean swapped;

    @Override // me.mioclient.render.shader.PostProcessEffect
    public void pushFramebuffer() {
        if (this.swapped || this.framebuffer == null) {
            return;
        }
        DuckWorldRenderer duckWorldRenderer = (DuckWorldRenderer)(minecraftClient.worldRenderer);
        // JADX left this as a no-op: the subclass declared its own `framebuffer` field that
        // shadowed PostProcessEffect.framebuffer, and this method read vanilla's outline
        // framebuffer and immediately set it straight back. The outline geometry therefore kept
        // drawing into vanilla's framebuffer while applyPostEffect() sampled this pass's own (empty)
        // framebuffer, so vanilla composited its plain white silhouette to the screen instead.
        // Install this pass's framebuffer, mirroring ShaderFramebufferSwapper.swapFramebuffer.
        this.savedFramebuffer = minecraftClient.worldRenderer.getEntityOutlinesFramebuffer();
        duckWorldRenderer.setEntityOutlinesFramebuffer(this.framebuffer);
        this.swapped = true;
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public void popFramebuffer() {
        if (!this.swapped) {
            return;
        }
        ((DuckWorldRenderer)(minecraftClient.worldRenderer)).setEntityOutlinesFramebuffer(this.savedFramebuffer);
        this.savedFramebuffer = null;
        this.swapped = false;
    }

    public void do866() {
        applyPostEffect(() -> {
            this.outlineVertexConsumerProvider.draw();
        });
    }
}
