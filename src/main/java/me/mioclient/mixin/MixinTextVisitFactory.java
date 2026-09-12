package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.player.NameProtect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/* compiled from: 0.java */
@Mixin({TextVisitFactory.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinTextVisitFactory.class */
public class MixinTextVisitFactory {
    private static NameProtect nameprotect;

    private static NameProtect getNameprotect() {

        if (nameprotect == null && ClientServices.moduleInjector != null) {

            try { nameprotect = (NameProtect) ClientServices.moduleInjector.getModule(NameProtect.class); } catch (Exception e) {}

        }

        return nameprotect;

    }

    @ModifyArg(method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), index = 0)
    private static String adjustText(String str) {
        NameProtect nameprotect_lcl = getNameprotect();

        if (nameprotect_lcl == null) return null;

        if (nameprotect_lcl.isToggled() && MinecraftClient.getInstance().world != null) {
            str = str.replace(MinecraftClient.getInstance().getSession().getUsername(), nameprotect_lcl.name.getValue());
        }
        return str;
    }
}
