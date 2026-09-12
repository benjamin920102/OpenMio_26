package me.mioclient.mixin;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shape.Shape2DRenderer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.RenderUtil;
import me.mioclient.render.shape.Shape2DRenderer;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.systems.RenderSystem;
import me.mioclient.event.events.render.HudRenderEvent;
import me.mioclient.module.client.HUD;
import me.mioclient.module.combat.NoHitDelay;
import me.mioclient.module.render.Crosshair;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin({InGameHud.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinInGameHud.class */
public class MixinInGameHud implements ClientAccessor {
    private static HUD hud;

    private static HUD getHud() {

        if (hud == null && ClientServices.moduleInjector != null) {

            try { hud = (HUD) ClientServices.moduleInjector.getModule(HUD.class); } catch (Exception e) {}

        }

        return hud;

    }
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static Crosshair crosshair;

    private static Crosshair getCrosshair() {

        if (crosshair == null && ClientServices.moduleInjector != null) {

            try { crosshair = (Crosshair) ClientServices.moduleInjector.getModule(Crosshair.class); } catch (Exception e) {}

        }

        return crosshair;

    }
    private static NoHitDelay nohitdelay;

    private static NoHitDelay getNohitdelay() {

        if (nohitdelay == null && ClientServices.moduleInjector != null) {

            try { nohitdelay = (NoHitDelay) ClientServices.moduleInjector.getModule(NoHitDelay.class); } catch (Exception e) {}

        }

        return nohitdelay;

    }

    @Inject(method = {"renderPlayerList"}, at = {@At("HEAD")})
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        HUD hud_lcl = getHud();

        if (hud_lcl == null) return;

        hud_lcl.animateBottomOffset(minecraftClient.currentScreen instanceof ChatScreen ? 12.0f : 0.0f);
        if (MinecraftClient.getInstance().inGameHud.getDebugHud().shouldShowDebugHud() || MinecraftClient.getInstance().options.hudHidden) {
            return;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawContext.fill(-1, -1, 0, 0, 0);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableCull();
        GL11.glEnable(2848);
        eventBus.fireEvent(HudRenderEvent.getMatrixStackEvent_21234(drawContext.getMatrices(), drawContext, RenderUtil.getPartialTick()));
        Shape2DRenderer.resetShadowOffset();
        RenderSystem.enableDepthTest();
        GL11.glDisable(2848);
    }

    @Inject(method = {"renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void renderHeldItemTooltipHook(DrawContext drawContext, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.heldTooltips.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"renderStatusEffectOverlay"}, at = {@At("HEAD")}, cancellable = true)
    private void renderStatusEffectOverlayHook(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        HUD hud_lcl = getHud();

        if (hud_lcl == null) return;

        if (hud_lcl.isToggled() && hud_lcl.setting2.getValue() == HUD.HUDMode.HIDE) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"renderCrosshair"}, at = {@At("HEAD")}, cancellable = true)
    private void onRenderCrosshair(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
        Crosshair crosshair_lcl = getCrosshair();

        if (crosshair_lcl == null) return;

        if (crosshair_lcl.isToggled()) {
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = {"renderCrosshair"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 1)})
    public <T> Object renderAttackIndicatorHook(T t) {
        NoHitDelay nohitdelay_lcl = getNohitdelay();

        if (nohitdelay_lcl == null) return t;

        return nohitdelay_lcl.isToggled() ? AttackIndicator.OFF : t;
    }

    @Inject(method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void renderScoreboardSidebarHook(DrawContext drawContext, ScoreboardObjective scoreboardObjective, CallbackInfo callbackInfo) {
        HUD hud_lcl = getHud();
        NoRender norender_lcl = getNorender();

        if (hud_lcl == null) return;

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.scoreBoard.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(-Math.max(hud_lcl.getSafeAreaX() - 1, 0), 0.0f, 0.0f);
    }

    @Inject(method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"}, at = {@At("TAIL")}, cancellable = true)
    private void nigger(DrawContext drawContext, ScoreboardObjective scoreboardObjective, CallbackInfo callbackInfo) {
        drawContext.getMatrices().pop();
    }

    @ModifyArgs(method = {"renderMiscOverlays"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    private void renderOverlayHook(Args args) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.blindness.getValue().booleanValue()) {
            args.set(2, Float.valueOf(0.0f));
        }
    }
}
