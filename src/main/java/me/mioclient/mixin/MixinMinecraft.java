package me.mioclient.mixin;

import me.mioclient.event.events.client.ClientTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.screen.ConfirmGameClose;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.gui.screen.AbstractClickGuiScreen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import me.mioclient.event.events.client.ClientTickEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.events.render.RenderEvent;
import me.mioclient.event.events.screen.SetScreenHookPostEvent;
import me.mioclient.event.events.screen.SetScreenHookPreEvent;
import me.mioclient.gui.screen.ConfirmGameClose;
import me.mioclient.module.client.UI;
import me.mioclient.module.combat.Arrows;
import me.mioclient.module.combat.AutoClicker;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.combat.NoHitDelay;
import me.mioclient.module.exploit.MultiTask;
import me.mioclient.module.misc.AntiQuit;
import me.mioclient.module.misc.UnfocusedFPS;
import me.mioclient.module.player.AutoEat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
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

/* compiled from: 0.java */
@Mixin({MinecraftClient.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinMinecraft.class */
public class MixinMinecraft {

    @Unique
    private Screen mio$lastScreen;

    @Shadow
    private boolean field_1695;

    @Shadow
    public int field_1771;

    @Shadow
    @Final
    public GameOptions field_1690;

    @Shadow
    @Nullable
    public Screen field_1755;

    @Shadow
    @Final
    private Window field_1704;
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

    @Inject(method = {"doAttack()Z"}, at = {@At("HEAD")})
    public void doAttackHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NoHitDelay nohitdelay_lcl = getNohitdelay();
        AutoClicker autoclicker_lcl = getAutoclicker();

        if (nohitdelay_lcl == null) return;

        if (autoclicker_lcl == null) return;

        if (nohitdelay_lcl.isToggled() || autoclicker_lcl.isToggled()) {
            this.field_1771 = 0;
        }
    }

    @Inject(method = {"stop"}, at = {@At("HEAD")})
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

    @Inject(method = {"disconnect(Lnet/minecraft/client/gui/screen/Screen;)V"}, at = {@At("HEAD")})
    public void disconnect(Screen screen, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new DisconnectEvent());
    }

    @ModifyExpressionValue(method = {"handleBlockBreaking"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z")})
    public boolean handleBlockBreakingHook(boolean z) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return z;

        return !multitask_lcl.isToggled() && z;
    }

    @Redirect(method = {"doItemUse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"), require = 0)
    public boolean doItemUseHook(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return false;

        return !multitask_lcl.isToggled() && clientPlayerInteractionManager.isBreakingBlock();
    }

    @Inject(method = {"setScreen"}, at = {@At("HEAD")}, cancellable = true)
    private void setScreenHookPre(Screen screen, CallbackInfo callbackInfo) {
        this.mio$lastScreen = this.field_1755;
        SetScreenHookPreEvent setScreenHookPreEvent = new SetScreenHookPreEvent(this.mio$lastScreen, screen);
        ClientAccessor.eventBus.fireEvent(setScreenHookPreEvent);
        if (setScreenHookPreEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"setScreen"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V", shift = At.Shift.AFTER)}, cancellable = true)
    private void setScreenHookPost(Screen screen, CallbackInfo callbackInfo) {
        SetScreenHookPostEvent setScreenHookPostEvent = new SetScreenHookPostEvent(this.mio$lastScreen, screen);
        ClientAccessor.eventBus.fireEvent(setScreenHookPostEvent);
        if (setScreenHookPostEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void tickHook(CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(ClientTickEvent.fontsEvent);
    }

    @ModifyExpressionValue(method = {"handleInputEvents"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z")})
    private boolean handleInputEventsHook(boolean z) {
        AutoEat autoeat_lcl = getAutoeat();

        if (autoeat_lcl == null) return z;

        if (autoeat_lcl.is2086() && autoeat_lcl.isToggled()) {
            return false;
        }
        return z;
    }

    @Inject(method = {"getFramerateLimit"}, at = {@At("HEAD")}, cancellable = true)
    public void getFramerateLimit(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        UnfocusedFPS unfocusedfps_lcl = getUnfocusedfps();

        if (unfocusedfps_lcl == null) return;

        if (!unfocusedfps_lcl.isToggled() || this.field_1695) {
            if ((this.field_1755 instanceof AbstractClickGuiScreen) && this.field_1695) {
                callbackInfoReturnable.setReturnValue(Integer.valueOf(this.field_1704.getFramerateLimit()));
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

    @WrapWithCondition(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;scheduleStop()V")})
    private boolean renderHook(MinecraftClient minecraftClient) {
        AntiQuit antiquit_lcl = getAntiquit();

        if (antiquit_lcl == null) return true;

        if (!antiquit_lcl.isToggled() || !antiquit_lcl.gameClose.getValue().booleanValue()) {
            return true;
        }
        if (minecraftClient.currentScreen instanceof ConfirmGameClose) {
            return false;
        }
        minecraftClient.setScreen(new ConfirmGameClose());
        return false;
    }

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;runTasks()V", shift = At.Shift.AFTER)})
    private void render(boolean z, CallbackInfo callbackInfo, @Local int i) {
        ClientAccessor.eventBus.fireEvent(new RenderEvent(i >= 1));
    }

    @ModifyExpressionValue(method = {"handleInputEvents"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z")})
    private boolean handleInputEvents(boolean z) {
        Arrows arrows_lcl = getArrows();

        if (arrows_lcl == null) return z;

        if (arrows_lcl.isToggled() && arrows_lcl.autoShoot.getValue().booleanValue()) {
            return false;
        }
        return z;
    }

    @Inject(method = {"doItemUse"}, at = {@At("HEAD")})
    private void doItemUse(CallbackInfo callbackInfo) {
        AutoCrystal autocrystal_lcl = getAutocrystal();

        if (autocrystal_lcl == null) return;

        autocrystal_lcl.cancel();
    }

    @ModifyExpressionValue(method = {"doItemUse"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")})
    private boolean doItemUseHook(boolean z) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return z;

        if (multitask_lcl.isToggled()) {
            return false;
        }
        return z;
    }

    @ModifyExpressionValue(method = {"doAttack"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z")})
    private boolean doAttack(boolean z) {
        MultiTask multitask_lcl = getMultitask();

        if (multitask_lcl == null) return z;

        if (multitask_lcl.isToggled()) {
            return false;
        }
        return z;
    }
}
