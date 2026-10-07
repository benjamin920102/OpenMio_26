package me.mioclient.mixin;

import me.mioclient.ducks.DuckSimpleOption;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({OptionInstance.class})

public class MixinSimpleOption<T> implements DuckSimpleOption<T> {

    @Shadow
    T value;

    @Override // me.mioclient.ducks.DuckSimpleOption
    public void forceSetValue(T t) {
        this.value = t;
    }
}
