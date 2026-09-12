package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.sugar.Local;
import java.io.File;
import java.util.function.Consumer;
import me.mioclient.module.misc.ExtraScreenshot;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({ScreenshotRecorder.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinScreenshotRecorder.class */
public class MixinScreenshotRecorder {
    private static ExtraScreenshot screenshot;

    private static ExtraScreenshot getScreenshot() {

        if (screenshot == null && ClientServices.moduleInjector != null) {

            try { screenshot = (ExtraScreenshot) ClientServices.moduleInjector.getModule(ExtraScreenshot.class); } catch (Exception e) {}

        }

        return screenshot;

    }

    @Inject(method = {"saveScreenshotInner"}, at = {@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/ScreenshotRecorder;takeScreenshot(Lnet/minecraft/client/gl/Framebuffer;)Lnet/minecraft/client/texture/NativeImage;", shift = At.Shift.AFTER)}, cancellable = true)
    private static void saveScreenshotInner(File file, String str, Framebuffer framebuffer, Consumer<Text> consumer, CallbackInfo callbackInfo, @Local NativeImage nativeImage) {
        ExtraScreenshot screenshot_lcl = getScreenshot();

        if (screenshot_lcl == null) return;

        if (screenshot_lcl.isToggled()) {
            Util.getIoWorkerExecutor().execute(() -> {
                screenshot_lcl.processNativeImage(nativeImage);
            });
            if (screenshot_lcl.shouldDeleteFile()) {
                callbackInfo.cancel();
            }
        }
    }

    @ModifyVariable(method = {"saveScreenshotInner"}, at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static Consumer<Text> modifyMessageReceiver(Consumer<Text> consumer) {
        ExtraScreenshot screenshot_lcl = getScreenshot();

        if (screenshot_lcl == null) return consumer;

        return (!screenshot_lcl.isToggled() || screenshot_lcl.isModeNone()) ? consumer : new Consumer<Text>() { // from class: me.mioclient.mixin.MixinScreenshotRecorder.1
            @Override // java.util.function.Consumer
            public void accept(Text text) {
            }
        };
    }
}
