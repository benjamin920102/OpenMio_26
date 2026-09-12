package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.NoSlow;
import me.mioclient.module.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({Block.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinBlock.class */
public class MixinBlock {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    @Inject(method = {"shouldDrawSide"}, at = {@At("HEAD")}, cancellable = true)
    private static void onShouldDrawHook(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction, BlockPos blockPos2, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Xray.getInstance().isToggled()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(Boolean.valueOf(Xray.getInstance().shouldRenderBlock(blockPos, blockState.getBlock())));
        }
    }

    @Inject(method = {"getSlipperiness"}, at = {@At("HEAD")}, cancellable = true)
    private void getSlipperinessHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return;

        if (noslow_lcl.isToggled()) {
            Block block = (Block)(Object) this;
            if (noslow_lcl.ice.getValue().booleanValue() && (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE)) {
                callbackInfoReturnable.setReturnValue(Float.valueOf(0.6f));
            }
            if (noslow_lcl.slime.getValue().booleanValue() && block == Blocks.SLIME_BLOCK) {
                callbackInfoReturnable.setReturnValue(Float.valueOf(0.6f));
            }
            if (noslow_lcl.honey.getValue().booleanValue() && block == Blocks.HONEY_BLOCK) {
                callbackInfoReturnable.setReturnValue(Float.valueOf(0.6f));
            }
        }
    }
}
