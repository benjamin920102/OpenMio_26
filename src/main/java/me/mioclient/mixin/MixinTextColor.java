package me.mioclient.mixin;

import java.util.function.Supplier;
import me.mioclient.ducks.DuckTextColor;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({TextColor.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinTextColor.class */
public class MixinTextColor implements DuckTextColor {

    @Unique
    private Supplier<Integer> customColorSupplier = null;

    @Inject(method = {"getRgb"}, at = {@At("HEAD")}, cancellable = true)
    private void getRgbHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (this.customColorSupplier != null) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(this.customColorSupplier.get());
        }
    }

    @Override // me.mioclient.ducks.DuckTextColor
    public Supplier<Integer> getSupplier() {
        return this.customColorSupplier;
    }

    @Override // me.mioclient.ducks.DuckTextColor
    public void setSupplier(Supplier<Integer> supplier) {
        this.customColorSupplier = supplier;
    }
}
