package me.mioclient.ducks;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/DuckTextColor.class */
public interface DuckTextColor {
    @Nullable
    Supplier<Integer> getSupplier();

    void setSupplier(@Nullable Supplier<Integer> supplier);
}
