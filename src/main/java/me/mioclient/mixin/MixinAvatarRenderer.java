package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.Shader;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/* First-person hands never enter the outline target by themselves, so the hand
   model submit is redirected to the outline-color overload (same shape as
   Disa's). Gated on the Hands + hand-outline settings; disabled hands keep the
   plain submit. */
@Mixin({AvatarRenderer.class})
public abstract class MixinAvatarRenderer {
    @Redirect(method = {"renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private void mio$handsOutline(SubmitNodeCollector collector, ModelPart part, PoseStack poseStack, RenderType renderType, int l1, int l2, TextureAtlasSprite sprite) {
        collector.submitModelPart(part, poseStack, renderType, l1, l2, sprite, -1, null, handsOutlineColor());
    }

    private static int handsOutlineColor() {
        try {
            if (ClientServices.moduleInjector == null) {
                return 0;
            }
            Shader shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class);
            if (shader == null || !shader.isToggled() || !shader.hands.getValue().booleanValue() || shader.isHandOutlineDisabled()) {
                return 0;
            }
            return shader.outline.getValue().hashCode();
        } catch (Exception e) {
            return 0;
        }
    }
}
