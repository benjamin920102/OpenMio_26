package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.NoRender;
import me.mioclient.module.render.SkyColor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port: BossHealthOverlay.render -> extractRenderState, shouldDarkenSky -> shouldDarkenScreen. */
@Mixin({BossHealthOverlay.class})

public class MixinBossBarHud {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static SkyColor skycolor;

    private static SkyColor getSkycolor() {

        if (skycolor == null && ClientServices.moduleInjector != null) {

            try { skycolor = (SkyColor) ClientServices.moduleInjector.getModule(SkyColor.class); } catch (Exception e) {}

        }

        return skycolor;

    }

    @Inject(method = {"extractRenderState"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void renderHook(GuiGraphicsExtractor drawContext, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.bossBars.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"shouldDarkenScreen"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void shouldDarkenSkyHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NoRender norender_lcl = getNorender();
        SkyColor skycolor_lcl = getSkycolor();

        if (norender_lcl == null) return;

        if (skycolor_lcl == null) return;

        if ((norender_lcl.isToggled() && norender_lcl.bossBars.getValue().booleanValue()) || (skycolor_lcl.isToggled() && skycolor_lcl.isDimensionEnabled())) {
            callbackInfoReturnable.setReturnValue(false);
            callbackInfoReturnable.cancel();
        }
    }
}
