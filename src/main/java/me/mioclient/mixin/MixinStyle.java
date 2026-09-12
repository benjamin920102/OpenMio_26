package me.mioclient.mixin;

import java.util.function.Supplier;
import me.mioclient.ducks.DuckStyle;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import me.mioclient.ducks.DuckTextColor;

/* compiled from: 0.java */
@Mixin({Style.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinStyle.class */
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
