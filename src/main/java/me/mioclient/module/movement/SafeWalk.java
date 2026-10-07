package me.mioclient.module.movement;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.player.ClipAtLedgeEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

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
            minecraftClient.options.keyShift.setDown(false);
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.sneak.getValue().booleanValue()) {
            BlockPos down = BlockPos.containing(minecraftClient.player.position()).below();
            BlockState blockState = minecraftClient.level.getBlockState(down);
            if (blockState.is(Blocks.AIR) || blockState.getCollisionShape(minecraftClient.level, down).isEmpty()) {
                this.stopwatch.reset();
                minecraftClient.options.keyShift.setDown(true);
            } else {
                minecraftClient.options.keyShift.setDown(!this.stopwatch.hasElapsedMs((long) this.delay.getValue().intValue()));
            }
        }
    }

    @Listen
    public void onClipAtLedge(ClipAtLedgeEvent clipAtLedgeEvent) {
        if (minecraftClient.player.isSwimming() || minecraftClient.player.isInLava() || minecraftClient.player.isInWater() || this.sneak.getValue().booleanValue()) {
            return;
        }
        clipAtLedgeEvent.cancel();
    }
}