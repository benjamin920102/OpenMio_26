package me.mioclient.util.registry;

import java.util.Collection;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/CollectionBasedRegistry.class */
public interface CollectionRegistry<E, T extends Collection<E>> {
    T getRegistry();

    boolean register(E e);

    boolean unregister(E e);
}
