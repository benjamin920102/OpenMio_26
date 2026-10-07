package me.mioclient.mixin.ducks;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractSoundInstance.class})

public interface DuckAbstractSoundInstance {
    @Accessor("volume")
    void setVolume(float f);
}
