package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.NoSlow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SlimeBlock.class})

public class MixinSlimeBlock {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    @Inject(method = {"stepOn"}, at = {@At("HEAD")}, cancellable = true)
    private void onSteppedOnHook(Level world, BlockPos blockPos, BlockState blockState, Entity entity, CallbackInfo callbackInfo) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return;

        if (noslow_lcl.isToggled() && noslow_lcl.slime.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }
}
