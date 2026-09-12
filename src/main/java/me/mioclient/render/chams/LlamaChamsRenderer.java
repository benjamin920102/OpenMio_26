package me.mioclient.render.chams;


import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.util.math.MatrixStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/LlamaChamsRenderer.class */
public class LlamaChamsRenderer extends LivingEntityChamsRenderer {
    @Override // me.mioclient.render.chams.LivingEntityChamsRenderer
    public void renderCustomModel(ChamsRenderContext matrixStackData, EntityModel<?> entityModel) {
        MatrixStack matrixStack1013 = matrixStackData.getMatrixStack();
        if (entityModel instanceof LlamaEntityModel) {
            LlamaEntityModel llamaEntityModel = (LlamaEntityModel) entityModel;
            if (!llamaEntityModel.child) {
                ImmutableList.of(llamaEntityModel.head, llamaEntityModel.body, llamaEntityModel.rightHindLeg, llamaEntityModel.leftHindLeg, llamaEntityModel.rightFrontLeg, llamaEntityModel.leftFrontLeg, llamaEntityModel.rightChest, llamaEntityModel.leftChest).forEach(modelPart -> {
                    ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart);
                });
                return;
            }
            matrixStack1013.push();
            matrixStack1013.scale(0.71428573f, 0.64935064f, 0.7936508f);
            matrixStack1013.translate(0.0f, 1.3125f, 0.22f);
            ChamsRenderDispatcher.renderModelPart(matrixStackData, llamaEntityModel.head);
            matrixStack1013.pop();
            matrixStack1013.push();
            matrixStack1013.scale(0.625f, 0.45454544f, 0.45454544f);
            matrixStack1013.translate(0.0f, 2.0625f, 0.0f);
            ChamsRenderDispatcher.renderModelPart(matrixStackData, llamaEntityModel.body);
            matrixStack1013.pop();
            matrixStack1013.push();
            matrixStack1013.scale(0.45454544f, 0.41322312f, 0.45454544f);
            matrixStack1013.translate(0.0f, 2.0625f, 0.0f);
            ImmutableList.of(llamaEntityModel.rightHindLeg, llamaEntityModel.leftHindLeg, llamaEntityModel.rightFrontLeg, llamaEntityModel.leftFrontLeg, llamaEntityModel.rightChest, llamaEntityModel.leftChest).forEach(modelPart2 -> {
                ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart2);
            });
            matrixStack1013.pop();
        }
    }
}
