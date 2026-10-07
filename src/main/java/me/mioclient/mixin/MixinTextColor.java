package me.mioclient.mixin;

import java.util.function.Supplier;
import me.mioclient.ducks.DuckTextColor;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TextColor.class})

public class MixinTextColor implements DuckTextColor {

    @Unique
    private Supplier<Integer> customColorSupplier = null;

    /* 26.2 port (verified via JDK25 javap): TextColor.getRgb gone, now getValue(). */
    @Inject(method = {"getValue"}, at = {@At("HEAD")}, cancellable = true, require = 0)
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
