package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.screen.SetScreenHookPostEvent;
import me.mioclient.event.events.screen.SetScreenHookPreEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.jspecify.annotations.Nullable;

/* 26.2 new: setScreen moved from Minecraft to Gui. */
@Mixin({Gui.class})
public class MixinGui {
    @Unique
    private Screen mio$lastScreen;

    @Shadow
    public @Nullable Screen screen() {
        return null;
    }

    @Inject(method = {"setScreen"}, at = {@At("HEAD")}, cancellable = true)
    private void setScreenHookPre(Screen screen, CallbackInfo callbackInfo) {
        this.mio$lastScreen = this.screen();
        SetScreenHookPreEvent setScreenHookPreEvent = new SetScreenHookPreEvent(this.mio$lastScreen, screen);
        ClientAccessor.eventBus.fireEvent(setScreenHookPreEvent);
        if (setScreenHookPreEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"setScreen"}, at = {@At("RETURN")})
    private void setScreenHookPost(Screen screen, CallbackInfo callbackInfo) {
        SetScreenHookPostEvent setScreenHookPostEvent = new SetScreenHookPostEvent(this.mio$lastScreen, screen);
        ClientAccessor.eventBus.fireEvent(setScreenHookPostEvent);
        if (setScreenHookPostEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
