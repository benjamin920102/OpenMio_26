package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.render.shape.Shape2DRenderer;

import me.mioclient.command.ClientCommandRegistry;

import me.mioclient.module.render.Blur;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port (verified via JDK25 javap): Screen.renderBlurredBackground ->
   extractBlurredBackground; renderBackground -> extractBackground;
   handleTextClick/renderInGameBackground gone — hooks deleted. */
@Mixin({Screen.class})

public abstract class MixinScreen {
    private static NoRender norender;
    private static Blur blur;

    private static NoRender getNorender() {
        if (norender == null && ClientServices.moduleInjector != null) {
            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}
        }
        return norender;
    }

    private static Blur getBlur() {
        if (blur == null && ClientServices.moduleInjector != null) {
            try { blur = (Blur) ClientServices.moduleInjector.getModule(Blur.class); } catch (Exception e) {}
        }
        return blur;
    }

    @Shadow
    @Nullable
    protected Minecraft minecraft;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    protected abstract void extractBlurredBackground(GuiGraphicsExtractor drawContext);

    @Inject(method = {"extractBackground"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractBlurredBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V", shift = At.Shift.BEFORE)}, cancellable = true, require = 0)
    private void renderBackground(GuiGraphicsExtractor drawContext, int i, int i2, float f, CallbackInfo callbackInfo) {
        /* Every screen frame gets its live extractor so GUI-phase 2D shapes route through
           vanilla fills instead of discarded immediate buffers. */
        Shape2DRenderer.setPendingExtractor(drawContext);
        Blur blurLocal = getBlur();
        if (blurLocal != null && blurLocal.isToggled() && this.minecraft.level != null && this.minecraft.player != null) {
            extractBlurredBackground(drawContext);
            callbackInfo.cancel();
        } else if (Minecraft.getInstance().gui.screen() instanceof AbstractClickGuiScreen) {
            extractBlurredBackground(drawContext);
            callbackInfo.cancel();
        }
    }
}
