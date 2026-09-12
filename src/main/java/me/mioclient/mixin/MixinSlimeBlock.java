package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.NoSlow;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({SlimeBlock.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinSlimeBlock.class */
public class MixinSlimeBlock {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    @Inject(method = {"onSteppedOn"}, at = {@At("HEAD")}, cancellable = true)
    private void onSteppedOnHook(World world, BlockPos blockPos, BlockState blockState, Entity entity, CallbackInfo callbackInfo) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return;

        if (noslow_lcl.isToggled() && noslow_lcl.slime.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"bounce"}, at = {@At("HEAD")}, cancellable = true)
    private void bounceHook(Entity entity, CallbackInfo callbackInfo) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return;

        if (noslow_lcl.isToggled() && noslow_lcl.slime.getValue().booleanValue() && noslow_lcl.noSlimeBounce.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }
}
