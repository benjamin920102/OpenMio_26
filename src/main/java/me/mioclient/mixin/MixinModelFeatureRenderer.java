package me.mioclient.mixin;

import me.mioclient.ducks.DuckChamsState;
import me.mioclient.render.chams.ChamsTintConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/* 26.2 textured chams (same shape as Disa's capture/wrap, simplified): vanilla
   rasterizes every entity model through ModelFeatureRenderer.prepareModel, which
   ends in Model.renderToBuffer with the live vertex consumer. When the entity's
   render state carries a chams tint (see DuckChamsState, set in
   MixinEntityRenderer), the consumer is wrapped so the model draws textured but
   tinted. No custom pipelines, no deferred buffers. */
@Mixin({ModelFeatureRenderer.class})
public abstract class MixinModelFeatureRenderer {
    @Redirect(method = {"prepareModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private void mio$tintChams(Model model, PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int tint, ModelFeatureRenderer.Submit<?> submit) {
        VertexConsumer vc = consumer;
        try {
            Object state = submit.state();
            if (state instanceof EntityRenderState renderState) {
                int chams = ((DuckChamsState) (Object) renderState).mio$getChamsTint();
                if (chams != 0 && (chams >>> 24) != 0) {
                    vc = new ChamsTintConsumer(consumer, chams);
                }
            }
        } catch (Exception ignored) {
        }
        model.renderToBuffer(poseStack, vc, light, overlay, tint);
    }
}
