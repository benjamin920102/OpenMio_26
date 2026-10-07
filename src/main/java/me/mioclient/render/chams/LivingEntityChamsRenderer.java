package me.mioclient.render.chams;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.ClonedPlayerEntity;

import me.mioclient.mode.render.MatrixStackDataMode;

import me.mioclient.api.ClientAccessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntityChamsRenderer implements ClientAccessor, EntityRenderCallback<LivingEntity> {
    public boolean flag;
    public boolean flag2 = false;
    public boolean flag3 = false;

    @Override
    public void do721(LivingEntity livingEntity, float f, PoseStack matrixStack) {
        this.flag = minecraftClient.player == livingEntity;
        this.flag2 = me.mioclient.util.player.ClonedPlayerEntity.class.isInstance(livingEntity);
        if (this.flag2) {
            f = 1.0f;
        }
        if (this.flag2) {
            this.flag3 = ((me.mioclient.util.player.ClonedPlayerEntity) (Object) livingEntity).isChamsVisible();
        } else {
            this.flag3 = false;
        }
        matrixStack.pushPose();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        ChamsRenderContext matrixStackData = new ChamsRenderContext(matrixStack, MatrixStackDataMode.BOTH);
        renderLivingParts(matrixStackData, livingEntity);
        matrixStack.popPose();
    }

    public void renderLivingParts(ChamsRenderContext matrixStackData, LivingEntity livingEntity) {
        me.mioclient.render.shape.BoxRenderer.drawBoxOutline(matrixStackData.getMatrixStack(), livingEntity.getBoundingBox(), ChamsRenderDispatcher.color, (float) ChamsRenderDispatcher.val);
    }

    public void renderCustomModel(ChamsRenderContext matrixStackData, Object entityModel) {
    }

    public <T> T selectForLocalPlayer(T t, T t2) {
        return this.flag ? t2 : t;
    }
}
