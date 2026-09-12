package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.misc.NoNarrator;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.NarratorManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({NarratorManager.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinNarratorManager.class */
public class MixinNarratorManager {
    private static NoNarrator nonarrator;

    private static NoNarrator getNonarrator() {

        if (nonarrator == null && ClientServices.moduleInjector != null) {

            try { nonarrator = (NoNarrator) ClientServices.moduleInjector.getModule(NoNarrator.class); } catch (Exception e) {}

        }

        return nonarrator;

    }

    @Inject(method = {"getNarratorMode"}, at = {@At("HEAD")}, cancellable = true)
    private void getNarrator(CallbackInfoReturnable<NarratorMode> callbackInfoReturnable) {
        NoNarrator nonarrator_lcl = getNonarrator();

        if (nonarrator_lcl == null) return;

        if (nonarrator_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(NarratorMode.OFF);
        }
    }

    @Inject(method = {"onModeChange"}, at = {@At("HEAD")}, cancellable = true)
    private void onModeChange(CallbackInfo callbackInfo) {
        NoNarrator nonarrator_lcl = getNonarrator();

        if (nonarrator_lcl == null) return;

        if (nonarrator_lcl.isToggled()) {
            callbackInfo.cancel();
        }
    }
}
