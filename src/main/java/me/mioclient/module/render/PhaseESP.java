package me.mioclient.module.render;

import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/PhaseESP.class */
public class PhaseESP extends Module {
    public Setting<Color> safe = add(new ColorSetting("Safe", new Color(540373280, true)).setThemeSync(true));
    public Setting<Color> semiSafe = add(new ColorSetting("SemiSafe", new Color(637525248, true)).setThemeSync(true));
    public Setting<Color> unsafe = add(new ColorSetting("Unsafe", new Color(634257408, true)).setThemeSync(true));
    public Setting<Boolean> outline = add(new BooleanSetting("Outline", false).setVisible());
    public Setting<Float> alpha = add(new NumberSetting<>("Alpha", 1.0f, 0.0f, 1.0f).requiresSettings(this.outline));

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/PhaseESP$PhaseESPMode.class */
    public static enum PhaseESPMode {
        phaseESPMode {
            @Override
            public Color getModeColor(PhaseESP phaseESP) {
                return phaseESP.safe.getValue();
            }
        },
        phaseESPMode2 {
            @Override
            public Color getModeColor(PhaseESP phaseESP) {
                return phaseESP.semiSafe.getValue();
            }
        },
        phaseESPMode3 {
            @Override
            public Color getModeColor(PhaseESP phaseESP) {
                return phaseESP.unsafe.getValue();
            }
        };

        public Color getModeColor(PhaseESP phaseESP) {
            return null;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/PhaseESP$Record.class */
    private static final class Record {
        public final BlockPos blockPos;
        public final PhaseESPMode phaseESPMode;

        public Record(BlockPos blockPos, PhaseESPMode phaseESPMode) {
            this.blockPos = blockPos;
            this.phaseESPMode = phaseESPMode;
        }




        public BlockPos getBlockPos() {
            return this.blockPos;
        }

        public PhaseESPMode getMode() {
            return this.phaseESPMode;
        }
    }

    public PhaseESP() {
        super("PhaseESP", "Highlights safe blocks to phase into.", Category.RENDER, new String[0]);
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        Box expand = minecraftClient.player.getBoundingBox().withMaxY(minecraftClient.player.getBoundingBox().minY).expand(Double.longBitsToDouble(4576918229304087675L), 0.0d, Double.longBitsToDouble(4576918229304087675L));
        if (BlockPlacer.isBlockCollidable(minecraftClient.player.getBlockPos()) || minecraftClient.player.isInSwimmingPose()) {
            return;
        }
        BlockPos.stream(expand).filter(this::isPhaseableBlock).map(blockPos -> {
            return new Record(blockPos, classifyPhaseMode(blockPos));
        }).forEach(record -> {
            VoxelShape outlineShape = minecraftClient.world.getBlockState(record.blockPos).getOutlineShape(minecraftClient.world, record.blockPos);
            if (outlineShape.isEmpty()) {
                outlineShape = VoxelShapes.cuboid(0.0d, 0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L));
            }
            Box offset = outlineShape.getBoundingBox().offset(record.blockPos);
            BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), offset.withMaxY(record.blockPos.getY()), record.phaseESPMode.getModeColor(this));
            if (this.outline.getValue().booleanValue()) {
                BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), offset.withMaxY(record.blockPos.getY()), ColorUtils.withAlphaPercent(record.phaseESPMode.getModeColor(this), this.alpha.getValue().floatValue()), Float.intBitsToFloat(1065353216));
            }
        });
    }

    public PhaseESPMode classifyPhaseMode(BlockPos blockPos) {
        boolean z = minecraftClient.world.getBlockState(blockPos).getBlock().getBlastResistance() >= Float.intBitsToFloat(1142292480);
        if (BlockPlacer.isBlockCollidable(blockPos.down()) && z) {
            if (minecraftClient.world.getBlockState(blockPos.down()).getBlock().getBlastResistance() >= Float.intBitsToFloat(1142292480)) {
                return (isNotBedrock(blockPos) || isNotBedrock(blockPos.down())) ? PhaseESPMode.phaseESPMode2 : PhaseESPMode.phaseESPMode;
            }
        }
        return PhaseESPMode.phaseESPMode3;
    }

    public boolean isNotBedrock(BlockPos blockPos) {
        return BlockPlacer.getBlockAt(blockPos) != Blocks.BEDROCK;
    }

    public boolean isPhaseableBlock(BlockPos blockPos) {
        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        return blockState.getBlock().getBlastResistance() >= Float.intBitsToFloat(1142292480) && BlockPlacer.isBlockCollidable(blockPos) && !blockState.isReplaceable();
    }
}
