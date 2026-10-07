package me.mioclient.render.hud;

import me.mioclient.api.ClientServices;
import me.mioclient.module.HudModule;

import me.mioclient.util.anim.ValueAnimator;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathUtils;

import java.awt.Color;
import me.mioclient.setting.NumberSetting;
import me.mioclient.api.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ResizableHudElement extends DraggableHudElement {
    public static final Identifier identifier = Identifier.fromNamespaceAndPath("mio", "textures/triangle.png");
    public static ResizableHudElement size;
    public Setting<Float> setting;
    public final ValueAnimator zoomHelper;
    public boolean flag;

    public ResizableHudElement(HudModule moduleList) {
        super(moduleList);
        this.setting = new NumberSetting("Size", Float.valueOf(Float.intBitsToFloat(1065353216)), Float.valueOf(Float.intBitsToFloat(1056964608)), Float.valueOf(Float.intBitsToFloat(1073741824)));
        this.zoomHelper = new ValueAnimator();
    }

    @Override
    public void render(GuiGraphicsExtractor drawContext, PoseStack matrixStack, double d, double d2) {
        super.render(drawContext, matrixStack, d, d2);
        float[] floatArray365 = this.moduleList.getSize();
        matrixStack.pushPose();
        if (this.flag) {
            matrixStack.scale(getScale(), getScale(), Float.intBitsToFloat(1065353216));
            matrixStack.translate(getX() / getScale(), getY() / getScale(), 0.0f);
        } else {
            matrixStack.translate(getX(), getY(), 0.0f);
        }
        this.moduleList.renderContent(drawContext);
        if (!flag && this.flag) {
            boolean isOverResizeHandle = isOverResizeHandle(d, d2);
            if (size == this) {
                this.setting.setValue(Float.valueOf((float) MathUtils.floorToDecimals(Math.min((d - getX()) / floatArray365[0], (d2 - getY()) / floatArray365[1]), 2)));
            }
            if (isOverResizeHandle && isMouseButtonDown(1)) {
                this.setting.setValue(Float.valueOf(Float.intBitsToFloat(1065353216)));
            }
            if (isOverResizeHandle && isMouseButtonDown(0)) {
                size = this;
            } else if (!isMouseButtonDown(0)) {
                size = null;
            }
            if (size == this) {
                this.zoomHelper.animateTo(Float.intBitsToFloat(1063675494), 250L);
            } else if (isOverResizeHandle) {
                this.zoomHelper.animateTo(Float.intBitsToFloat(1053609165), 250L);
            } else {
                this.zoomHelper.animateTo(Float.intBitsToFloat(1045220557), 250L);
            }
            matrixStack.pushPose();
            float intBitsToFloat = Float.intBitsToFloat(1017118720);
            matrixStack.scale(intBitsToFloat, intBitsToFloat, Float.intBitsToFloat(1140457472));
            matrixStack.translate((floatArray365[0] - Float.intBitsToFloat(1084227584)) / intBitsToFloat, (floatArray365[1] - Float.intBitsToFloat(1084227584)) / intBitsToFloat, 0.0f);
            Color color811 = ColorUtils.getThemeColor();
            drawContext.blit(RenderPipelines.GUI_TEXTURED, identifier, 0, 0, 0.0f, 0.0f, 256, 256, 256, 256);
            matrixStack.popPose();
        }
        matrixStack.popPose();
    }

    @Override
    public float[] getSize() {
        float[] floatArray365 = this.moduleList.getSize();
        floatArray365[0] = floatArray365[0] * getScale();
        floatArray365[1] = floatArray365[1] * getScale();
        return floatArray365;
    }

    public void enableScaling(HudModule moduleList) {
        this.flag = true;
        moduleList.register((Setting<?>) this.setting);
    }

    public float getScale() {
        return this.setting.getValue().floatValue();
    }

    public boolean isOverResizeHandle(double d, double d2) {
        if (!this.flag) {
            return false;
        }
        float[] floatArray2950 = getMeasuredSize();
        float f = getX() + Math.max(floatArray2950[0], Float.intBitsToFloat(1082130432));
        float f2 = getY() + Math.max(floatArray2950[1], Float.intBitsToFloat(1082130432));
        return d >= ((double) (f - Float.intBitsToFloat(1084227584))) && d <= ((double) f) && d2 >= ((double) (f2 - Float.intBitsToFloat(1084227584))) && d2 <= ((double) f2);
    }

    public boolean isMouseButtonDown(int i) {
        return GLFW.glfwGetMouseButton(minecraftClient.getWindow().handle(), i) == 1;
    }
}
