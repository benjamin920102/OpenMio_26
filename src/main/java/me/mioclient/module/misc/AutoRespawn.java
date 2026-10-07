package me.mioclient.module.misc;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.Category;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screens.DeathScreen;

public class AutoRespawn extends Module {
    public final Stopwatch stopwatch;

    public AutoRespawn() {
        super("AutoRespawn", "Respawns automatically.", Category.MISC, new String[0]);
        this.stopwatch = new Stopwatch();
        setDrawn(false);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if ((minecraftClient.gui.screen() instanceof DeathScreen) && this.stopwatch.hasElapsedMs(150L)) {
            minecraftClient.player.respawn();
            this.stopwatch.reset();
        }
    }
}