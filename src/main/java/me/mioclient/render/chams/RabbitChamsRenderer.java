package me.mioclient.render.chams;


import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.util.math.MatrixStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/RabbitChamsRenderer.class */
public class RabbitChamsRenderer extends LivingEntityChamsRenderer {
    @Override // me.mioclient.render.chams.LivingEntityChamsRenderer
    public void renderCustomModel(ChamsRenderContext matrixStackData, EntityModel<?> entityModel) {
        MatrixStack matrixStack1013 = matrixStackData.getMatrixStack();
        if (entityModel instanceof RabbitEntityModel) {
            RabbitEntityModel rabbitEntityModel = (RabbitEntityModel) entityModel;
            if (!rabbitEntityModel.child) {
                matrixStack1013.push();
                matrixStack1013.scale(0.6f, 0.6f, 0.6f);
                matrixStack1013.translate(0.0f, 1.0f, 0.0f);
                ImmutableList.of(rabbitEntityModel.leftHindLeg, rabbitEntityModel.rightHindLeg, rabbitEntityModel.leftHaunch, rabbitEntityModel.rightHaunch, rabbitEntityModel.body, rabbitEntityModel.leftFrontLeg, rabbitEntityModel.rightFrontLeg, rabbitEntityModel.head, rabbitEntityModel.rightEar, rabbitEntityModel.leftEar, rabbitEntityModel.tail, rabbitEntityModel.nose, new ModelPart[0]).forEach(modelPart -> {
                    ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart);
                });
                matrixStack1013.pop();
                return;
            }
            matrixStack1013.push();
            matrixStack1013.scale(0.56666666f, 0.56666666f, 0.56666666f);
            matrixStack1013.translate(0.0f, 1.375f, 0.125f);
            ImmutableList.of(rabbitEntityModel.head, rabbitEntityModel.leftEar, rabbitEntityModel.rightEar, rabbitEntityModel.nose).forEach(modelPart2 -> {
                ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart2);
            });
            matrixStack1013.pop();
            matrixStack1013.push();
            matrixStack1013.scale(0.4f, 0.4f, 0.4f);
            matrixStack1013.translate(0.0f, 2.25f, 0.0f);
            ImmutableList.of(rabbitEntityModel.leftHindLeg, rabbitEntityModel.rightHindLeg, rabbitEntityModel.leftHaunch, rabbitEntityModel.rightHaunch, rabbitEntityModel.body, rabbitEntityModel.leftFrontLeg, rabbitEntityModel.rightFrontLeg, rabbitEntityModel.tail).forEach(modelPart3 -> {
                ChamsRenderDispatcher.renderModelPart(matrixStackData, modelPart3);
            });
            matrixStack1013.pop();
        }
    }
}
