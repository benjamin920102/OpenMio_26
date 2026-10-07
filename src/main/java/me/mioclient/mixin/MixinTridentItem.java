package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mioclient.module.exploit.Trident;
import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({TridentItem.class})

public class MixinTridentItem {
    private static Trident trident;

    private static Trident getTrident() {

        if (trident == null && ClientServices.moduleInjector != null) {

            try { trident = (Trident) ClientServices.moduleInjector.getModule(Trident.class); } catch (Exception e) {}

        }

        return trident;

    }

    /* 26.2 port: Item.onStoppedUsing -> TridentItem.releaseUsing (bytecode-verified:
       still calls Player.isInWaterOrRain, Player.push(DDD) and uses const 10). */
    @ModifyExpressionValue(method = {"releaseUsing"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z")})
    private boolean onStoppedUsingHook(boolean z) {
        Trident trident_lcl = getTrident();

        if (trident_lcl == null) return z;

        return z || trident_lcl.isAlwaysBoosting();
    }

    @ModifyExpressionValue(method = {"use"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z")})
    private boolean useHook(boolean z) {
        Trident trident_lcl = getTrident();

        if (trident_lcl == null) return z;

        return z || trident_lcl.isAlwaysBoosting();
    }

    @ModifyArgs(method = {"releaseUsing"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;push(DDD)V"))
    private void onStoppedUsingHook(Args args) {
        Trident trident_lcl = getTrident();

        if (trident_lcl == null) return;

        args.set(0, Double.valueOf(((Double) args.get(0)).doubleValue() * trident_lcl.getBoostMultiplier()));
        args.set(1, Double.valueOf(((Double) args.get(1)).doubleValue() * trident_lcl.getBoostMultiplier()));
        args.set(2, Double.valueOf(((Double) args.get(2)).doubleValue() * trident_lcl.getBoostMultiplier()));
    }

    @ModifyConstant(method = {"releaseUsing"}, constant = {@Constant(intValue = 10)})
    private int onStoppedUsing(int i) {
        Trident trident_lcl = getTrident();

        if (trident_lcl == null) return i;

        if (trident_lcl.isToggled()) {
            return 0;
        }
        return i;
    }
}
