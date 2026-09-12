package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.Tooltips;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin({TooltipBackgroundRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinTooltipBackgroundRenderer.class */
public class MixinTooltipBackgroundRenderer {
    private static Tooltips tooltips;

    private static Tooltips getTooltips() {
        if (tooltips == null && ClientServices.moduleInjector != null) {
            try { tooltips = (Tooltips) ClientServices.moduleInjector.getModule(Tooltips.class); } catch (Exception e) {}
        }
        return tooltips;
    }

    @ModifyArgs(method = {"render"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V"))
    private static void renderHook(Args args) {
        Tooltips tt = getTooltips();
        if (tt == null || !tt.isToggled() || tt.getTintColor() == null) {
            return;
        }
        args.set(6, Integer.valueOf(tt.getTintColor().hashCode()));
        args.set(7, Integer.valueOf(tt.getTintColor().darker().hashCode()));
        tt.setShulkerTintColor(null);
    }
}
