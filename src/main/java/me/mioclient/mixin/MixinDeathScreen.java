package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.misc.CustomDeathText;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified via JDK25 javap): DeathScreen has no 'message' field;
   death text is 'causeOfDeath'. */
@Mixin({DeathScreen.class})

public class MixinDeathScreen {
    private static CustomDeathText customdeath;

    private static CustomDeathText getCustomdeath() {

        if (customdeath == null && ClientServices.moduleInjector != null) {

            try { customdeath = (CustomDeathText) ClientServices.moduleInjector.getModule(CustomDeathText.class); } catch (Exception e) {}

        }

        return customdeath;

    }

    @Mutable
    @Shadow
    @Final
    private Component causeOfDeath;

    @Inject(method = {"<init>"}, at = {@At("RETURN")}, require = 0)
    public void mio$init(Component text, boolean z, net.minecraft.client.player.LocalPlayer player, CallbackInfo callbackInfo) {
        if (customdeath == null || !getCustomdeath().isToggled()) {
            return;
        }
        this.causeOfDeath = getCustomdeath().getText327();
    }
}
