package me.mioclient.render.chams;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.render.MatrixStackDataMode;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.MathConstants;

import me.mioclient.ducks.DuckEndCrystalEntity;

import me.mioclient.mixin.ducks.DuckEndCrystalEntityRenderer;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.render.Animations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import me.mioclient.mode.render.MatrixStackDataMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EndCrystalRenderer.class */
public class EndCrystalRenderer implements EntityRenderCallback<EndCrystalEntity> {
    public static AutoCrystal ac = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public static Animations animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class);
    public static final float val = (float) Math.sin(0.7853981633974483d);
    public final Quaternionf quaternionf = new Quaternionf();
    public EndCrystalEntity last;

    @Override // me.mioclient.render.chams.EntityRenderCallback
    /* renamed from: do1025, reason: merged with bridge method [inline-methods] */
    public void do721(EndCrystalEntity endCrystalEntity, float f, MatrixStack matrixStack) {
        this.last = endCrystalEntity;
        ChamsRenderContext matrixStackData = new ChamsRenderContext(matrixStack, MatrixStackDataMode.BOTH);
        DuckEndCrystalEntityRenderer renderer = (DuckEndCrystalEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(endCrystalEntity);
        matrixStack.push();
        float yOffset = EndCrystalEntityRenderer.getYOffset(endCrystalEntity, f);
        float f2 = (endCrystalEntity.endCrystalAge + f) * 3.0f;
        matrixStack.scale(2.0f * getScale(), 2.0f * getScale(), 2.0f * getScale());
        matrixStack.translate(0.0d, -MathConstants.val2, 0.0d);
        if (endCrystalEntity.shouldShowBottom() && !animations.isBottomPartHidden()) {
            ChamsRenderDispatcher.renderModelPart(matrixStackData, renderer.mio$getBottom());
        }
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f2 * getRotationSpeed()));
        matrixStack.translate(0.0f, 1.5f + (yOffset / 2.0f), 0.0f);
        matrixStack.multiply(this.quaternionf.setAngleAxis(1.0471976f, val, 0.0f, val));
        if (!animations.isOuterPartHidden()) {
            ChamsRenderDispatcher.renderModelPart(matrixStackData, renderer.mio$getFrame());
        }
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(this.quaternionf.setAngleAxis(1.0471976f, val, 0.0f, val));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f2 * getRotationSpeed()));
        if (!animations.isInnerPartHidden()) {
            ChamsRenderDispatcher.renderModelPart(matrixStackData, renderer.mio$getFrame());
        }
        matrixStack.pop();
    }

    public float getScale() {
        if (animations.isToggled() && animations.crystals.getValue().booleanValue()) {
            return animations.crystalScale.getValue().floatValue();
        }
        return 1.0f;
    }

    public float getRotationSpeed() {
        if (this.last.age < 10 && ac.isToggled() && ac.inhibit.getValue().booleanValue() && ((DuckEndCrystalEntity)(Object) this.last).isMioAttacked()) {
            return 1.5f;
        }
        if (animations.isToggled() && animations.crystals.getValue().booleanValue()) {
            return animations.rotationSpeed.getValue().floatValue();
        }
        return 1.0f;
    }
}
