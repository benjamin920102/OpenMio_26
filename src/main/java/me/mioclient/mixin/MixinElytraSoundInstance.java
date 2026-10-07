package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.ElytraFly;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified via JDK25 javap): ElytraOnPlayerSoundInstance.tick exists. */
@Mixin({ElytraOnPlayerSoundInstance.class})

public abstract class MixinElytraSoundInstance extends AbstractSoundInstance {
    private static ElytraFly elytrafly;

    private static ElytraFly getElytrafly() {

        if (elytrafly == null && ClientServices.moduleInjector != null) {

            try { elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class); } catch (Exception e) {}

        }

        return elytrafly;

    }

    protected MixinElytraSoundInstance(SoundEvent soundEvent, SoundSource soundCategory, RandomSource random) {
        super(soundEvent, soundCategory, random);
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void tickHook(CallbackInfo callbackInfo) {
        ElytraFly elytrafly_lcl = getElytrafly();

        if (elytrafly_lcl == null) return;

        if (elytrafly_lcl.isToggled() && elytrafly_lcl.muteElytra.getValue().booleanValue()) {
            this.volume = 0.0f;
            callbackInfo.cancel();
        }
    }
}
