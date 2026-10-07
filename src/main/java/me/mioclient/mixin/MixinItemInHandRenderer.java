package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.Shader;

import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/* Held-item submits carry an outline color int as their last argument (0 by
   default). When hand outlines are on it is replaced with the hands outline
   color; otherwise the original value passes through untouched. */
@Mixin({ItemInHandRenderer.class})
public abstract class MixinItemInHandRenderer {
    @ModifyArg(method = {"renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), index = 4)
    private int mio$handsItemOutline(int outlineColor) {
        try {
            if (ClientServices.moduleInjector == null) {
                return outlineColor;
            }
            Shader shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class);
            if (shader == null || !shader.isToggled() || !shader.hands.getValue().booleanValue() || shader.isHandOutlineDisabled()) {
                return outlineColor;
            }
            return shader.outline.getValue().hashCode();
        } catch (Exception e) {
            return outlineColor;
        }
    }
}
