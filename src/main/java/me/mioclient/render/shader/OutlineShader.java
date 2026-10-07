package me.mioclient.render.shader;

import me.mioclient.mixin.ducks.DuckLevelRenderer;

public abstract class OutlineShader extends PostProcessEffect {
    private Object savedFramebuffer;
    private boolean swapped;

    @Override
    public void pushFramebuffer() {
        if (this.swapped || this.framebuffer == null) {
            return;
        }
        try {
            // 26.2 port (verified): WorldRenderer is now LevelRenderer; vanilla outline target
            // is LevelRenderer.entityOutlineTarget() (public) backed by LevelTargetBundle.
            // There is no public setter (targets are ResourceHandle-managed by PostChain),
            // so this pass records the current target for sampling instead of swapping.
            Object current = null;
            try {
                current = minecraftClient.levelRenderer.entityOutlineTarget();
            } catch (Exception ignored) {
            }
            if (current == null) {
                try {
                    current = ((DuckLevelRenderer) minecraftClient.levelRenderer).mio$getEntityOutlineTargetField();
                } catch (Exception ignored) {
                }
            }
            this.savedFramebuffer = current;
            this.swapped = true;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void popFramebuffer() {
        if (!this.swapped) {
            return;
        }
        this.savedFramebuffer = null;
        this.swapped = false;
    }

    public void do866() {
        applyPostEffect(() -> {
        });
    }
}
