package me.mioclient.ducks;

import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/DuckPalettedContainerData.class */
public interface DuckPalettedContainerData<T> {
    PaletteStorage mio$storage();

    Palette<T> mio$palette();

    static PaletteStorage getPaletteStorage(PalettedContainer<?> palettedContainer) {
        return ((DuckPalettedContainer) palettedContainer).mio$data().mio$storage();
    }

    static <T> Palette<T> getPalette(PalettedContainer<T> palettedContainer) {
        return ((DuckPalettedContainer) palettedContainer).mio$data().mio$palette();
    }
}
