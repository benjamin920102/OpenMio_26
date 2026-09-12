package me.mioclient.api;

import me.mioclient.api.ClientAccessor;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FunctionMapper.class */
public interface ClientFunction<T, R> extends ClientAccessor {
    R selectTarget(T t);
}
