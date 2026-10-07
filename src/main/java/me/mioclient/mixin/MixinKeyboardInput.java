package me.mioclient.mixin;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.input.TickHookPreEvent;
import me.mioclient.mixin.ducks.DuckKeyBinding;
import me.mioclient.module.movement.NoSlow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified): Yarn KeyboardInput is MojMap client.player.KeyboardInput
   (extends ClientInput); tick() takes no args; Options holds public keyShift etc;
   KeyMapping state is isDown(); bound key via DuckKeyBinding.getKey().getValue(). */
@Mixin({KeyboardInput.class})
public class MixinKeyboardInput extends ClientInput {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    @Shadow
    @Final
    private Options options;

    @Inject(method = {"tick"}, at = {@At("HEAD")}, cancellable = true)
    private void tickHookPre(CallbackInfo callbackInfo) {
        TickHookPreEvent tickHookPreEvent = new TickHookPreEvent((KeyboardInput)(Object) this);
        ClientAccessor.eventBus.fireEvent(tickHookPreEvent);
        if (tickHookPreEvent.isCancelled()) {
            ClientAccessor.eventBus.fireEvent(new InputTickEvent(tickHookPreEvent.getInput()));
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At("TAIL")}, cancellable = true)
    private void tickHook(CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new InputTickEvent((KeyboardInput)(Object) this));
    }

    @Redirect(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"), require = 0)
    private boolean tickHook2(KeyMapping keyBinding) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return false;

        return (noslow_lcl.isToggled() && noslow_lcl.isOffhandSwapAllowed() && noslow_lcl.guiMove.getValue().booleanValue() && keyBinding != this.options.keyShift) ? GLFW.glfwGetKey(Minecraft.getInstance().getWindow().handle(), ((DuckKeyBinding) keyBinding).getKey().getValue()) == 1 : keyBinding.isDown();
    }
}
