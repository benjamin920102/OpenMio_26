package me.mioclient.ducks;

import java.util.function.Supplier;
import net.minecraft.network.chat.Style;

public interface DuckStyle {
    Style mio$withColor(Supplier<Integer> supplier);
}
