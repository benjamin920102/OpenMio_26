package me.mioclient.ducks;

import net.minecraft.util.BitStorage;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;

public interface DuckPalettedContainerData<T> {
    BitStorage mio$storage();

    Palette<T> mio$palette();

    static BitStorage getPaletteStorage(PalettedContainer<?> palettedContainer) {
        return ((DuckPalettedContainer) palettedContainer).mio$data().mio$storage();
    }

    static <T> Palette<T> getPalette(PalettedContainer<T> palettedContainer) {
        return ((DuckPalettedContainer) palettedContainer).mio$data().mio$palette();
    }
}
