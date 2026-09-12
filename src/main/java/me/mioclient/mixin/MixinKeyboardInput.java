package me.mioclient.mixin;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.input.TickHookPreEvent;
import me.mioclient.mixin.ducks.DuckKeyBinding;
import me.mioclient.module.movement.NoSlow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({KeyboardInput.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinKeyboardInput.class */
public class MixinKeyboardInput extends Input {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    @Shadow
    @Final
    private GameOptions field_3902;

    @Inject(method = {"tick"}, at = {@At("HEAD")}, cancellable = true)
    private void tickHookPre(boolean z, float f, CallbackInfo callbackInfo) {
        TickHookPreEvent tickHookPreEvent = new TickHookPreEvent((KeyboardInput)(Object) this, z ? f : -1.0f);
        ClientAccessor.eventBus.fireEvent(tickHookPreEvent);
        if (tickHookPreEvent.isCancelled()) {
            ClientAccessor.eventBus.fireEvent(new InputTickEvent(tickHookPreEvent.getInput(), tickHookPreEvent.getSpeed()));
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At("TAIL")}, cancellable = true)
    private void tickHook(boolean z, float f, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new InputTickEvent((KeyboardInput)(Object) this, z ? f : -1.0f));
    }

    @Redirect(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"), require = 0)
    private boolean tickHook2(KeyBinding keyBinding) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return false;

        return (noslow_lcl.isToggled() && noslow_lcl.isOffhandSwapAllowed() && noslow_lcl.guiMove.getValue().booleanValue() && keyBinding != this.field_3902.sneakKey) ? GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((DuckKeyBinding) keyBinding).getKey().getCode()) == 1 : keyBinding.isPressed();
    }
}
