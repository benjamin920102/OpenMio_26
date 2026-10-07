package me.mioclient.mixin;

import java.util.function.Supplier;
import me.mioclient.ducks.DuckStyle;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import me.mioclient.ducks.DuckTextColor;

@Mixin({Style.class})

public class MixinStyle implements DuckStyle {
    @Override // me.mioclient.mixin.ducks.StyleMixinInterface
    @Unique
    public Style mio$withColor(Supplier<Integer> supplier) {
        Style style = (Style)(Object) this;
        if (style.getColor() != null) {
            ((me.mioclient.ducks.DuckTextColor)(Object) style.getColor()).setSupplier(supplier);
        }
        return style;
    }
}
