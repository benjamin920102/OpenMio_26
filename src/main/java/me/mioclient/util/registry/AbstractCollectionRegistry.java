package me.mioclient.util.registry;

import java.util.Collection;
import java.util.Optional;

public abstract class AbstractCollectionRegistry<E, T extends Collection<E>> implements CollectionRegistry<E, T> {
    public final T registry;

    public AbstractCollectionRegistry(T t) {
        this.registry = t;
    }

    public Optional<E> findFirst(java.util.function.Predicate<E> predicate) {
        for (E e : this.registry) {
            if (predicate.test(e)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public E findFirstMatch(java.util.function.Predicate<E> predicate) {
        for (E e : this.registry) {
            if (predicate.test(e)) {
                return e;
            }
        }
        return null;
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    public T getRegistry() {
        return this.registry;
    }
}
