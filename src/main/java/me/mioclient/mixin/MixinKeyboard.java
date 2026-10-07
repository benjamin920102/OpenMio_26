package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.input.CharEvent;
import me.mioclient.event.events.input.KeyEvent;
import me.mioclient.module.misc.BetterChat;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyboardHandler.class})

public class MixinKeyboard {

    @Shadow
    @Final
    private Minecraft minecraft;

    /* 26.2 port: KeyboardHandler.onKey(long,int,int,int,int) ->
       keyPress(long, int action, KeyEvent(key, scancode, modifiers));
       action 1 == GLFW press, same as before. */
    @Inject(method = {"keyPress"}, at = {@At("TAIL")}, cancellable = true)
    private void onKey(long window, int action, net.minecraft.client.input.KeyEvent keyEvent, CallbackInfo callbackInfo) {
        if (keyEvent.key() >= 0 && this.minecraft.gui.screen() == null && action == 1) {
            KeyEvent event = new KeyEvent(keyEvent.key(), false);
            ClientAccessor.eventBus.fireEvent(event);
            if (event.isCancelled()) {
                callbackInfo.cancel();
            }
        }
    }

    /* 26.2 port: onChar(long,int,int) -> charTyped(long, CharacterEvent). */
    @Inject(method = {"charTyped"}, at = {@At("HEAD")})
    public void onChar(long window, net.minecraft.client.input.CharacterEvent characterEvent, CallbackInfo callbackInfo) {
        if (this.minecraft.gui.screen() == null && window == this.minecraft.getWindow().handle()) {
            ClientAccessor.eventBus.fireEvent(new CharEvent(characterEvent.codepoint()));
        }
    }

    @Inject(method = {"processF3"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clear(Z)V", shift = At.Shift.BEFORE)}, require = 0)
    private void processF3Pre(int i, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BetterChat.flag2 = true;
    }

    @Inject(method = {"processF3"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clear(Z)V", shift = At.Shift.AFTER)}, require = 0)
    private void processF3Post(int i, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BetterChat.flag2 = false;
    }
}
