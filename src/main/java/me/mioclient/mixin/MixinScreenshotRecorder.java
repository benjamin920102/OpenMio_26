package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.sugar.Local;
import java.io.File;
import java.util.function.Consumer;
import me.mioclient.module.misc.ExtraScreenshot;
import me.mioclient.util.client.MioScreenshot;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screenshot.class})

public class MixinScreenshotRecorder {
    private static ExtraScreenshot screenshot;

    private static ExtraScreenshot getScreenshot() {

        if (screenshot == null && ClientServices.moduleInjector != null) {

            try { screenshot = (ExtraScreenshot) ClientServices.moduleInjector.getModule(ExtraScreenshot.class); } catch (Exception e) {}

        }

        return screenshot;

    }

    @Inject(method = {"saveScreenshotInner"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private static void onSaveScreenshotHead(File file, String str, RenderTarget framebuffer, Consumer<Component> consumer, CallbackInfo callbackInfo) {
        ExtraScreenshot screenshot_lcl = getScreenshot();

        if (screenshot_lcl == null || !screenshot_lcl.wantsHiRes()) return;

        // Our own hi-res capture (replaces Fabrishot): re-render at scale, feed the module, skip vanilla.
        callbackInfo.cancel();
        MioScreenshot.captureHiRes(screenshot_lcl.hiResScale.getValue(), screenshot_lcl::processNativeImage);
    }

    @Inject(method = {"saveScreenshotInner"}, at = {@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Screenshot;takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;)Lcom/mojang/blaze3d/platform/NativeImage;", shift = At.Shift.AFTER)}, cancellable = true, require = 0)
    private static void saveScreenshotInner(File file, String str, RenderTarget framebuffer, Consumer<Component> consumer, CallbackInfo callbackInfo, @Local NativeImage nativeImage) {
        ExtraScreenshot screenshot_lcl = getScreenshot();

        if (screenshot_lcl == null) return;

        if (screenshot_lcl.isToggled()) {
            Util.ioPool().execute(() -> {
                screenshot_lcl.processNativeImage(nativeImage);
            });
            if (screenshot_lcl.shouldDeleteFile()) {
                callbackInfo.cancel();
            }
        }
    }

    @ModifyVariable(method = {"saveScreenshotInner"}, at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 0)
    private static Consumer<Component> modifyMessageReceiver(Consumer<Component> consumer) {
        ExtraScreenshot screenshot_lcl = getScreenshot();

        if (screenshot_lcl == null) return consumer;

        return (!screenshot_lcl.isToggled() || screenshot_lcl.isModeNone()) ? consumer : new Consumer<Component>() {
            @Override // java.util.function.Consumer
            public void accept(Component text) {
            }
        };
    }
}
