package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.FastWeb;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified via JDK25 javap): WebBlock.onEntityCollision gone;
   now entityInside(BlockState, Level, BlockPos, Entity, InsideBlockEffectApplier, boolean). */
@Mixin({WebBlock.class})

public class MixinCobwebBlock {
    private static FastWeb fastweb;

    private static FastWeb getFastweb() {

        if (fastweb == null && ClientServices.moduleInjector != null) {

            try { fastweb = (FastWeb) ClientServices.moduleInjector.getModule(FastWeb.class); } catch (Exception e) {}

        }

        return fastweb;

    }

    @Inject(method = {"entityInside"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void onEntityCollisionHook(BlockState blockState, Level world, BlockPos blockPos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean bl, CallbackInfo callbackInfo) {
        FastWeb fastweb_lcl = getFastweb();

        if (fastweb_lcl == null) return;

        if (fastweb_lcl.isGrimMode()) {
            callbackInfo.cancel();
        }
    }
}
