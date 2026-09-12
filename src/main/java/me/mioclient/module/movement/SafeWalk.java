package me.mioclient.module.movement;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.player.ClipAtLedgeEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/SafeWalk.class */
public class SafeWalk extends Module {
    public Setting<Boolean> sneak = add(new BooleanSetting("Sneak", false).setVisible());
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 0, 0, 1000).setSuffix("ms").requiresSettings(this.sneak));
    public final Stopwatch stopwatch;

    public SafeWalk() {
        super("SafeWalk", "Helps not to fall from blocks if you're bad.", Category.MOVEMENT, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (this.sneak.getValue().booleanValue()) {
            minecraftClient.options.sneakKey.setPressed(false);
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.sneak.getValue().booleanValue()) {
            BlockPos down = BlockPos.ofFloored(minecraftClient.player.getPos()).down();
            BlockState blockState = minecraftClient.world.getBlockState(down);
            if (blockState.isOf(Blocks.AIR) || blockState.getCollisionShape(minecraftClient.world, down).isEmpty()) {
                this.stopwatch.reset();
                minecraftClient.options.sneakKey.setPressed(true);
            } else {
                minecraftClient.options.sneakKey.setPressed(!this.stopwatch.hasElapsedMs((long) this.delay.getValue().intValue()));
            }
        }
    }

    @Listen
    public void onClipAtLedge(ClipAtLedgeEvent clipAtLedgeEvent) {
        if (minecraftClient.player.isSwimming() || minecraftClient.player.isInLava() || minecraftClient.player.isTouchingWater() || this.sneak.getValue().booleanValue()) {
            return;
        }
        clipAtLedgeEvent.cancel();
    }
}
