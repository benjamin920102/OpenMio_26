package me.mioclient.mixin;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({MessageIndicator.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinMessageIndicator.class */
public class MixinMessageIndicator {
    @Inject(method = {"indicatorColor"}, at = {@At("HEAD")}, cancellable = true)
    private void indicatorColorHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (((Object) this) == ChatUtils.messageIndicator) {
            Color color811 = ColorUtils.getThemeColor();
            callbackInfoReturnable.setReturnValue(Integer.valueOf(ColorUtils.getGradientColor(color811, color811.darker(), 3000.0d, 0.0d).hashCode()));
            callbackInfoReturnable.cancel();
        }
    }
}
