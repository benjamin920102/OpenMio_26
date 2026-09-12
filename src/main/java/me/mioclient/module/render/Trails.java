package me.mioclient.module.render;

import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL32C;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Trails.class */
public class Trails extends Module {
    public final HashMap<Integer, List<Inner>> hashMap;
    public Setting<Boolean> render = add(new BooleanSetting("Render", true));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 0.0f, 0.0f, 5.0f).setSuffix("s"));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", false).setDependent());
    public Setting<Boolean> self = add(new BooleanSetting("Self", true, v -> this.targets.isShown()));
    public Setting<Boolean> players = add(new BooleanSetting("Players", false, v -> this.targets.isShown()));
    public Setting<Boolean> pearls = add(new BooleanSetting("Pearls", false, v -> this.targets.isShown()));
    public Setting<Boolean> arrows = add(new BooleanSetting("Arrows", false, v -> this.targets.isShown()));
    public Setting<Boolean> exp = add(new BooleanSetting("Exp", false, v -> this.targets.isShown()));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 5.0f));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", false).setVisible());
    public Setting<Float> fadeDelay = add(new NumberSetting<>("FadeDelay", 1.0f, 0.0f, 10.0f, v -> this.fade.isShown()).setSuffix("s"));
    public Setting<Float> fadeDuration = add(new NumberSetting<>("FadeDuration", 0.5f, 0.0f, 10.0f, v -> this.fade.isShown()).setSuffix("s"));
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-1312974337, true)).setThemeSync(true));
    public Setting<Boolean> rainbow = add(new BooleanSetting("Rainbow", false).setVisible());
    public Setting<Double> threshold = add(new NumberSetting<>("Threshold", 1.0, 0.1, 3.0).requiresSettings(this.rainbow));
    public final Stopwatch stopwatch;

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Trails$Inner.class */
    public static class Inner {
        public final long num = System.currentTimeMillis();
        public final Vec3d vec3d;

        public Inner(Vec3d vec3d) {
            this.vec3d = vec3d;
        }

        public long getCreatedAt() {
            return this.num;
        }

        public Vec3d getPosition() {
            return this.vec3d;
        }
    }

    public Trails() {
        super("Trails", "Draws trails behind certain entities.", Category.RENDER, new String[0]);
        this.hashMap = new HashMap<>();
        this.stopwatch = new Stopwatch();
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.hashMap.clear();
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (isNotInGame() || !this.render.getValue().booleanValue()) {
            return;
        }
        for (Map.Entry<Integer, List<Inner>> entry : this.hashMap.entrySet()) {
            if (entry.getValue().size() > 2) {
                do3148(inner_3.getMatrixStack(), entry.getValue());
            }
        }
    }

    @Listen(get219= RenderConstants.num4)
    public void onRenderWorldPre(MatrixStackEvent.Inner inner) {
        if (isNotInGame()) {
            return;
        }
        if (this.delay.getValue().floatValue() != 0.0f) {
            if (!this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS)) {
                return;
            }
        }
        this.stopwatch.reset();
        synchronized (this.hashMap) {
            if (minecraftClient.player.age % 4 == 0) {
                int i = 0;
                Iterator<Integer> it = this.hashMap.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Integer next = it.next();
                    List<Inner> list = this.hashMap.get(next);
                    list.removeIf(inner2 -> {
                        return this.fade.getValue().booleanValue() && System.currentTimeMillis() - inner2.num > getFadeDelayMs() + getFadeDurationMs();
                    });
                    if (list.size() == 0) {
                        i = next.intValue();
                        break;
                    }
                }
                if (i != 0) {
                    this.hashMap.remove(Integer.valueOf(i));
                }
            }
            for (Entity entity : minecraftClient.world.getEntities()) {
                if (shouldRenderEntity(entity)) {
                    this.hashMap.compute(Integer.valueOf(entity.getId()), (num, list2) -> {
                        Inner inner3 = new Inner(entity.getLerpedPos(RenderUtil.getPartialTick()));
                        if (list2 == null) {
                            return new ArrayList(Collections.singleton(inner3));
                        }
                        list2.add(inner3);
                        return list2;
                    });
                }
            }
        }
    }

    public void do3148(MatrixStack matrixStack, List<Inner> list) {
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        RenderUtil.INSTANCE.setupRenderState();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        GL32C.glLineWidth(this.lineWidth.getValue().floatValue());
        RenderSystem.lineWidth(this.lineWidth.getValue().floatValue());
        float[] RGBtoHSB = Color.RGBtoHSB(this.color.getValue().getRed(), this.color.getValue().getGreen(), this.color.getValue().getBlue(), (float[]) null);
        for (int i = 0; i < list.size() - 2; i++) {
            RenderUtil.INSTANCE.addLineVertex(matrixStack, begin, list.get(i).getPosition(), list.get(i + 1).getPosition(), calculateTrailColor(RGBtoHSB, list.get(i)).hashCode(), calculateTrailColor(RGBtoHSB, list.get(i + 1)).hashCode());
        }
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderUtil.INSTANCE.restoreRenderState();
        GL32C.glLineWidth(Float.intBitsToFloat(1065353216));
    }

    public Color calculateTrailColor(float[] fArr, Inner inner) {
        Color hSBColor = this.rainbow.getValue().booleanValue() ? Color.getHSBColor((float) ((Math.ceil(inner.getCreatedAt() / (Double.longBitsToDouble(4626322717216342016L) * this.threshold.getValue().doubleValue())) % MathConstants.num3) / MathConstants.num3), fArr[1], fArr[2]) : this.color.getValue();
        return (!this.fade.getValue().booleanValue() || System.currentTimeMillis() - inner.getCreatedAt() < getFadeDelayMs()) ? ColorUtils.withAlpha(hSBColor, this.color.getValue().getAlpha()) : ColorUtils.withAlpha(hSBColor, (int) MathHelper.clamp((Float.intBitsToFloat(1065353216) - (((float) ((System.currentTimeMillis() - inner.getCreatedAt()) - getFadeDelayMs())) / ((float) getFadeDurationMs()))) * this.color.getValue().getAlpha(), 0.0f, Float.intBitsToFloat(1132396544)));
    }

    public boolean shouldRenderEntity(Entity entity) {
        return (entity == minecraftClient.player && this.self.getValue().booleanValue()) || ((entity instanceof PlayerEntity) && this.players.getValue().booleanValue() && entity != minecraftClient.player) || (((entity instanceof EnderPearlEntity) && this.pearls.getValue().booleanValue()) || (((entity instanceof ExperienceBottleEntity) && this.exp.getValue().booleanValue()) || ((entity instanceof ArrowEntity) && this.arrows.getValue().booleanValue())));
    }

    public long getFadeDelayMs() {
        return (long) (this.fadeDelay.getValue().floatValue() * Float.intBitsToFloat(1148846080));
    }

    public long getFadeDurationMs() {
        return (long) (this.fadeDuration.getValue().floatValue() * Float.intBitsToFloat(1148846080));
    }
}
