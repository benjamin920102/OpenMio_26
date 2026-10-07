package me.mioclient.mixin.ducks;

import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/* 26.2 port (verified): ClientTooltipPositioner is the functional interface;
   DefaultTooltipPositioner.INSTANCE is the vanilla impl with a private
   clamp helper `positionTooltip(int,int,Vector2i,int,int)`. Exposed here so
   custom tooltip code can reuse vanilla clamping. */
@Mixin({DefaultTooltipPositioner.class})
public interface DuckClientTooltipPositioner {
    @Invoker("positionTooltip")
    void mio$clampTooltip(int screenWidth, int screenHeight, Vector2i result, int tooltipWidth, int tooltipHeight);
}
