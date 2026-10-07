package me.mioclient.mixin.ducks;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/* 26.2 port (verified): DrawContext is now GuiGraphicsExtractor. Public API
   already covers setTooltipForNextFrame/tooltip; this duck exposes the mouse
   position fields plus the private deferred-tooltip path for custom positioners. */
@Mixin({GuiGraphicsExtractor.class})
public interface DuckGuiGraphicsExtractor {
    @Accessor("mouseX")
    int mio$getMouseX();

    @Accessor("mouseY")
    int mio$getMouseY();

    @Invoker("setTooltipForNextFrameInternal")
    void mio$setTooltipInternal(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner, Identifier style, boolean replaceExisting);
}
