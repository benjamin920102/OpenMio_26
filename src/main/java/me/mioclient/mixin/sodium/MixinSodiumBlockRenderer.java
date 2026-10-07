package me.mioclient.mixin.sodium;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.Xray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* Sodium 0.9.x (mc26.2): package moved me.jellysquid.mods.sodium -> net.caffeinemc.mods.sodium,
   and BlockRenderer.renderModel(BlockRenderContext, ChunkBuildBuffers) became
   renderModel(BlockStateModel, BlockState, BlockPos, BlockPos). */
@Pseudo
@Mixin(targets = {"net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer"}, remap = false)
public class MixinSodiumBlockRenderer {
    private static Xray xray;

    private static Xray getXray() {

        if (xray == null && ClientServices.moduleInjector != null) {

            try { xray = (Xray) ClientServices.moduleInjector.getModule(Xray.class); } catch (Exception e) {}

        }

        return xray;

    }

    @Inject(method = {"renderModel"}, at = {@At("HEAD")}, cancellable = true, remap = false)
    private void onRenderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo callbackInfo) {
        Xray xray_lcl = getXray();

        if (xray_lcl == null) return;

        if (Minecraft.getInstance().player == null || !xray_lcl.isToggled() || xray_lcl.shouldRenderBlock(pos, state.getBlock())) {
            return;
        }
        callbackInfo.cancel();
    }
}
