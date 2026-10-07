package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;
import me.mioclient.event.events.render.HudRenderEvent;
import me.mioclient.render.shape.Shape2DRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified via JDK25 javap): Hud.extractRenderState(extractor, deltaTracker)
   draws vanilla HUD into the live extractor. This hook fires the client's HUD event
   right after so HUD modules render through 26.2's GUI backend. Skipped while a screen
   is open (editor screens draw themselves). */
@Mixin({net.minecraft.client.gui.Hud.class})

public abstract class MixinHud {
    @Inject(method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"}, at = {@At("TAIL")})
    private void mio$fireHudRender(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || extractor == null) {
            return;
        }
        if (mc.gui.screen() != null) {
            return;
        }
        Shape2DRenderer.setPendingExtractor(extractor);
        float partialTick;
        try {
            partialTick = deltaTracker.getGameTimeDeltaTicks();
        } catch (Exception e) {
            partialTick = 1.0f;
        }
        try {
            ClientAccessor.eventBus.fireEvent(HudRenderEvent.getMatrixStackEvent_21234(new PoseStack(), extractor, partialTick));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
