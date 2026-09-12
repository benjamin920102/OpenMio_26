package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.mioclient.module.render.Ambience;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/* compiled from: 0.java */
@Mixin(targets = {"net.minecraft.client.option.GameOptions$3"})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinGameOptionsVisitor.class */
public class MixinGameOptionsVisitor {
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }

    @WrapWithCondition(method = {"method_42572"}, at = {@At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;)V")})
    private static boolean acceptHook(Logger logger, String str) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return true;

        return ambience_lcl.brightness.getValue() != Ambience.MixinEntityRendererMode.GAMMA;
    }
}
