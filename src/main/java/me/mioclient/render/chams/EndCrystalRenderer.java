package me.mioclient.render.chams;

import me.mioclient.api.ClientServices;

import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.render.Animations;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import com.mojang.math.Axis;
import org.joml.Quaternionf;

public class EndCrystalRenderer implements EntityRenderCallback<EndCrystal> {
    /* Resolved lazily — eager static init ran before the module registry was ready. */
    private static AutoCrystal ac;

    public static AutoCrystal getAutoCrystal() {
        if (ac == null && ClientServices.moduleInjector != null) {
            try {
                ac = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
            } catch (Exception e) {
            }
        }
        return ac;
    }

    private static Animations animations;

    public static Animations getAnimations() {
        if (animations == null && ClientServices.moduleInjector != null) {
            try {
                animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class);
            } catch (Exception e) {
            }
        }
        return animations;
    }
    public static final float val = (float) Math.sin(0.7853981633974483d);
    public final Quaternionf quaternionf = new Quaternionf();
    public EndCrystal last;

    @Override
    public void do721(EndCrystal endCrystalEntity, float f, PoseStack matrixStack) {
        this.last = endCrystalEntity;
        matrixStack.pushPose();
        matrixStack.scale(2.0f * getScale(), 2.0f * getScale(), 2.0f * getScale());
        matrixStack.translate(0.0d, -0.5d, 0.0d);
        matrixStack.mulPose(Axis.YP.rotationDegrees((endCrystalEntity.time + f) * 3.0f * getRotationSpeed()));
        me.mioclient.render.shape.BoxRenderer.drawBoxOutline(matrixStack, endCrystalEntity.getBoundingBox(), ChamsRenderDispatcher.color, (float) ChamsRenderDispatcher.val);
        matrixStack.popPose();
    }

    public float getScale() {
        Animations animations_lcl = getAnimations();
        if (animations_lcl != null && animations_lcl.isToggled() && animations_lcl.crystals.getValue().booleanValue()) {
            return animations_lcl.crystalScale.getValue().floatValue();
        }
        return 1.0f;
    }

    public float getRotationSpeed() {
        Animations animations_lcl = getAnimations();
        if (animations_lcl != null && animations_lcl.isToggled() && animations_lcl.crystals.getValue().booleanValue()) {
            return animations_lcl.rotationSpeed.getValue().floatValue();
        }
        return 1.0f;
    }
}
