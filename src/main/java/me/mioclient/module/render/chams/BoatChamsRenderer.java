package me.mioclient.module.render.chams;

import me.mioclient.api.ClientAccessor;
import me.mioclient.mixin.ducks.DuckBoatRenderer;
import me.mioclient.mode.render.MatrixStackDataMode;
import me.mioclient.render.chams.ChamsRenderContext;
import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.render.chams.EntityRenderCallback;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.joml.Quaternionf;

// 26.2 MojMap port (verified): texturesAndModels/ListModel removed; renderer now exposes
// EntityModel<BoatRenderState> via DuckBoatRenderer, parts via Model.allParts().
public class BoatChamsRenderer implements ClientAccessor, EntityRenderCallback<AbstractBoat> {
    public static final Quaternionf quaternionf = new Quaternionf();

    @Override
    public void do721(AbstractBoat boat, float f, PoseStack matrixStack) {
        Object renderer = minecraftClient.getEntityRenderDispatcher().getRenderer(boat);
        if (!(renderer instanceof DuckBoatRenderer duck)) {
            return;
        }
        EntityModel<BoatRenderState> model = duck.mio$getModel();
        if (model == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.375f, 0.0f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f - Mth.lerp(f, boat.yRotO, boat.getYRot())));
        float hurtTime = (float) boat.getHurtTime() - f;
        float damage = Math.max(boat.getDamage() - f, 0.0f);
        if (hurtTime > 0.0f) {
            matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0f * (float) boat.getHurtDir()));
        }
        float bubble = boat.getBubbleAngle(f);
        if (!boat.isUnderWater() && !Mth.equal(bubble, 0.0f)) {
            matrixStack.mulPose(quaternionf.setAngleAxis(bubble * ((float) Math.PI / 180.0f), 1.0f, 0.0f, 1.0f));
        }
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(90.0f));
        ChamsRenderContext ctx = new ChamsRenderContext(matrixStack, MatrixStackDataMode.BOTH);
        for (ModelPart part : model.allParts()) {
            ChamsRenderDispatcher.renderModelPart(ctx, part);
        }
        matrixStack.popPose();
    }
}
