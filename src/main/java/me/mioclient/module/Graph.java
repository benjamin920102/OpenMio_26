package me.mioclient.module;

import me.mioclient.api.ClientServices;

import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.ColorUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.mioclient.setting.NumberSetting;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.render.hud.ResizableHudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Graph.class */
public class Graph extends me.mioclient.module.HudModule {
    public Setting<Float> setting;
    public Setting<Integer> setting2;
    public Setting<Integer> setting3;
    public final List<Double> list;

    public Graph() {
        super("Graph", new String[0]);
        this.setting = add(new NumberSetting("Ceil", Float.valueOf(Float.intBitsToFloat(1104150528)), Float.valueOf(Float.intBitsToFloat(1092616192)), Float.valueOf(Float.intBitsToFloat(1120403456))));
        this.setting2 = add(new NumberSetting("Height", 30, 10, 40));
        this.setting3 = add(new NumberSetting("Width", 100, 50, 150));
        this.list = new ArrayList();
        setElement(new ResizableHudElement(this));
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.list.clear();
        for (int i = 0; i < 150; i++) {
            this.list.add(Double.valueOf(0.0d));
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.list.add(Double.valueOf(ClientServices.feetPlaceHelper.getSmoothedSpeed()));
        while (this.list.size() > this.setting3.getValue().intValue()) {
            this.list.remove(0);
        }
    }

    @Override // me.mioclient.module.HudModule
    public void renderContent(DrawContext drawContext) {
        Tessellator tessellator = Tessellator.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferBuilder begin = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        int i = 0;
        Iterator<Double> it = this.list.iterator();
        while (it.hasNext()) {
            float intBitsToFloat = Float.intBitsToFloat(1065353216) + ((float) ((this.setting2.getValue().intValue() - 2) * (Double.longBitsToDouble(4607182418800017408L) - (Math.min(it.next().doubleValue(), this.setting.getValue().floatValue()) / this.setting.getValue().floatValue()))));
            begin.vertex(drawContext.getMatrices().peek().getPositionMatrix(), i, intBitsToFloat, 0.0f).color(ColorUtils.withAlpha(getHudColor(intBitsToFloat + this.moduleListSearchHelper4.getY()), get2579(i)).hashCode());
            i++;
        }
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderSystem.disableBlend();
    }

    public int get2579(int i) {
        if (i <= 10) {
            return (int) MathHelper.clamp((i / Float.intBitsToFloat(1092616192)) * Float.intBitsToFloat(1132396544), 0.0f, Float.intBitsToFloat(1132396544));
        }
        if (i >= this.setting3.getValue().intValue() - 10) {
            return (int) MathHelper.clamp((Float.intBitsToFloat(1065353216) - (((i - this.setting3.getValue().intValue()) + 10) / Float.intBitsToFloat(1092616192))) * Float.intBitsToFloat(1132396544), 0.0f, Float.intBitsToFloat(1132396544));
        }
        return 255;
    }

    @Override // me.mioclient.module.HudModule
    public float[] getSize() {
        return new float[]{this.setting3.getValue().intValue(), this.setting2.getValue().intValue()};
    }
}
