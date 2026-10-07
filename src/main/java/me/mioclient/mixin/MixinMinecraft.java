package me.mioclient.mixin;

import me.mioclient.event.events.client.ClientTickEvent;
import net.minecraft.client.player.LocalPlayer;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.screen.ConfirmGameClose;

import me.mioclient.api.ClientAccessor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.events.render.RenderEvent;
import me.mioclient.event.events.screen.SetScreenHookPostEvent;
import me.mioclient.event.events.screen.SetScreenHookPreEvent;
import me.mioclient.module.client.UI;
import me.mioclient.module.combat.Arrows;
import me.mioclient.module.combat.AutoClicker;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.combat.NoHitDelay;
import me.mioclient.module.exploit.MultiTask;
import me.mioclient.module.misc.AntiQuit;
import me.mioclient.module.misc.UnfocusedFPS;
import me.mioclient.module.player.AutoEat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import com.mojang.blaze3d.platform.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Minecraft.class})

public abstract class MixinMinecraft {

    @Shadow
    private boolean running;

    @Shadow
    public abstract boolean isWindowActive();

    @Shadow
    public int missTime;

    @Shadow
    @Final
    public Options options;

    @Shadow
    @Final
    public net.minecraft.client.gui.Gui gui;

    @Shadow
    @Final
    private Window window;
    private static UI clickgui;

    private static UI getClickgui() {

        if (clickgui == null && ClientServices.moduleInjector != null) {

            try { clickgui = (UI) ClientServices.moduleInjector.getModule(UI.class); } catch (Exception e) {}

        }

        return clickgui;

    }
    private static MultiTask multitask;

    private static MultiTask getMultitask() {

        if (multitask == null && ClientServices.moduleInjector != null) {

            try { multitask = (MultiTask) ClientServices.moduleInjector.getModule(MultiTask.class); } catch (Exception e) {}

        }

        return multitask;

    }
    private static AutoEat autoeat;

    private static AutoEat getAutoeat() {

        if (autoeat == null && ClientServices.moduleInjector != null) {

            try { autoeat = (AutoEat) ClientServices.moduleInjector.getModule(AutoEat.class); } catch (Exception e) {}

        }

        return autoeat;

    }
    private static UnfocusedFPS unfocusedfps;

    private static UnfocusedFPS getUnfocusedfps() {

        if (unfocusedfps == null && ClientServices.moduleInjector != null) {

            try { unfocusedfps = (UnfocusedFPS) ClientServices.moduleInjector.getModule(UnfocusedFPS.class); } catch (Exception e) {}

        }

        return unfocusedfps;

    }
    private static AntiQuit antiquit;

    private static AntiQuit getAntiquit() {

        if (antiquit == null && ClientServices.moduleInjector != null) {

            try { antiquit = (AntiQuit) ClientServices.moduleInjector.getModule(AntiQuit.class); } catch (Exception e) {}

        }

        return antiquit;

    }
    private static NoHitDelay nohitdelay;

    private static NoHitDelay getNohitdelay() {

        if (nohitdelay == null && ClientServices.moduleInjector != null) {

            try { nohitdelay = (NoHitDelay) ClientServices.moduleInjector.getModule(NoHitDelay.class); } catch (Exception e) {}

        }

        return nohitdelay;

    }
    private static AutoClicker autoclicker;

    private static AutoClicker getAutoclicker() {

        if (autoclicker == null && ClientServices.moduleInjector != null) {

            try { autoclicker = (AutoClicker) ClientServices.moduleInjector.getModule(AutoClicker.class); } catch (Exception e) {}

        }

        return autoclicker;

    }
    private static Arrows arrows;

    private static Arrows getArrows() {

        if (arrows == null && ClientServices.moduleInjector != null) {

            try { arrows = (Arrows) ClientServices.moduleInjector.getModule(Arrows.class); } catch (Exception e) {}

        }

        return arrows;

    }
    private static AutoCrystal autocrystal;

    private static AutoCrystal getAutocrystal() {

        if (autocrystal == null && ClientServices.moduleInjector != null) {

            try { autocrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class); } catch (Exception e) {}

        }

