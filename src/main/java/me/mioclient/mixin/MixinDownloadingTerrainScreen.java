package me.mioclient.mixin;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port (verified via JDK25 javap): LevelLoadingScreen.shouldCloseOnEsc exists. */
@Mixin({LevelLoadingScreen.class})

public class MixinDownloadingTerrainScreen {
    @Inject(method = {"shouldCloseOnEsc"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void shouldCloseOnEscHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(true);
    }
}
