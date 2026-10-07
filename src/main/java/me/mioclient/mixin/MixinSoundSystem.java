package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.sound.PlayEvent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port (verified via JDK25 javap): SoundManager.play still exists but
   takes net.minecraft.client.resources.sounds.SoundInstance and returns
   SoundEngine$PlayResult. Match by simple name, require = 0. */
@Mixin({SoundManager.class})

public class MixinSoundSystem {
    @Inject(method = {"play"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void playHook(SoundInstance soundInstance, CallbackInfoReturnable<Object> callbackInfo) {
        PlayEvent playEvent = new PlayEvent(soundInstance);
        ClientAccessor.eventBus.fireEvent(playEvent);
        if (playEvent.isCancelled()) {
            callbackInfo.setReturnValue(null);
            callbackInfo.cancel();
        }
    }
}
