package me.mioclient.util.registry;

import java.lang.Enum;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;
import me.mioclient.api.Setting;

public class ModeValueMap<E, T> {
    public final HashMap<E, T> hashMap = new HashMap<>();
    public final Supplier<E> supplier;

    public ModeValueMap(Supplier<E> supplier) {
        this.supplier = supplier;
    }

    public ModeValueMap(Setting<E> setting) {
        Objects.requireNonNull(setting);
        this.supplier = setting::getValue;
    }

    public T getCurrent() {
        return this.hashMap.get(this.supplier.get());
    }

    public void put(E e, T t) {
        this.hashMap.put(e, t);
    }
}
