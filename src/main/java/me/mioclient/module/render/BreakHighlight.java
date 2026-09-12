package me.mioclient.module.render;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.breakhighlight.BreakProgressTracker;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathUtils;

import java.awt.Color;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/BreakHighlight.class */
public class BreakHighlight extends Module {
    public Setting<Double> range = add(new NumberSetting<>("Range", 10.0, 1.0, 25.0).setSuffix("m"));
    public Setting<Boolean> friends = add(new BooleanSetting("Friends", true));
    public Setting<Boolean> enemyColor = add(new BooleanSetting("EnemyColor", true));
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(279542783, true)).setThemeSync(true));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(12425727, true)).setThemeSync(true));

    public BreakHighlight() {
        super("BreakHighlight", "Highlights the blocks that are being broken.", Category.RENDER, new String[0]);
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        for (BreakProgressTracker entityIdHelper : ClientServices.breakHighlightHelper.getList1517()) {
            if (MathUtils.getCameraDistanceToBlock(entityIdHelper.getBlockPos()) <= this.range.getValue().doubleValue() && RotationManager.isInFrustum(new Box(entityIdHelper.getBlockPos())) && WorldUtils.isBreakable(entityIdHelper.getBlockPos())) {
                Color value = this.fill.getValue();
                Color value2 = this.outline.getValue();
                VoxelShape outlineShape = minecraftClient.world.getBlockState(entityIdHelper.getBlockPos()).getOutlineShape(minecraftClient.world, entityIdHelper.getBlockPos());
                float f = entityIdHelper.get2814(inner_3.getTickDelta());
                PlayerEntity entity181 = (entityIdHelper.getEntity()) instanceof PlayerEntity ? (PlayerEntity) (entityIdHelper.getEntity()) : null;
                Box offset = scaleBoxByProgress(outlineShape.isEmpty() ? new Box(0.0d, 0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L)) : outlineShape.getBoundingBox(), f).offset(entityIdHelper.getBlockPos());
                if (entity181 instanceof PlayerEntity) {
                    PlayerEntity playerEntity = entity181;
                    Color color530 = ClientServices.socialManager.getSocialColor(playerEntity.getGameProfile().getName(), (Color) null);
                    if (this.friends.getValue().booleanValue() || !ClientServices.socialManager.isFriendEntity(playerEntity)) {
                        if (color530 != null) {
                            if (!ClientServices.socialManager.isEnemy(playerEntity.getGameProfile().getName()) || this.enemyColor.getValue().booleanValue()) {
                                value = ColorUtils.withAlpha(color530, this.fill.getValue().getAlpha());
                                value2 = ColorUtils.withAlpha(color530, this.outline.getValue().getAlpha());
                            }
                        }
                    }
                }
                BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), scaleBoxByProgress(offset, f), value);
                BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), scaleBoxByProgress(offset, f), value2, Float.intBitsToFloat(1065353216));
            }
        }
    }

    public Box scaleBoxByProgress(Box box, float f) {
        float clamp = MathHelper.clamp(f, 0.0f, Float.intBitsToFloat(1065353216));
        double abs = Math.abs(box.getLengthX());
        double abs2 = Math.abs(box.getLengthY());
        double abs3 = Math.abs(box.getLengthZ());
        Vec3d center = box.getCenter();
        return new Box(center.subtract((clamp * abs) / Double.longBitsToDouble(4611686018427387904L), (clamp * abs2) / Double.longBitsToDouble(4611686018427387904L), (clamp * abs3) / Double.longBitsToDouble(4611686018427387904L)), center.add((clamp * abs) / Double.longBitsToDouble(4611686018427387904L), (clamp * abs2) / Double.longBitsToDouble(4611686018427387904L), (clamp * abs3) / Double.longBitsToDouble(4611686018427387904L)));
    }
}
