package me.mioclient.module.render;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.render.shape.BoxRenderer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.RenderUtil;

import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Borders.class */
public class Borders extends Module {
    public Setting<Color> chunk = add(new ColorSetting("Chunk", new Color(-65536, true)));
    public Setting<Color> map = add(new ColorSetting("Map", new Color(-65281, true)));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 3.0f));
    public Setting<Integer> mapSize = add(new NumberSetting<>("MapSize", 0, 0, 4));
    public Setting<Integer> level = add(new NumberSetting<>("Level", -1, -1, 255).setHoleSnapConfig("~", HoleSnapMode.MIN));

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Borders$Record.class */
    public static final class Record {
        public final int num;
        public final int num2;
        public final int num3;

        public Record(int i, int i2, int i3) {
            this.num = i;
            this.num2 = i2;
            this.num3 = i3;
        }

        public void drawBorderAtLevel(MatrixStack matrixStack, Color color, int i, float f) {
            double d = i;
            if (i == -1) {
                d = ClientAccessor.minecraftClient.player.getLerpedPos(RenderUtil.getPartialTick()).y;
            }
            BoxRenderer.drawBoxOutline(matrixStack, getBox791(d), color, f);
        }

        public Box getBox791(double d) {
            int i = this.num3 / 2;
            return new Box(this.num - i, d, this.num2 - i, this.num + i, d, this.num2 + i);
        }




        public int get476() {
            return this.num;
        }

        public int get477() {
            return this.num2;
        }

        public int getSize() {
            return this.num3;
        }
    }

    public Borders() {
        super("Borders", "Shows region borders on your screen.", Category.RENDER, new String[0]);
        this.level.setHoleSnapConfig("~", HoleSnapMode.MIN);
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (this.chunk.getValue().getAlpha() != 0) {
            Record record1948 = getRecord1948();
            MatrixStack matrixStack472 = inner_3.getMatrixStack();
            record1948.drawBorderAtLevel(matrixStack472, this.chunk.getValue(), this.level.getValue().intValue(), this.lineWidth.getValue().floatValue());
        }
        if (this.map.getValue().getAlpha() != 0) {
            Record record1949 = getRecord1949();
            MatrixStack matrixStack4722 = inner_3.getMatrixStack();
            record1949.drawBorderAtLevel(matrixStack4722, this.map.getValue(), this.level.getValue().intValue(), this.lineWidth.getValue().floatValue());
        }
    }

    public Record getRecord1948() {
        ChunkPos chunkPos = minecraftClient.player.getChunkPos();
        return new Record(chunkPos.getCenterX(), chunkPos.getCenterZ(), 16);
    }

    public Record getRecord1949() {
        int intValue = 128 * (1 << this.mapSize.getValue().intValue());
        return new Record(((MathHelper.floor((minecraftClient.player.getBlockX() + Double.longBitsToDouble(4634204016564240384L)) / intValue) * intValue) + (intValue / 2)) - 64, ((MathHelper.floor((minecraftClient.player.getBlockZ() + Double.longBitsToDouble(4634204016564240384L)) / intValue) * intValue) + (intValue / 2)) - 64, intValue);
    }
}
