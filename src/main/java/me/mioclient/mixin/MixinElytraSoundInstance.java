package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.ElytraFly;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({ElytraSoundInstance.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinElytraSoundInstance.class */
public abstract class MixinElytraSoundInstance extends MovingSoundInstance {
    private static ElytraFly elytrafly;

    private static ElytraFly getElytrafly() {

        if (elytrafly == null && ClientServices.moduleInjector != null) {

            try { elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class); } catch (Exception e) {}

        }

        return elytrafly;

    }

    protected MixinElytraSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, Random random) {
        super(soundEvent, soundCategory, random);
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")}, cancellable = true)
    private void tickHook(CallbackInfo callbackInfo) {
        ElytraFly elytrafly_lcl = getElytrafly();

        if (elytrafly_lcl == null) return;

        if (elytrafly_lcl.isToggled() && elytrafly_lcl.muteElytra.getValue().booleanValue()) {
            this.volume = 0.0f;
            callbackInfo.cancel();
        }
    }
}
