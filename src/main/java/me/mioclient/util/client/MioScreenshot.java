package me.mioclient.util.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

/**
 * Our own screenshot capture, replacing the Fabrishot soft-dependency (which
 * has no 26.2 build). All methods must be called on the render thread.
 */
public final class MioScreenshot {
    private MioScreenshot() {
    }

    /** Capture the current frame at native resolution. */
    public static void captureNative(Consumer<NativeImage> callback) {
        RenderSystem.assertOnRenderThread();
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget target = minecraft.gameRenderer.mainRenderTarget();
        Screenshot.takeScreenshot(target, callback);
    }

    /**
     * Capture a supersampled screenshot: the main framebuffer is resized to
     * {@code scale}x, one frame is re-rendered into it, the pixels are
     * grabbed, and the original size is restored.
     */
    public static void captureHiRes(int scale, Consumer<NativeImage> callback) {
        RenderSystem.assertOnRenderThread();
        if (scale <= 1) {
            captureNative(callback);
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget target = minecraft.gameRenderer.mainRenderTarget();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        target.resize(Math.max(2, width * scale), Math.max(2, height * scale));
        try {
            // 26.2: GameRenderer.render(DeltaTracker, boolean).
            minecraft.gameRenderer.render(minecraft.getDeltaTracker(), true);
            Screenshot.takeScreenshot(target, callback);
        } finally {
            target.resize(width, height);
        }
    }
}
