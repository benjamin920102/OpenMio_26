package me.mioclient.ducks;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public interface DuckTextColor {
    @Nullable
    Supplier<Integer> getSupplier();

    void setSupplier(@Nullable Supplier<Integer> supplier);
}
