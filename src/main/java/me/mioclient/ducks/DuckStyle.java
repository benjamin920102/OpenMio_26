package me.mioclient.ducks;

import java.util.function.Supplier;
import net.minecraft.text.Style;

public interface DuckStyle {
    Style mio$withColor(Supplier<Integer> supplier);
}
