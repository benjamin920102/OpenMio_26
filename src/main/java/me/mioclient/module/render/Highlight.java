package me.mioclient.module.render;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shape.BoxRenderer;

import me.mioclient.api.ClientServices;
import me.mioclient.render.shape.BoxRenderer;

import java.awt.Color;
import java.util.Iterator;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.render.DrawBlockOutlineEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import me.mioclient.module.exploit.Reach;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Highlight.class */
public class Highlight extends Module {
    public static final Reach reach = (Reach) ClientServices.moduleInjector.getModule(Reach.class);
    public Setting<Boolean> complex = add(new BooleanSetting("Complex", false));
    public Setting<Boolean> unreachable = add(new BooleanSetting("Unreachable", false));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 3.0f));
    public Setting<Color> color = add(new ColorSetting("Color", new Color(1992137215, true)).setThemeSync(true));
    public Setting<Color> fillColor = add(new ColorSetting("FillColor", new Color(12425727, true)).setThemeSync(true));

    public Highlight() {
        super("Highlight", "Highlights the block you're looking at.", Category.RENDER, "blockhighlight");
        setDrawn(false);
        this.unreachable.addVisibilityPredicate(bool -> {
            return reach.isToggled() && (reach.liquidPlace.getValue().booleanValue() || reach.airPlace.getValue().booleanValue());
        });
    }

    @Listen
    public void onDrawBlockOutline(DrawBlockOutlineEvent drawBlockOutlineEvent) {
        if (isUnreachableHighlightEnabled()) {
            return;
        }
        drawBlockOutlineEvent.cancel();
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        BlockPos blockPos406;
        if (isNotInGame() || (blockPos406 = getBlockPos406()) == null) {
            return;
        }
        BlockState blockState = minecraftClient.world.getBlockState(blockPos406);
        if (is408() || !(blockState.getBlock() instanceof AirBlock)) {
            VoxelShape outlineShape = blockState.getOutlineShape(minecraftClient.world, blockPos406);
            if (outlineShape.isEmpty()) {
                if (is408()) {
                    drawHighlightBox(inner_3.getMatrixStack(), VoxelShapes.fullCube().getBoundingBox(), blockPos406);
                }
            } else {
                if (isUnreachableHighlightEnabled()) {
                    return;
                }
                if (!this.complex.getValue().booleanValue()) {
                    drawHighlightBox(inner_3.getMatrixStack(), outlineShape.getBoundingBox(), blockPos406);
                    return;
                }
                Iterator it = outlineShape.getBoundingBoxes().iterator();
                while (it.hasNext()) {
                    drawHighlightBox(inner_3.getMatrixStack(), (Box) it.next(), blockPos406);
                }
            }
        }
    }

    public BlockPos getBlockPos406() {
        BlockHitResult blockHitResult1232;
        if (reach.isToggled() && reach.ghostHand.getValue().booleanValue() && (blockHitResult1232 = reach.getGhostHandHitResult()) != null && blockHitResult1232.getType() != HitResult.Type.MISS && blockHitResult1232.getBlockPos() != null) {
            return reach.getGhostHandHitResult().getBlockPos();
        }
        if (is408()) {
            BlockHitResult hitResult1229 = (Reach.getAirPlaceHitResult()) instanceof BlockHitResult ? (BlockHitResult) (Reach.getAirPlaceHitResult()) : null;
            if (hitResult1229 instanceof BlockHitResult) {
                return hitResult1229.getBlockPos();
            }
            return null;
        }
        BlockHitResult blockHitResult = (minecraftClient.crosshairTarget) instanceof BlockHitResult ? (BlockHitResult) (minecraftClient.crosshairTarget) : null;
        if (!(blockHitResult instanceof BlockHitResult)) {
            return null;
        }
        BlockHitResult blockHitResult2 = blockHitResult;
        if (blockHitResult2.getType() != HitResult.Type.MISS) {
            return blockHitResult2.getBlockPos();
        }
        return null;
    }

    public void drawHighlightBox(MatrixStack matrixStack, Box box, BlockPos blockPos) {
        BoxRenderer.drawFilledBox(matrixStack, box.offset(blockPos).expand(Double.longBitsToDouble(4566758108544739836L)), this.fillColor.getValue());
        BoxRenderer.drawBoxOutline(matrixStack, box.offset(blockPos).expand(Double.longBitsToDouble(4566758108544739836L)), this.color.getValue(), this.lineWidth.getValue().floatValue());
    }

    public boolean is408() {
        return reach.isToggled() && reach.airPlace.getValue().booleanValue() && ((minecraftClient.player.getMainHandStack().getItem() instanceof BlockItem) || (minecraftClient.player.getOffHandStack().getItem() instanceof BlockItem));
    }

    public boolean isUnreachableHighlightEnabled() {
        return this.unreachable.getValue().booleanValue() && this.unreachable.isVisible();
    }
}
