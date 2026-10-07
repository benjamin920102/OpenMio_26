package me.mioclient.mixin.ducks;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): Yarn AbstractContainerScreen -> MojMap AbstractContainerScreen;
   focusedSlot is hoveredSlot; x/y are leftPos/topPos; backgroundWidth/Height
   are imageWidth/imageHeight. */
@Mixin({AbstractContainerScreen.class})
public interface DuckHandledScreen {
    @Accessor("hoveredSlot")
    Slot mio$getFocusedSlot();

    @Accessor("leftPos")
    int getX();

    @Accessor("topPos")
    int getY();

    @Accessor("imageWidth")
    int getBgWidth();

    @Accessor("imageHeight")
    int getBgHeight();
}
