package me.mioclient.module.render;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.anim.AnimationProgress;

import me.mioclient.render.shape.Shape2DRenderer;

import java.awt.Color;
import me.mioclient.event.events.render.HudRenderEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.anim.AnimationProgress;
import me.mioclient.module.Module;
import net.minecraft.client.util.math.MatrixStack;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Crosshair.class */
public class Crosshair extends Module {
    public Setting<Boolean> thirdPerson = add(new BooleanSetting("ThirdPerson", true));
    public Setting<Boolean> dot = add(new BooleanSetting("Dot", true));
    public Setting<Boolean> shadow = add(new BooleanSetting("Shadow", false).setVisible());
    public Setting<Integer> distance = add(new NumberSetting<>("Distance", 1, 0, 3, v -> this.shadow.isShown()));
    public Setting<Boolean> dynamic = add(new BooleanSetting("Dynamic", true).setVisible());
    public Setting<Boolean> smooth = add(new BooleanSetting("Smooth", true, v -> this.dynamic.isShown()));
    public Setting<Float> amplitude = add(new NumberSetting<>("Amplitude", 2.0f, 0.1f, 8.0f, v -> this.dynamic.isShown()));
    public Setting<Integer> gap = add(new NumberSetting<>("Gap", 2, 0, 16));
    public Setting<Integer> length = add(new NumberSetting<>("Length", 2, 0, 16));
    public Setting<Integer> width = add(new NumberSetting<>("Width", 1, 1, 5));
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-2828, true)).setThemeSync(true));
    public final AnimationProgress progress;

    public Crosshair() {
        super("Crosshair", "Allows you to customize your crosshair.", Category.RENDER, new String[0]);
        this.progress = new AnimationProgress(Float.intBitsToFloat(1073741824), true);
        setDrawn(false);
    }

    @Listen
    public void onHudRender(HudRenderEvent matrixStackEvent_2) {
        if (isNotInGame()) {
            return;
        }
        if (!minecraftClient.gameRenderer.getCamera().isThirdPerson() || this.thirdPerson.getValue().booleanValue()) {
            float intValue = this.gap.getValue().intValue();
            if (this.dynamic.getValue().booleanValue()) {
                if (this.smooth.getValue().booleanValue()) {
                    this.progress.animateToggle((minecraftClient.player.input.movementForward == 0.0f && minecraftClient.player.input.movementSideways == 0.0f && !minecraftClient.player.input.jumping) ? false : true);
                } else {
                    this.progress.setToggledImmediate((minecraftClient.player.input.movementForward == 0.0f && minecraftClient.player.input.movementSideways == 0.0f && !minecraftClient.player.input.jumping) ? false : true);
                }
                intValue += this.amplitude.getValue().floatValue() * this.progress.getValue();
            }
            float scaleFactor = (float) minecraftClient.getWindow().getScaleFactor();
            matrixStackEvent_2.getMatrixStack().push();
            matrixStackEvent_2.getMatrixStack().scale(Float.intBitsToFloat(1065353216) / scaleFactor, Float.intBitsToFloat(1065353216) / scaleFactor, Float.intBitsToFloat(1065353216) / scaleFactor);
            if (this.shadow.getValue().booleanValue()) {
                MatrixStack matrixStack472 = matrixStackEvent_2.getMatrixStack();
                matrixStack472.push();
                matrixStack472.translate(this.distance.getValue().intValue(), this.distance.getValue().intValue(), 0.0f);
                drawCrosshairLines(matrixStackEvent_2.getMatrixStack(), this.length.getValue().intValue(), this.width.getValue().intValue() / Float.intBitsToFloat(1073741824), intValue, Color.BLACK);
                matrixStack472.pop();
            }
            drawCrosshairLines(matrixStackEvent_2.getMatrixStack(), this.length.getValue().intValue(), this.width.getValue().intValue() / Float.intBitsToFloat(1073741824), intValue, this.color.getValue());
            matrixStackEvent_2.getMatrixStack().pop();
        }
    }

    public void drawCrosshairLines(MatrixStack matrixStack, float f, float f2, float f3, Color color) {
        int width = minecraftClient.getWindow().getWidth() / 2;
        int height = minecraftClient.getWindow().getHeight() / 2;
        if (Math.ceil(f2) - f2 != 0.0d) {
            f3 += Float.intBitsToFloat(1056964608);
        } else {
            width--;
            height++;
        }
        if (this.dot.getValue().booleanValue()) {
            Shape2DRenderer.drawRect(matrixStack, width - f2, height - f2, width + f2, height + f2, color);
        }
        if (f == 0.0f) {
            return;
        }
        Shape2DRenderer.drawRect(matrixStack, width - f2, (height - f3) - f, width + f2, height - f3, color);
        Shape2DRenderer.drawRect(matrixStack, width + f3, height - f2, width + f3 + f, height + f2, color);
        Shape2DRenderer.drawRect(matrixStack, width - f2, height + f3, width + f2, height + f3 + f, color);
        Shape2DRenderer.drawRect(matrixStack, (width - f3) - f, height - f2, width - f3, height + f2, color);
    }
}
