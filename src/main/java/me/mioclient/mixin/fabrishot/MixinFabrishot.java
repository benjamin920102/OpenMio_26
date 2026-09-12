package me.mioclient.mixin.fabrishot;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import java.io.File;
import java.io.FileInputStream;
import me.mioclient.module.misc.ExtraScreenshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Pseudo
@Mixin(targets = {"me.ramidzkh.fabrishot.Fabrishot"}, remap = false)
/* loaded from: mio-yarn.jar:me/mioclient/mixin/fabrishot/MixinFabrishot.class */
public class MixinFabrishot {
    private static ExtraScreenshot extrascreenshot;

    private static ExtraScreenshot getExtrascreenshot() {

        if (extrascreenshot == null && ClientServices.moduleInjector != null) {

            try { extrascreenshot = (ExtraScreenshot) ClientServices.moduleInjector.getModule(ExtraScreenshot.class); } catch (Exception e) {}

        }

        return extrascreenshot;

    }

    @Inject(method = {"printFileLink"}, at = {@At("HEAD")}, cancellable = true, remap = false)
    private static void printFileLinkHook(File file, CallbackInfo callbackInfo) {
        ExtraScreenshot extrascreenshot_lcl = getExtrascreenshot();

        if (extrascreenshot_lcl == null) return;

        if (extrascreenshot_lcl.isToggled()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] readAllBytes = fileInputStream.readAllBytes();
                fileInputStream.close();
                extrascreenshot_lcl.processScreenshot(readAllBytes);
            } catch (Exception e) {
            }
            if (extrascreenshot_lcl.shouldDeleteFile()) {
                try {
                    file.delete();
                } catch (Exception e2) {
                }
            }
            if (extrascreenshot_lcl.isModeNone()) {
                return;
            }
            callbackInfo.cancel();
        }
    }
}
