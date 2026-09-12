package me.mioclient.module.render;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/VoidESP.class */
public class VoidESP extends Module {
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 3.0f));
    public Setting<Float> height = add(new NumberSetting<>("Height", 0.1f, -1.0f, 1.0f).setSuffix("m"));
    public Setting<Integer> radius = add(new NumberSetting<>("Radius", 10, 1, 16).setSuffix("m"));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", true));
    public Setting<Float> fadeRadius = add(new NumberSetting<>("FadeRadius", 7.0f, 1.0f, 16.0f, v -> this.fade.getValue().booleanValue()).setSuffix("m"));
    public Setting<Boolean> colors = add(new BooleanSetting("Colors", true).setDependent());
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(822018048, true), v -> this.colors.isShown()).setThemeSync(true));
    public Setting<Color> line = add(new ColorSetting("Line", new Color(-1140916224, true), v -> this.colors.isShown()).setThemeSync(true));
    public final List<BlockPos> list;

    public VoidESP() {
        super("VoidESP", "Highlights void blocks.", Category.RENDER, new String[0]);
        this.list = new CopyOnWriteArrayList();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        this.list.clear();
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.list.removeIf(blockPos -> {
            return BlockPlacer.getBlockAt(blockPos) == Blocks.BEDROCK;
        });
        for (BlockPos blockPos2 : WorldUtils.getBlocksInRadius(minecraftClient.gameRenderer.getCamera().getPos(), this.radius.getValue().intValue(), false)) {
            if (blockPos2.getY() == minecraftClient.world.getBottomY() && BlockPlacer.getBlockAt(blockPos2) != Blocks.BEDROCK && !this.list.contains(blockPos2)) {
                this.list.add(blockPos2);
            }
        }
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return String.valueOf(this.list.size());
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        for (BlockPos blockPos : this.list) {
            if (RotationManager.isInFrustum(new Box(blockPos))) {
                Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
                if (blockPos.isWithinDistance((Position) pos, this.radius.getValue().intValue())) {
                    Color value = this.fill.getValue();
                    Color value2 = this.line.getValue();
                    double distanceTo = pos.distanceTo(blockPos.toCenterPos());
                    if (this.fade.getValue().booleanValue() && distanceTo >= this.fadeRadius.getValue().floatValue()) {
                        float intBitsToFloat = Float.intBitsToFloat(1065353216) - ((float) MathHelper.clamp((distanceTo - this.fadeRadius.getValue().floatValue()) / (this.radius.getValue().intValue() - this.fadeRadius.getValue().floatValue()), 0.0d, Double.longBitsToDouble(4607182418800017408L)));
                        value = ColorUtils.withAlpha(value, (int) (intBitsToFloat * value.getAlpha()));
                        value2 = ColorUtils.withAlpha(value2, (int) (intBitsToFloat * value2.getAlpha()));
                    }
                    BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), new Box(blockPos).withMaxY(blockPos.getY() + this.height.getValue().floatValue()), value);
                    BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), new Box(blockPos).withMaxY(blockPos.getY() + this.height.getValue().floatValue()), value2, this.lineWidth.getValue().floatValue());
                } else {
                    this.list.remove(blockPos);
                }
            }
        }
    }
}
