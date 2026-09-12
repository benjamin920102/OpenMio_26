package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.mioclient.module.render.NoRender;
import me.mioclient.module.render.SkyColor;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({BackgroundRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinBackgroundRenderer.class */
public class MixinBackgroundRenderer {
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

    @Inject(method = {"applyFog"}, at = {@At("TAIL")})
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float f, boolean z, float f2, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();
        SkyColor skycolor_lcl = getSkycolor();

        if (norender_lcl == null) return;

        if (skycolor_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.fog.getValue().booleanValue() && (fogType == BackgroundRenderer.FogType.FOG_TERRAIN || norender_lcl.sky.getValue().booleanValue())) {
            RenderSystem.setShaderFogStart(0.0f);
            RenderSystem.setShaderFogEnd(f * norender_lcl.range.getValue().floatValue());
        }
        if (skycolor_lcl.isToggled() && skycolor_lcl.isDimensionEnabled()) {
            Color value = skycolor_lcl.fog.getValue();
            RenderSystem.setShaderFogColor(value.getRed() / 255.0f, value.getGreen() / 255.0f, value.getBlue() / 255.0f, value.getAlpha() / 255.0f);
        }
    }
}
