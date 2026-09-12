package me.mioclient.module;

import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.util.anim.ValueAnimator;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.anim.ValueAnimator;

import java.awt.Color;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.api.Setting;
import me.mioclient.render.hud.ResizableHudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Inventory.class */
public class Inventory extends me.mioclient.module.HudModule {
    public Setting<Color> setting;
    public Setting<Color> setting2;
    public Setting<Boolean> setting3;
    public final ValueAnimator zoomHelper;

    public Inventory() {
        super("Inventory", new String[0]);
        this.setting = add(new ColorSetting("Background", new Color(10, 10, 10, 50)));
        this.setting2 = add(new ColorSetting("Outline", new Color(10, 10, 10, 100)));
        this.setting3 = add(new BooleanSetting("HideEmpty", true));
        this.zoomHelper = new ValueAnimator();
        ResizableHudElement size = new ResizableHudElement(this);
        size.enableScaling(this);
        setElement(size);
    }

    @Override // me.mioclient.module.HudModule
    public void renderContent(DrawContext drawContext) {
        this.zoomHelper.toggle(!isEmpty(), 200L);
        float f = this.zoomHelper.getValue();
        if (!this.setting3.getValue().booleanValue()) {
            f = Float.intBitsToFloat(1065353216);
        }
        Color color817 = ColorUtils.withAlphaPercent(this.setting.getValue(), (this.setting.getValue().getAlpha() / Float.intBitsToFloat(1132396544)) * f);
        Color color8172 = ColorUtils.withAlphaPercent(this.setting2.getValue(), (this.setting2.getValue().getAlpha() / Float.intBitsToFloat(1132396544)) * f);
        RenderUtil.INSTANCE.drawFilledRectColor(drawContext.getMatrices(), 0.0f, 0.0f, Float.intBitsToFloat(1126301696), Float.intBitsToFloat(1113063424), color817);
        RenderUtil.INSTANCE.drawRectOutline(drawContext.getMatrices(), Float.intBitsToFloat(-1082130432), Float.intBitsToFloat(-1082130432), Float.intBitsToFloat(1126301696), Float.intBitsToFloat(1113063424), color8172);
        DefaultedList defaultedList = minecraftClient.player.getInventory().main;
        for (int i = 0; i < defaultedList.size() - 9; i++) {
            int i2 = (i % 9) * 18;
            int i3 = (i / 9) * 18;
            ItemStack itemStack = (ItemStack) defaultedList.get(i + 9);
            drawContext.drawItem(itemStack, i2, i3);
            drawContext.drawItemInSlot(minecraftClient.textRenderer, itemStack, i2, i3);
        }
    }

    public boolean isEmpty() {
        DefaultedList defaultedList = minecraftClient.player.getInventory().main;
        for (int i = 0; i < defaultedList.size() - 9; i++) {
            if (!((ItemStack) defaultedList.get(i + 9)).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override // me.mioclient.module.HudModule
    public float[] getSize() {
        return new float[]{Float.intBitsToFloat(1126301696), Float.intBitsToFloat(1113063424)};
    }
}