        return autocrystal;

    }
    private static boolean saved = false;

    @Inject(method = {"startAttack()Z"}, at = {@At("HEAD")}, require = 0)
    public void doAttackHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NoHitDelay nohitdelay_lcl = getNohitdelay();
        AutoClicker autoclicker_lcl = getAutoclicker();

        if (nohitdelay_lcl == null) return;

        if (autoclicker_lcl == null) return;

        if (nohitdelay_lcl.isToggled() || autoclicker_lcl.isToggled()) {
            this.missTime = 0;
        }
    }

    @Inject(method = {"stop"}, at = {@At("HEAD")}, require = 0)
    public void scheduleStop(CallbackInfo callbackInfo) {
        UI clickgui_lcl = getClickgui();

        if (clickgui_lcl == null) return;

        if (saved || ClientServices.presetHelper == null) {
            return;
        }
        saved = true;
        clickgui_lcl.disable();
        ClientServices.presetHelper.do41();
    }

    @Inject(method = {"disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V"}, at = {@At("HEAD")}, require = 0)
    public void disconnect(Screen screen, boolean bl, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new DisconnectEvent());
    }

    @ModifyExpressionValue(method = {"handleBlockBreaking"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")}, require = 0)
    public boolean handleBlockBreakingHook(boolean z) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return z;

        return !multitask_lcl.isToggled() && z;
    }

    @Redirect(method = {"startUseItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isDestroying()Z"), require = 0)
    public boolean doItemUseHook(MultiPlayerGameMode clientPlayerInteractionManager) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return false;

        return !multitask_lcl.isToggled() && clientPlayerInteractionManager.isDestroying();
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")}, require = 0)
    private void tickHook(CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(ClientTickEvent.fontsEvent);
    }

    @ModifyExpressionValue(method = {"handleInputEvents"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")}, require = 0)
    private boolean handleInputEventsHook(boolean z) {
        AutoEat autoeat_lcl = getAutoeat();

        if (autoeat_lcl == null) return z;

        if (autoeat_lcl.is2086() && autoeat_lcl.isToggled()) {
            return false;
        }
        return z;
    }

    @Inject(method = {"getFramerateLimit"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    public void getFramerateLimit(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        UnfocusedFPS unfocusedfps_lcl = getUnfocusedfps();

        if (unfocusedfps_lcl == null) return;

        if (!unfocusedfps_lcl.isToggled() || this.running) {
            if ((this.gui.screen() instanceof AbstractClickGuiScreen) && this.running) {
                callbackInfoReturnable.setReturnValue(Integer.valueOf(this.options.framerateLimit().get()));
                return;
            }
            return;
        }
        if (unfocusedfps_lcl.fps.isHoleSnapConfigured()) {
            callbackInfoReturnable.setReturnValue(Integer.valueOf(unfocusedfps_lcl.get2003()));
        } else {
            callbackInfoReturnable.setReturnValue(unfocusedfps_lcl.fps.getValue());
        }
    }

    @WrapWithCondition(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;scheduleStop()V")}, require = 0)
    private boolean renderHook(Minecraft minecraftClient) {
        AntiQuit antiquit_lcl = getAntiquit();

        if (antiquit_lcl == null) return true;

        if (!antiquit_lcl.isToggled() || !antiquit_lcl.gameClose.getValue().booleanValue()) {
            return true;
        }
        if (minecraftClient.gui.screen() instanceof ConfirmGameClose) {
            return false;
        }
        minecraftClient.gui.setScreen(new ConfirmGameClose());
        return false;
    }

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTasks()V", shift = At.Shift.AFTER)}, require = 0)
    private void render(boolean z, CallbackInfo callbackInfo, @Local int i) {
        ClientAccessor.eventBus.fireEvent(new RenderEvent(i >= 1));
    }

    @ModifyExpressionValue(method = {"handleInputEvents"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")}, require = 0)
    private boolean handleInputEvents(boolean z) {
        Arrows arrows_lcl = getArrows();

        if (arrows_lcl == null) return z;

        if (arrows_lcl.isToggled() && arrows_lcl.autoShoot.getValue().booleanValue()) {
            return false;
        }
        return z;
    }

    @Inject(method = {"startUseItem"}, at = {@At("HEAD")}, require = 0)
    private void doItemUse(CallbackInfo callbackInfo) {
        AutoCrystal autocrystal_lcl = getAutocrystal();

        if (autocrystal_lcl == null) return;

        autocrystal_lcl.cancel();
    }

    @ModifyExpressionValue(method = {"startUseItem"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z")}, require = 0)
    private boolean doItemUseHook(boolean z) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return z;

        if (multitask_lcl.isToggled()) {
            return false;
        }
        return z;
    }

    @ModifyExpressionValue(method = {"startAttack"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z")}, require = 0)
    private boolean doAttack(boolean z) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return z;

        if (multitask_lcl.isToggled()) {
            return false;
        }
        return z;
    }
}
