package me.mioclient.mixin;

import me.mioclient.ducks.DuckPalettedContainer;
import me.mioclient.ducks.DuckPalettedContainerData;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({PalettedContainer.class})

public class MixinPalettedContainer<T> implements DuckPalettedContainer<T> {

    @Override // me.mioclient.ducks.DuckPalettedContainer
    public DuckPalettedContainerData<T> mio$data() {
        try {
            java.lang.reflect.Field f = PalettedContainer.class.getDeclaredField("data");
            f.setAccessible(true);
            return (DuckPalettedContainerData<T>) f.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
