package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.NoRender;
import me.mioclient.module.render.SkyColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({BossBarHud.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinBossBarHud.class */
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

    @Inject(method = {"render"}, at = {@At("HEAD")}, cancellable = true)
    private void renderHook(DrawContext drawContext, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.bossBars.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"shouldDarkenSky"}, at = {@At("HEAD")}, cancellable = true)
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
