package me.mioclient.module.render;

import java.util.Set;
import me.mioclient.ducks.DuckSimpleOption;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Xray.class */
public class Xray extends Module {
    public static Xray xray;
    public Setting<Boolean> bypass = add(new BooleanSetting("Bypass", false));
    public Setting<Set<Block>> whitelist = add(new EntityListObjectSetting<>("Whitelist", Registries.BLOCK));
    public double val;

    public Xray() {
        super("Xray", "Wallhack on ores.", Category.RENDER, new String[0]);
        xray = this;
        this.whitelist.setChangeCallback(this::reloadWorldRenderer);
        this.bypass.setChangeCallback(this::reloadWorldRenderer);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.val = ((Double) minecraftClient.options.getGamma().getValue()).doubleValue();
        ((DuckSimpleOption<Double>)(Object) minecraftClient.options.getGamma()).forceSetValue(Double.valueOf(Double.longBitsToDouble(4652007308841189376L)));
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        ((DuckSimpleOption<Double>)(Object) minecraftClient.options.getGamma()).forceSetValue(Double.valueOf(this.val));
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        reloadWorldRenderer();
    }

    public boolean shouldRenderBlock(BlockPos blockPos, Block block) {
        return (block == null || blockPos == null || isNotInGame() || !this.whitelist.getValue().contains(block) || (this.bypass.getValue().booleanValue() && !isBlockExposed(blockPos))) ? false : true;
    }

    public boolean isBlockExposed(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!minecraftClient.world.getBlockState(blockPos.offset(direction)).isOpaque()) {
                return true;
            }
        }
        return false;
    }

    public void reloadWorldRenderer() {
        if (minecraftClient.world != null) {
            minecraftClient.worldRenderer.reload();
        }
    }

    public static Xray getInstance() {
        return xray;
    }
}
