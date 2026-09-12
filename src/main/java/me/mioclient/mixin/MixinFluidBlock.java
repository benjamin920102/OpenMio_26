package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.exploit.Reach;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({FluidBlock.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinFluidBlock.class */
public class MixinFluidBlock {
    private static Reach reach;

    private static Reach getReach() {

        if (reach == null && ClientServices.moduleInjector != null) {

            try { reach = (Reach) ClientServices.moduleInjector.getModule(Reach.class); } catch (Exception e) {}

        }

        return reach;

    }

    @Inject(method = {"getOutlineShape"}, at = {@At("HEAD")}, cancellable = true)
    private void getOutlineShapeHook(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext, CallbackInfoReturnable<VoxelShape> callbackInfoReturnable) {
        Reach reach_lcl = getReach();

        if (reach_lcl == null) return;

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (reach_lcl.isToggled() && reach_lcl.liquidPlace.getValue().booleanValue()) {
            if ((minecraftClient.player.getMainHandStack().getItem() instanceof BlockItem) || (minecraftClient.player.getOffHandStack().getItem() instanceof BlockItem)) {
                boolean z = false;
                Direction[] values = Direction.values();
                int length = values.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    if (blockView.getBlockState(blockPos.offset(values[i])).isAir()) {
                        z = true;
                        break;
                    }
                    i++;
                }
                if (MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(blockPos.toCenterPos()) >= reach_lcl.modifier.getValue().floatValue() * reach_lcl.modifier.getValue().floatValue() || z) {
                    callbackInfoReturnable.setReturnValue(VoxelShapes.fullCube());
                    callbackInfoReturnable.cancel();
                }
            }
        }
    }
}
