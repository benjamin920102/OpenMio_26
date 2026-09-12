package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({InGameOverlayRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinInGameOverlayRenderer.class */
public class MixinInGameOverlayRenderer {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Inject(method = {"renderFireOverlay"}, at = {@At("HEAD")}, cancellable = true)
    private static void onRenderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.fire.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"renderUnderwaterOverlay"}, at = {@At("HEAD")}, cancellable = true)
    private static void onRenderUnderwaterOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.blindness.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"renderInWallOverlay"}, at = {@At("HEAD")}, cancellable = true)
    private static void render(Sprite sprite, MatrixStack matrixStack, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.blindness.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }
}
