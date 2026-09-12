package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.mioclient.event.events.input.KeyEvent;
import me.mioclient.event.events.input.MouseScrollEvent;
import me.mioclient.event.events.input.UpdateMouseEvent;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({Mouse.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinMouse.class */
public class MixinMouse {
    @Inject(method = {"onMouseButton"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;onKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;)V")})
    private void onMouseButtonHook(long j, int i, int i2, int i3, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new KeyEvent(i, true));
    }

    @WrapWithCondition(method = {"updateMouse"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V")})
    private boolean updateMouseHook(ClientPlayerEntity clientPlayerEntity, double d, double d2) {
        UpdateMouseEvent updateMouseEvent = new UpdateMouseEvent(d, d2);
        ClientAccessor.eventBus.fireEvent(updateMouseEvent);
        return !updateMouseEvent.isCancelled();
    }

    @Inject(method = {"onMouseScroll"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", shift = At.Shift.BEFORE)}, cancellable = true)
    private void onMouseScroll(long j, double d, double d2, CallbackInfo callbackInfo) {
        MouseScrollEvent mouseScrollEvent = new MouseScrollEvent(d, d2);
        ClientAccessor.eventBus.fireEvent(mouseScrollEvent);
        if (mouseScrollEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
