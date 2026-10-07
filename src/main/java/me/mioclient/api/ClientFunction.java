package me.mioclient.api;

import me.mioclient.api.ClientAccessor;

public interface ClientFunction<T, R> extends ClientAccessor {
    R selectTarget(T t);
}
