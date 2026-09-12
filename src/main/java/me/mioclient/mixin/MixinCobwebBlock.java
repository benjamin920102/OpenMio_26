package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.FastWeb;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({CobwebBlock.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinCobwebBlock.class */
public class MixinCobwebBlock {
    private static FastWeb fastweb;

    private static FastWeb getFastweb() {

        if (fastweb == null && ClientServices.moduleInjector != null) {

            try { fastweb = (FastWeb) ClientServices.moduleInjector.getModule(FastWeb.class); } catch (Exception e) {}

        }

        return fastweb;

    }

    @Inject(method = {"onEntityCollision"}, at = {@At("HEAD")}, cancellable = true)
    private void onEntityCollisionHook(BlockState blockState, World world, BlockPos blockPos, Entity entity, CallbackInfo callbackInfo) {
        FastWeb fastweb_lcl = getFastweb();

        if (fastweb_lcl == null) return;

        if (fastweb_lcl.isGrimMode()) {
            callbackInfo.cancel();
        }
    }
}
