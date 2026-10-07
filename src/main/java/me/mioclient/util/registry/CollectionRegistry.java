package me.mioclient.util.registry;

import java.util.Collection;

public interface CollectionRegistry<E, T extends Collection<E>> {
    T getRegistry();

    boolean register(E e);

    boolean unregister(E e);
}
