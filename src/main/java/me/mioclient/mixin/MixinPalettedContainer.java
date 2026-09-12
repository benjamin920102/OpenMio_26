package me.mioclient.mixin;

import me.mioclient.ducks.DuckPalettedContainer;
import me.mioclient.ducks.DuckPalettedContainerData;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/* compiled from: 0.java */
@Mixin({PalettedContainer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinPalettedContainer.class */
public class MixinPalettedContainer<T> implements DuckPalettedContainer<T> {

    @Shadow
    private volatile PalettedContainer.Data<T> field_34560;

    @Override // me.mioclient.ducks.DuckPalettedContainer
    public DuckPalettedContainerData<T> mio$data() {
        return (DuckPalettedContainerData<T>)(Object) this.field_34560;
    }
}
