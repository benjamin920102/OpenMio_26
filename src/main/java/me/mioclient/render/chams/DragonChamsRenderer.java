package me.mioclient.render.chams;

import me.mioclient.api.ClientAccessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import com.mojang.math.Axis;

public class DragonChamsRenderer implements ClientAccessor, EntityRenderCallback<EnderDragon> {
    @Override
    public void do721(EnderDragon enderDragonEntity, float f, PoseStack matrixStack) {
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(-enderDragonEntity.getYRot()));
        matrixStack.translate(0.0f, 0.0f, 1.0f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        me.mioclient.render.shape.BoxRenderer.drawBoxOutline(matrixStack, enderDragonEntity.getBoundingBox(), ChamsRenderDispatcher.color, (float) ChamsRenderDispatcher.val);
        matrixStack.popPose();
    }
}
